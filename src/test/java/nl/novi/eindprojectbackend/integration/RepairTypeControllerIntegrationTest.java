package nl.novi.eindprojectbackend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RepairTypeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private String getAuthToken(String username, String password) throws Exception {
        String loginJson = """
        {
            "username": "monteur1",
            "password": "Monteur"
        }
        """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("jwt").asText();
    }

    @Test
    void testRepairTypeCRUDOperations() throws Exception {
        String token = getAuthToken("medewerker1", "Medewerker");


        String repairTypeJson = """
        {
            "name": "Brake Repair",
            "description": "Fixing brakes",
            "cost": 150.00
        }
        """;

        MvcResult createResult = mockMvc.perform(post("/api/repair-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(repairTypeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Brake Repair"))
                .andExpect(jsonPath("$.cost").value(150.00))
                .andReturn();


        String responseBody = createResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long repairTypeId = jsonNode.get("id").asLong();


        mockMvc.perform(get("/api/repair-types/" + repairTypeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Brake Repair"))
                .andExpect(jsonPath("$.cost").value(150.00));


        String updatedRepairTypeJson = """
        {
            "name": "Advanced Brake Repair",
            "description": "Brake system overhaul",
            "cost": 200.00
        }
        """;

        mockMvc.perform(put("/api/repair-types/" + repairTypeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedRepairTypeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Advanced Brake Repair"))
                .andExpect(jsonPath("$.cost").value(200.00));


        mockMvc.perform(delete("/api/repair-types/" + repairTypeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/repair-types/" + repairTypeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
