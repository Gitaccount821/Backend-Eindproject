package nl.novi.eindprojectbackend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = getAuthToken("monteur1", "Monteur");
    }

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
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("jwt").asText();
    }

    @Test
    void createRepairType_ShouldReturnCreatedRepairType() throws Exception {
        // Arrange
        String json = """
            {
              "name": "Turbo Engine Fix",
              "cost": 45.0,
              "description": "Advanced turbo engine repair"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/repair-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Turbo Engine Fix"))
                .andExpect(jsonPath("$.cost").value(45.0));
    }

    @Test
    void getRepairTypeById_ShouldReturnCorrectData() throws Exception {
        // Arrange
        Long id = createRepairTypeAndReturnId("Wheel Alignment");

        // Act & Assert
        mockMvc.perform(get("/api/repair-types/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wheel Alignment"));
    }

    @Test
    void updateRepairType_ShouldApplyChanges() throws Exception {
        // Arrange
        Long id = createRepairTypeAndReturnId("Engine Fix");

        String updatedJson = """
            {
              "name": "Engine Overhaul",
              "description": "Full engine rebuild",
              "cost": 500.0
            }
        """;

        // Act & Assert
        mockMvc.perform(put("/api/repair-types/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Engine Overhaul"))
                .andExpect(jsonPath("$.cost").value(500.0));
    }

    @Test
    void patchRepairType_ShouldUpdateOnlyProvidedFields() throws Exception {
        // Arrange
        Long id = createRepairTypeAndReturnId("Brake Tune");

        String patchJson = """
            {
              "cost": 120.0
            }
        """;

        // Act & Assert
        mockMvc.perform(patch("/api/repair-types/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(120.0));
    }

    @Test
    void deleteRepairType_ShouldRemoveItFromDatabase() throws Exception {
        // Arrange
        Long id = createRepairTypeAndReturnId("Temporary Type");

        // Act
        mockMvc.perform(delete("/api/repair-types/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Assert
        mockMvc.perform(get("/api/repair-types/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRepairTypes_ShouldContainCreatedItems() throws Exception {
        // Arrange
        Long idA = createRepairTypeAndReturnId("Type A");
        Long idB = createRepairTypeAndReturnId("Type B");

        // Act & Assert
        mockMvc.perform(get("/api/repair-types")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Type A')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Type B')]").exists());
    }

    private Long createRepairTypeAndReturnId(String name) throws Exception {
        String json = """
            {
              "name": "%s",
              "description": "Test description",
              "cost": 100.0
            }
        """.formatted(name);

        MvcResult result = mockMvc.perform(post("/api/repair-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
