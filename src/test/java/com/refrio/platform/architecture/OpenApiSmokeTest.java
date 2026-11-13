package com.refrio.platform.architecture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class OpenApiSmokeTest {
 @Autowired MockMvc mvc;
 @Test void swaggerMetadataAndVersionedCommandsAreDocumented()throws Exception{
  mvc.perform(get("/v3/api-docs"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.info.title").value("Refrio Platform API"))
    .andExpect(jsonPath("$.paths['/api/v1/operations/shipments'].post").exists());
 }
}
