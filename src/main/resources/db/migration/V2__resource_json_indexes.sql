-- PostgreSQL-specific, indexed read models for the legacy Angular JSON compatibility layer.
-- No extra writes or duplicate storage: these views point at the same transactional records.
CREATE INDEX IF NOT EXISTS idx_refrio_batch_product
  ON resource_records (((payload::jsonb) ->> 'productId')) WHERE resource = 'batches';
CREATE INDEX IF NOT EXISTS idx_refrio_shipment_recipient
  ON resource_records (((payload::jsonb) ->> 'recipientId')) WHERE resource = 'shipments';
CREATE INDEX IF NOT EXISTS idx_refrio_events_aggregate
  ON resource_records (((payload::jsonb) ->> 'aggregateId')) WHERE resource = 'events';
CREATE INDEX IF NOT EXISTS idx_refrio_telemetry_product
  ON resource_records (((payload::jsonb) ->> 'productId')) WHERE resource = 'telemetry';
CREATE INDEX IF NOT EXISTS idx_refrio_certificate_shipment
  ON resource_records (((payload::jsonb) ->> 'shipmentId')) WHERE resource = 'certificates';

CREATE VIEW bc_inventory_products AS
  SELECT external_id AS product_id, resource AS inventory_scope, payload::jsonb AS details
  FROM resource_records WHERE resource IN ('inventory','bodeguero-inventory');
CREATE VIEW bc_inventory_batches AS
  SELECT external_id AS batch_id, (payload::jsonb ->> 'productId') AS product_id, payload::jsonb AS details
  FROM resource_records WHERE resource = 'batches';
CREATE VIEW bc_operations_shipments AS
  SELECT external_id AS shipment_id, (payload::jsonb ->> 'recipientId') AS recipient_id, payload::jsonb AS details
  FROM resource_records WHERE resource = 'shipments';
CREATE VIEW bc_alerting_incidents AS
  SELECT external_id AS incident_id, payload::jsonb AS details
  FROM resource_records WHERE resource = 'incidents';
CREATE VIEW bc_traceability_certificates AS
  SELECT external_id AS certificate_id, (payload::jsonb ->> 'shipmentId') AS shipment_id, payload::jsonb AS details
  FROM resource_records WHERE resource = 'certificates';
CREATE VIEW bc_publicportal_leads AS
  SELECT external_id AS lead_id, payload::jsonb AS details
  FROM resource_records WHERE resource = 'leads';
CREATE VIEW bc_events_operational_events AS
  SELECT external_id AS event_id, (payload::jsonb ->> 'aggregateId') AS aggregate_id, payload::jsonb AS details
  FROM resource_records WHERE resource = 'events';
CREATE VIEW bc_analytics_snapshot AS
  SELECT (SELECT COUNT(*) FROM resource_records WHERE resource IN ('inventory','bodeguero-inventory')) AS product_records,
         (SELECT COUNT(*) FROM resource_records WHERE resource = 'shipments') AS shipment_records,
         (SELECT COUNT(*) FROM resource_records WHERE resource = 'losses') AS loss_records;
