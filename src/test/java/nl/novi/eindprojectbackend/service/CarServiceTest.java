package nl.novi.eindprojectbackend.service;

import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCar = new Car();
        testCar.setId(1L);
        testCar.setCarType("Sedan");
    }

    @Test
    void testGetCarById() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        Optional<Car> result = carService.getCarById(1L);
        assertTrue(result.isPresent());
        assertEquals("Sedan", result.get().getCarType());
    }

    @Test
    void testDeleteCar() {
        doNothing().when(carRepository).deleteById(1L);

        carService.deleteCar(1L);
        verify(carRepository, times(1)).deleteById(1L);
    }
}
