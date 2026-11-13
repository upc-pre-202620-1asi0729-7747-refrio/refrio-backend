package com.refrio.platform.iam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class AuthenticationIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;
 @Test void signsUpAndSignsInWithoutExposingPassword() throws Exception {
  String name="u"+UUID.randomUUID().toString().substring(0,8);
  String body="{\"username\":\""+name+"\",\"email\":\""+name+"@example.org\",\"password\":\"secure-pass-123\",\"firstName\":\"Ana\",\"lastName\":\"Rios\",\"role\":\"Minorista\",\"subscriptionPlan\":\"Basico\",\"sede\":\"Lima\"}";
  mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
  String login="{\"username\":\""+name+"\",\"password\":\"secure-pass-123\"}";
  String result=mvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON).content(login)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  String token=mapper.readTree(result).path("token").asText();
  mvc.perform(get("/api/session").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.username").value(name)).andExpect(jsonPath("$.password").doesNotExist());
  mvc.perform(get("/api/session")).andExpect(status().isUnauthorized());
 }
}
