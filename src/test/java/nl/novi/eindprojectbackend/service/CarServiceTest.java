package nl.novi.eindprojectbackend.service;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.repositories.UserRepository;
import nl.novi.eindprojectbackend.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("user1");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setCarType("Sedan");
        testCar.setOwner(testUser);
    }

    @Test
    void testGetCarById_ShouldReturnCar_WhenCarExists() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        Optional<Car> result = carService.getCarById(1L);

        assertTrue(result.isPresent());
        assertEquals("Sedan", result.get().getCarType());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCarById_ShouldReturnEmpty_WhenCarDoesNotExist() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Car> result = carService.getCarById(1L);

        assertFalse(result.isPresent());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteCar_ShouldDeleteCar_WhenCarExists() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        doNothing().when(carRepository).delete(any(Car.class));

        carService.deleteCar(1L);

        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).delete(testCar);
    }

    @Test
    void testDeleteCar_ShouldThrowException_WhenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carService.deleteCar(1L));

        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testAddCar_ShouldSaveCar_WhenUserExists() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car result = carService.addCar(testCar, "user1");

        assertNotNull(result);
        assertEquals("Sedan", result.getCarType());
        assertEquals(testUser, result.getOwner());

        verify(userRepository, times(1)).findById("user1");
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void testAddCar_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carService.addCar(new Car(), "nonexistent"));

        verify(userRepository, times(1)).findById("nonexistent");
    }

    @Test
    void testUpdateCar_ShouldUpdateCar_WhenCarExists() {
        Car updatedCar = new Car();
        updatedCar.setCarType("SUV");
        updatedCar.setOwner(testUser);

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        Car result = carService.updateCar(1L, updatedCar);

        assertEquals("SUV", result.getCarType());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void testUpdateCar_ShouldThrowException_WhenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carService.updateCar(1L, new Car()));

        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testPatchCar_ShouldUpdateCarFields() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Map<String, Object> updates = Map.of("carType", "Coupe");

        Car patchedCar = carService.patchCar(1L, updates);

        assertEquals("Coupe", patchedCar.getCarType());

        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void testGetAllCars_ShouldReturnListOfCars() {
        when(carRepository.findAll()).thenReturn(List.of(testCar));

        List<Car> result = carService.getAllCars();

        assertEquals(1, result.size());
        verify(carRepository, times(1)).findAll();
    }
}
