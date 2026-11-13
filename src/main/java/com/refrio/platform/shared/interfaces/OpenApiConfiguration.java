package com.refrio.platform.shared.interfaces;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(info=@Info(title="Refrio Platform API",version="1.1.0",
    description="REST API for cold-chain inventory, FEFO, shipments, alerts, certificates and analytics."
    +" Versioned endpoints are /api/v1; /api/operations remains for the Angular compatibility contract."))
@SecurityScheme(name="bearerAuth",type=SecuritySchemeType.HTTP,in=SecuritySchemeIn.HEADER,
    scheme="bearer",bearerFormat="JWT",description="JWT returned by POST /api/session")
public class OpenApiConfiguration {}
