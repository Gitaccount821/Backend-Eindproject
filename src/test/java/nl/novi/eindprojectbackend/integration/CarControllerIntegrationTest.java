package nl.novi.eindprojectbackend.integration;

import nl.novi.eindprojectbackend.repositories.CarRepository;
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
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    private String jwtToken;

    @BeforeEach
    void authenticateAndGetToken() throws Exception {
        String loginJson = """
            {
                "username": "monteur1",
                "password": "Monteur"
            }
        """;

        MvcResult result = mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        jwtToken = extractToken(responseBody);
    }

    @Test
    void testCreateAndGetCar() throws Exception {
        String carJson = """
            {
                "carType": "Sedan",
                "ownerUsername": "klant1",
                "repairRequestDate": "20-02-2025"
            }
        """;

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cars/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carType").value("Sedan"))
                .andExpect(jsonPath("$.ownerUsername").value("klant1"));
    }


    private String extractToken(String responseBody) {
        return responseBody.replace("{\"jwt\":\"", "").replace("\"}", "");
    }
}
