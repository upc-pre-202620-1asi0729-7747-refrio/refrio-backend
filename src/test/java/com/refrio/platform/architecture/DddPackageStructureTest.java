package com.refrio.platform.architecture;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.*;

/** Prevents accidental removal or flattening of any Refrio bounded context. */
class DddPackageStructureTest {
    private static final List<String> CONTEXTS=List.of(
        "iam","inventory","operations","alerting","traceability","analytics","publicportal","events");
    private static final Path SOURCE=Path.of("src/main/java/com/refrio/platform");
    private static final Path TESTS=Path.of("src/test/java/com/refrio/platform");

    @Test void productionContainsExactlyEightBusinessBoundariesAndShared() throws IOException {
        try(Stream<Path> stream=Files.list(SOURCE)) {
            Set<String> names=stream.filter(Files::isDirectory).map(p->p.getFileName().toString()).collect(Collectors.toSet());
            var expected=new HashSet<>(CONTEXTS);expected.add("shared");
            assertEquals(expected,names,"No debe faltar ni agregarse un bounded context");
        }
    }
    @Test void everyContextOwnsDomainApplicationInfrastructureRestAndTests() throws IOException {
        for(String name:CONTEXTS) {
            Path module=SOURCE.resolve(name);
            for(String layer:List.of("domain/model","application","infrastructure/persistence","interfaces/rest"))
                assertTrue(Files.isDirectory(module.resolve(layer)),name+" debe tener "+layer);
            assertTrue(hasJava(module.resolve("domain/model"),"aggregates"),name+": faltan agregados");
            if(!"iam".equals(name))
                assertTrue(hasJava(module.resolve("domain/model"),"repositories"),name+": faltan puertos de repositorio");
            assertTrue(hasJava(module.resolve("infrastructure"),"persistence/jpa/adapters") || "iam".equals(name),name+": falta adaptador JPA");
            assertTrue(hasJava(TESTS.resolve(name),""),name+": faltan pruebas de ese contexto");
        }
    }
    @Test void noBusinessDomainDependsOnSpringJpaJacksonOrControllers() throws IOException {
        for(String name:CONTEXTS) {
            try(Stream<Path> stream=Files.walk(SOURCE.resolve(name).resolve("domain"))) {
                for(Path file:stream.filter(x->x.toString().endsWith(".java")).toList()) {
                    String content=Files.readString(file);
                    assertFalse(content.contains("import org.springframework."),file+" imports Spring");
                    assertFalse(content.contains("import jakarta.persistence."),file+" imports JPA");
                    assertFalse(content.contains("import com.fasterxml.jackson."),file+" imports Jackson");
                    assertFalse(content.contains(".infrastructure."),file+" depends on infrastructure");
                    assertFalse(content.contains(".interfaces."),file+" depends on HTTP interfaces");
                }
            }
        }
    }
    private static boolean hasJava(Path base,String child) throws IOException {
        Path dir=child.isBlank()?base:base.resolve(child);
        if(!Files.exists(dir))return false;
        try(Stream<Path> files=Files.walk(dir)) {
            return files.anyMatch(p->p.toString().endsWith(".java"));
        }
    }
}
