package nl.novi.eindprojectbackend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest


@AutoConfigureMockMvc


class RepairControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddRepairAndGetRepairs() throws Exception {
        String token = getAuthToken("monteur1", "Monteur");  // 🔥 Get JWT token

        String repairJson = """
    {
        "repairTypeId": 1,
        "repairRequestDate": "25-02-2025",
        "repairDate": "27-02-2025"
    }
    """;

        mockMvc.perform(post("/api/cars/1/repairs")
                        .header("Authorization", "Bearer " + token)  // ✅ Add the JWT token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(repairJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairs").isNotEmpty());
    }

    // 🔥 Helper method to get JWT token (You may need to modify it based on your setup)
    private String getAuthToken(String username, String password) throws Exception {
        String loginJson = """
    {
        "username": "%s",
        "password": "%s"
    }
    """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        // Extract JWT from JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("jwt").asText();  // ✅ Extracts only the token
    }

}
