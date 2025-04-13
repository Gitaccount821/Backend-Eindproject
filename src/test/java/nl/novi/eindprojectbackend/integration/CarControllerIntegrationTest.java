package nl.novi.eindprojectbackend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;

    @BeforeEach
    void authenticateAndGetToken() throws Exception {
        // Arrange
        String loginJson = """
            {
                "username": "monteur1",
                "password": "Monteur"
            }
        """;

        // Act
        MvcResult result = mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();
        jwtToken = extractToken(responseBody);
    }

    @Test
    void shouldCreateCar_WhenValidRequestIsSent() throws Exception {
        // Arrange
        String carJson = """
            {
                "carType": "Hatchback",
                "ownerUsername": "klant1",
                "repairRequestDate": "20-03-2025"
            }
        """;

        // Act
        MvcResult result = mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        assertEquals("Hatchback", json.get("carType").asText());
        assertEquals("klant1", json.get("ownerUsername").asText());
    }

    @Test
    void shouldReturnCar_WhenFetchingById() throws Exception {
        // Arrange
        String carJson = """
            {
                "carType": "SUV",
                "ownerUsername": "klant1",
                "repairRequestDate": "22-03-2025"
            }
        """;

        MvcResult postResult = mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();

        String response = postResult.getResponse().getContentAsString();
        Long carId = objectMapper.readTree(response).get("id").asLong();

        // Act
        MvcResult getResult = mockMvc.perform(get("/api/cars/" + carId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andReturn();

        // Assert
        String getResponse = getResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(getResponse);
        assertEquals("SUV", json.get("carType").asText());
        assertEquals("klant1", json.get("ownerUsername").asText());
    }

    @Test
    void shouldReturnAllCars_WhenRequested() throws Exception {
        // Arrange
        String carJson = """
            {
                "carType": "Van",
                "ownerUsername": "klant1",
                "repairRequestDate": "25-03-2025"
            }
        """;

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isOk());

        // Act
        MvcResult result = mockMvc.perform(get("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken))
                .andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        assertTrue(json.isArray());
        assertTrue(json.size() > 0);
    }

    @Test
    void shouldPatchCar_WhenValidFieldsProvided() throws Exception {
        // Arrange
        String requestBody = """
            {
                "carType": "Convertible",
                "ownerUsername": "klant1",
                "repairRequestDate": "28-03-2025"
            }
        """;

        MvcResult result = mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        String patchJson = """
            {
                "carType": "Convertible XL"
            }
        """;

        // Act
        MvcResult patchResult = mockMvc.perform(patch("/api/cars/" + id)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String patchResponse = patchResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(patchResponse);
        assertEquals("Convertible XL", json.get("carType").asText());
    }

    @Test
    void shouldDeleteCar_WhenValidIdProvided() throws Exception {
        // Arrange
        String createJson = """
            {
                "carType": "DeleteTest",
                "ownerUsername": "klant1",
                "repairRequestDate": "29-03-2025"
            }
        """;

        MvcResult createResult = mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andReturn();

        Long carId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Act
        MvcResult deleteResult = mockMvc.perform(delete("/api/cars/" + carId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andReturn();

        // Assert
        assertEquals(200, deleteResult.getResponse().getStatus());

        MvcResult checkResult = mockMvc.perform(get("/api/cars/" + carId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andReturn();

        assertEquals(404, checkResult.getResponse().getStatus());
    }

    private String extractToken(String responseBody) {
        return responseBody.replace("{\"jwt\":\"", "").replace("\"}", "");
    }
}