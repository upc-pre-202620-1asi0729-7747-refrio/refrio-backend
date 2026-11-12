package com.refrio.platform.operations.application;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.inventory.domain.services.FefoPolicy;
import com.refrio.platform.operations.domain.services.ShipmentPolicy;
import com.refrio.platform.operations.application.ports.RecipientIdentityPort;
import com.refrio.platform.operations.domain.model.aggregates.*;
import com.refrio.platform.operations.domain.model.repositories.ShipmentRepository;
import com.refrio.platform.operations.domain.model.valueobjects.ShipmentRoute;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.inventory.domain.model.repositories.*;
import com.refrio.platform.alerting.application.AlertingApplicationService;
import com.refrio.platform.events.application.OperationalEventPublisher;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.*;
@Service
public class OperationsApplicationService {
 private static final List<String> RESOURCES=List.of("inventory","bodeguero-inventory","batches","shipments","clients","suppliers","alerts","incidents","losses","telemetry","events");
 private final DocumentRepository store;private final UnitOfWorkLock mutex;
 private final ShipmentRepository shipmentRepository;
 private final RecipientIdentityPort recipientIdentities;
 private final StockBatchRepository batchRepository;
 private final InventoryProductRepository productRepository;
 private final AlertingApplicationService alerting;
 private final OperationalEventPublisher eventPublisher;
 public OperationsApplicationService(DocumentRepository store,UnitOfWorkLock mutex,
         ShipmentRepository shipmentRepository,RecipientIdentityPort recipientIdentities,
         StockBatchRepository batchRepository,InventoryProductRepository productRepository,
         AlertingApplicationService alerting,
         OperationalEventPublisher eventPublisher){
  this.store=store;this.mutex=mutex;this.shipmentRepository=shipmentRepository;this.recipientIdentities=recipientIdentities;
  this.batchRepository=batchRepository;this.productRepository=productRepository;
  this.alerting=alerting;this.eventPublisher=eventPublisher;
 }
 @Transactional(readOnly=true) public ObjectNode snapshot(AuthenticatedUser user){
  ObjectNode data=store.object();for(String key:RESOURCES){ArrayNode list=store.all(key);if(!user.supervisor())list=visible(key,list,user);data.set(key,list);}return data;
 }
 public ArrayNode visible(String key,ArrayNode data,AuthenticatedUser user){
  if(user.supervisor())return data;
  ArrayNode result=store.array();
  for(JsonNode x:data){String audience=x.path("audience").asText("Supervisor");
   boolean allowed=switch(key){
    case "inventory","suppliers","clients"->false;
    case "shipments"->String.valueOf(user.id()).equals(x.path("recipientId").asText());
    case "bodeguero-inventory"->true;
    case "batches","losses","telemetry","events"->"Minorista".equals(audience)||"ALL".equals(audience);
    case "alerts","incidents"->"Minorista".equals(audience)||"ALL".equals(audience)||user.username().equals(x.path("actor").asText());
    default->false;
   };if(allowed)result.add(x);
  }return result;
 }
 @Transactional public ObjectNode execute(String command,ArrayNode args,AuthenticatedUser user){
  mutex.acquire();
  JsonNode result=switch(command){
   case "saveProduct"->saveProduct(arg(args,0),arg(args,1),user);
   case "registerBatch"->registerBatch(arg(args,0),user);
   case "createShipment"->createShipment(arg(args,0),user);
   case "advanceShipment"->advanceShipment(text(arg(args,0)),user);
   case "receiveShipment"->receiveShipment(text(arg(args,0)),text(arg(args,1)),arg(args,2),text(arg(args,3)),user);
   case "recordLoss"->recordLoss(text(arg(args,0)),integer(arg(args,1)),text(arg(args,2)),user);
   case "recordTemperature"->recordTemperature(text(arg(args,0)),number(arg(args,1)),user);
   case "updateIncident"->updateIncident(args,user);
   default->throw new DomainException("Operación desconocida: "+command);
  };
  ObjectNode response=store.object();response.set("result",result==null?NullNode.getInstance():result);response.set("data",snapshot(user));return response;
 }
 private JsonNode saveProduct(JsonNode input,JsonNode existingId,AuthenticatedUser actor){
  ObjectNode product=store.copy(input);String id=existingId.isNull()?"":text(existingId);String table=actor.supervisor()?"inventory":"bodeguero-inventory";
  if(id.isBlank())id="PRD-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
  String name=text(product.path("name"));if(name.isBlank())throw new DomainException("El nombre del producto es obligatorio");
  if(!existingId.isNull()&&!store.find(table,id).isPresent())throw new DomainException("Producto inexistente");
  product.put("id",id);if(!product.has("stock"))product.put("stock",existingId.isNull()?"0":store.get(table,id).path("stock").asText("0"));
  if(!product.has("temp"))product.put("temp","4°C");if(!product.has("exp"))product.put("exp","");
  if(!product.has("warehouse"))product.put("warehouse",actor.supervisor()?"Almacén principal":"Bodega principal");
  InventoryProduct domainProduct=new InventoryProduct(id,name,text(product.path("category")),
      text(product.path("stock")),text(product.path("temp")),text(product.path("exp")),
      text(product.path("warehouse")),product.path("status").asText(null),
      product.path("sourceProductId").asText(null));
  productRepository.save(domainProduct,actor.role());
  event("Producto guardado",id,"Inventory & FEFO",actor,"SUCCESS","Registro de producto",actor.role());return TextNode.valueOf(id);
 }
 private JsonNode registerBatch(JsonNode input,AuthenticatedUser actor){
  ObjectNode batch=store.copy(input);String productId=text(batch.path("productId"));String table=actor.supervisor()?"inventory":"bodeguero-inventory";
  ObjectNode product=store.get(table,productId);int quantity=integer(batch.path("quantity"));if(quantity<=0)throw new DomainException("La cantidad debe ser mayor que cero");
  String expiration=text(batch.path("expirationDate"));try{LocalDate.parse(expiration);}catch(Exception e){throw new DomainException("La fecha de vencimiento debe tener formato YYYY-MM-DD");}
  String id=batch.path("id").asText("");if(id.isBlank())id="BT-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
  if(store.find("batches",id).isPresent())throw new DomainException("El lote ya existe");
  batch.put("id",id);batch.put("audience",actor.role());batch.put("status",batch.path("status").asText("Disponible"));
  batch.put("receptionDate",batch.path("receptionDate").asText(LocalDate.now(ZoneId.of("America/Lima")).toString()));
  StockBatch newBatch=new StockBatch(id,productId,quantity,batch.path("weight").asText(null),
      batch.path("receptionDate").asText(),expiration,batch.path("supplier").asText(null),
      batch.path("location").asText(null),batch.path("status").asText(),actor.role(),batch.path("notes").asText(null));
  batchRepository.save(newBatch);
  InventoryProduct current=productRepository.findById(productId,actor.role()).orElseThrow(()->new DomainException("Producto inexistente"));
  productRepository.save(current.withStockAdjustment(quantity),actor.role());
  event("Lote registrado",id,"Inventory & FEFO",actor,"SUCCESS","Ingreso de "+quantity+" unidades",actor.role());return NullNode.getInstance();
 }
 private JsonNode createShipment(JsonNode input,AuthenticatedUser actor){
  supervisor(actor);String clientId=text(input.path("clientId"));ObjectNode client=store.get("clients",clientId);
  String recipient=text(client.path("recipientId"));String expected=text(input.path("expectedRecipientId"));
  if(recipient.isBlank())throw new DomainException("El cliente no tiene usuario destinatario");
  if(!recipientIdentities.isExistingMinorista(recipient))throw new DomainException("El cliente debe tener un usuario Minorista registrado y válido");
  if(!expected.isBlank()&&!recipient.equals(expected))throw new DomainException("El destinatario no coincide con el cliente");
  if(!input.path("lines").isArray()||input.path("lines").isEmpty())throw new DomainException("El despacho debe tener productos");
  LocalDate today=LocalDate.now(ZoneId.of("America/Lima"));List<FefoPolicy.Batch> batches=StreamSupport.stream(store.all("batches").spliterator(),false).map(b->new FefoPolicy.Batch(text(b.path("id")),text(b.path("productId")),integer(b.path("quantity")),date(b.path("expirationDate")),text(b.path("status")),text(b.path("audience")))).toList();
  List<Allocation> plan=new ArrayList<>();Set<String> products=new HashSet<>();
  for(JsonNode line:input.path("lines")){String productId=text(line.path("productId"));if(!products.add(productId))throw new DomainException("No repitas productos en un envío");store.get("inventory",productId);plan.addAll(FefoPolicy.plan(batches,productId,integer(line.path("quantity")),today));}
  if(!input.path("scanned").isArray())throw new DomainException("Debes escanear los lotes en orden FEFO");
  List<String> scanned=new ArrayList<>();input.path("scanned").forEach(s->scanned.add(text(s)));FefoPolicy.validatePicking(plan,scanned);
  for(Allocation a:plan){
   StockBatch batch=batchRepository.findById(a.batchId()).orElseThrow(()->new DomainException("Lote inexistente"));
   batchRepository.save(batch.debit(a.quantity()));
   InventoryProduct product=productRepository.findById(a.productId(),"Supervisor").orElseThrow(()->new DomainException("Producto inexistente"));
   productRepository.save(product.withStockAdjustment(-a.quantity()),"Supervisor");
  }
  String id="ENV-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();ObjectNode shipment=store.object();shipment.put("id",id);
  String productName=products.stream().map(p->store.get("inventory",p).path("name").asText()).collect(Collectors.joining(", "));
  ShipmentRoute route=new ShipmentRoute(text(input.path("origin")),text(input.path("destination")));
  shipment.put("product",productName);shipment.put("route",route.label());
  shipment.put("temperature","4°C");shipment.put("eta",text(input.path("eta")));shipment.put("driver",text(input.path("driver")));shipment.put("vehicle",text(input.path("vehicle")));
  shipment.put("clientId",clientId);shipment.put("clientName",text(client.path("name")));shipment.put("recipientId",recipient);
  shipment.set("allocations",allocations(plan));shipment.put("status","Preparing");shipment.put("createdAt",now());
  ArrayNode timeline=store.array();timeline.add(timelineEntry("Preparing"));shipment.set("timeline",timeline);store.put("shipments",shipment);
  event("Despacho creado",id,"Operations",actor,"SUCCESS","Picking FEFO confirmado; "+plan.size()+" lotes",actor.role());return TextNode.valueOf(id);
 }
 private JsonNode advanceShipment(String id,AuthenticatedUser actor){
  supervisor(actor);
  ShipmentAggregate current=shipmentRepository.findById(id).orElseThrow(()->new DomainException("Despacho inexistente"));
  ShipmentAggregate advanced=current.advance();String next=advanced.status();
  shipmentRepository.save(advanced);
  event("Despacho actualizado",id,"Operations",actor,"SUCCESS",next,"Supervisor");return NullNode.getInstance();
 }
 private JsonNode receiveShipment(String id,String decision,JsonNode received,String notes,AuthenticatedUser actor){
  ObjectNode shipment=store.get("shipments",id);if(!actor.supervisor()&&!String.valueOf(actor.id()).equals(text(shipment.path("recipientId"))))throw new ForbiddenOperationException("El despacho pertenece a otro destinatario");
  List<Allocation> actual=toAllocations(received);
  ShipmentAggregate current=shipmentRepository.findById(id).orElseThrow(()->new DomainException("Despacho inexistente"));
  ShipmentAggregate updated=current.receive(decision,actual,notes,actor.username());
  for(Allocation a:actual){if(a.quantity()==0)continue;ObjectNode original=store.get("inventory",a.productId());
   ObjectNode product=store.find("bodeguero-inventory",a.productId()).orElseGet(()->{ObjectNode p=original.deepCopy();p.put("stock","0");p.put("warehouse","Bodega destino");return p;});
   increaseStock(product,a.quantity());store.put("bodeguero-inventory",product);
   String batchId="BT-R-"+a.batchId();ObjectNode batch=store.find("batches",batchId).orElseGet(()->{ObjectNode b=store.get("batches",a.batchId()).deepCopy();b.put("id",batchId);b.put("audience","Minorista");b.put("quantity",0);b.put("location","Bodega destino");return b;});
   batch.put("quantity",integer(batch.path("quantity"))+a.quantity());store.put("batches",batch);
  }
  shipmentRepository.save(updated);
  event("Recepción registrada",id,"Traceability & Certification",actor,"SUCCESS",decision,"ALL");return NullNode.getInstance();
 }
 private JsonNode recordLoss(String batchId,int quantity,String cause,AuthenticatedUser actor){
  if(quantity<=0||cause.isBlank())throw new DomainException("Indica cantidad y causa de la merma");ObjectNode batch=store.get("batches",batchId);
  if(!actor.role().equals(text(batch.path("audience"))))throw new ForbiddenOperationException("El lote no pertenece a este rol");
  if(quantity>integer(batch.path("quantity")))throw new DomainException("La merma excede las existencias");
  StockBatch currentBatch=batchRepository.findById(batchId).orElseThrow(()->new DomainException("Lote inexistente"));
  batchRepository.save(currentBatch.debit(quantity));
  String productId=currentBatch.productId();
  InventoryProduct currentProduct=productRepository.findById(productId,actor.role()).orElseThrow(()->new DomainException("Producto inexistente"));
  productRepository.save(currentProduct.withStockAdjustment(-quantity),actor.role());
  ObjectNode loss=store.object();loss.put("id",UUID.randomUUID().toString());loss.put("batchId",batchId);loss.put("productId",productId);loss.put("quantity",quantity);loss.put("cause",cause);loss.put("at",now());loss.put("audience",actor.role());store.put("losses",loss);
  event("Merma registrada",batchId,"Inventory & FEFO",actor,"WARNING",quantity+" unidades. Causa: "+cause,actor.role());return NullNode.getInstance();
 }
 private JsonNode recordTemperature(String productId,double value,AuthenticatedUser actor){
  if(!Double.isFinite(value)||value < -100||value > 100)throw new DomainException("Lectura de temperatura inválida");String table=actor.supervisor()?"inventory":"bodeguero-inventory";
  InventoryProduct currentProduct=productRepository.findById(productId,actor.role()).orElseThrow(()->new DomainException("Producto inexistente"));
  productRepository.save(currentProduct.withTemperature(value),actor.role());
  ObjectNode reading=store.object();reading.put("id",UUID.randomUUID().toString());reading.put("productId",productId);reading.put("audience",actor.role());reading.put("value",value);reading.put("at",now());store.put("telemetry",reading);
  event("Lectura manual registrada",productId,"Storage & Telemetry",actor,"SUCCESS",value+" °C",actor.role());return NullNode.getInstance();
 }
 private JsonNode updateIncident(ArrayNode args,AuthenticatedUser actor){
  String alertId=text(arg(args,0)),stage=text(arg(args,1));JsonNode input=arg(args,2),subjectId=arg(args,3),audience=arg(args,4);
  String scope=audience.isNull()?actor.role():text(audience);
  if(!input.isObject())throw new DomainException("Datos de incidencia inválidos");
  var saved=alerting.update(alertId,stage,text(input.path("cause")),text(input.path("action")),
      input.hasNonNull("decision")?text(input.path("decision")):null,
      text(input.path("verification")),subjectId.isNull()?null:text(subjectId),scope,actor);
  // Optional evidence snapshot from the Angular incident workflow.
  if(arg(args,5).isObject()){
   ObjectNode doc=store.get("incidents",saved.id());doc.set("snapshot",arg(args,5));store.put("incidents",doc);
  }
  event("Incidencia "+saved.status(),alertId,"Alerting",actor,"SUCCESS",saved.action()==null?"":saved.action(),scope);
  return NullNode.getInstance();
 }
 private void increaseStock(ObjectNode product,int adjustment){
  int stock;try{stock=Integer.parseInt(text(product.path("stock")));}catch(Exception ex){throw new DomainException("Stock del producto no es un número entero válido");}
  if(stock+adjustment<0)throw new DomainException("Stock insuficiente para la operación");product.put("stock",String.valueOf(stock+adjustment));
 }
 private void event(String name,String id,String context,AuthenticatedUser actor,String outcome,String description,String audience){
  eventPublisher.publish(name,id,context,actor,outcome,description,audience);
 }
 private static JsonNode arg(ArrayNode a,int index){return index<a.size()?a.get(index):NullNode.getInstance();}
 private static String text(JsonNode x){return x.isMissingNode()||x.isNull()?"":x.asText("");}
 private static int integer(JsonNode x){if(x.isIntegralNumber()&&x.canConvertToInt())return x.intValue();try{String s=text(x);if(!s.matches("-?[0-9]+"))throw new Exception();return Integer.parseInt(s);}catch(Exception e){throw new DomainException("Se necesita un número entero válido");}}
 private static double number(JsonNode x){try{return Double.parseDouble(text(x));}catch(Exception e){throw new DomainException("Se necesita un número válido");}}
 private static LocalDate date(JsonNode x){try{return LocalDate.parse(text(x));}catch(Exception e){return null;}}
 private static String now(){return Instant.now().toString();}
 private static void supervisor(AuthenticatedUser actor){if(!actor.supervisor())throw new ForbiddenOperationException("Esta operación requiere rol Supervisor");}
 private ObjectNode timelineEntry(String status){ObjectNode t=store.object();t.put("status",status);t.put("at",now());return t;}
 private ArrayNode allocations(List<Allocation> values){ArrayNode out=store.array();values.forEach(a->{ObjectNode n=store.object();n.put("batchId",a.batchId());n.put("productId",a.productId());n.put("quantity",a.quantity());out.add(n);});return out;}
 private List<Allocation> toAllocations(JsonNode nodes){if(!nodes.isArray())throw new DomainException("Se esperaba un arreglo de lotes");List<Allocation> a=new ArrayList<>();for(JsonNode n:nodes)a.add(new Allocation(text(n.path("batchId")),text(n.path("productId")),integer(n.path("quantity"))));return a;}
}
