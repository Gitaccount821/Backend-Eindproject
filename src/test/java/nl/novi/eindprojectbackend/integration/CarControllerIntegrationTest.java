package nl.novi.eindprojectbackend.integration;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Test
    void testCreateAndGetCar() throws Exception {
        // Create a new car (POST)
        String carJson = """
            {
                "carType": "Sedan",
                "ownerUsername": "klant1",
                "repairRequestDate": "20-02-2025"
            }
        """;

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isOk());

        // Retrieve the car (GET)
        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carType").value("Sedan"))
                .andExpect(jsonPath("$.ownerUsername").value("klant1"));
    }
}
