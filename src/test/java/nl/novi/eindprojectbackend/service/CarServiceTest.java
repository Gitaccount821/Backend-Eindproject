 package nl.novi.eindprojectbackend.service;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
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
    private PdfAttachmentRepository pdfAttachmentRepository;

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
        testCar.setRepairRequestDate("20-02-2025");
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

        assertThrows(BadRequestException.class, () -> carService.addCar(new Car(), "nonexistent"));

        verify(userRepository, times(1)).findById("nonexistent");
    }

    @Test
    void testGetAllCars_ShouldReturnListOfCars() {
        when(carRepository.findAll()).thenReturn(List.of(testCar));

        List<Car> result = carService.getAllCars();

        assertEquals(1, result.size());
        verify(carRepository, times(1)).findAll();
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
    void testGetCarById_ShouldThrowException_WhenCarDoesNotExist() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.getCarById(1L));

        verify(carRepository, times(1)).findById(1L);
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
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testUpdateCar_ShouldThrowException_WhenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.updateCar(1L, new Car()));

        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateCar_WithCarDto_ShouldUpdateCar_WhenValidDataProvided() {
        CarDto carDto = new CarDto();
        carDto.setCarType("Coupe");
        carDto.setRepairRequestDate("15-03-2025");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car result = carService.updateCar(1L, carDto);

        assertEquals("Coupe", result.getCarType());
        assertEquals("15-03-2025", result.getRepairRequestDate());

        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testUpdateCar_WithCarDto_ShouldThrowException_WhenCarTypeIsMissing() {
        CarDto carDto = new CarDto(null, null, "15-03-2025", null, null, "user1", null);

        assertThrows(BadRequestException.class, () -> carService.updateCar(1L, carDto));
    }

    @Test
    void testPatchCar_ShouldModifyFieldsAndSave() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Map<String, Object> updates = Map.of(
                "carType", "Hatchback",
                "repairRequestDate", "30-04-2025"
        );

        Car patchedCar = carService.patchCar(1L, updates);

        assertEquals("Hatchback", patchedCar.getCarType());
        assertEquals("30-04-2025", patchedCar.getRepairRequestDate());

        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testPatchCar_ShouldThrowException_WhenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.patchCar(1L, Map.of("carType", "Truck")));

        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteCar_ShouldDeleteCar_WhenCarExists() {
        PdfAttachment pdfAttachment = new PdfAttachment();
        testCar.setPdfAttachment(pdfAttachment);

        Repair repair1 = new Repair();
        Repair repair2 = new Repair();
        testCar.setRepairs(List.of(repair1, repair2));

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        doNothing().when(repairRepository).deleteAll(anyList());
        doNothing().when(pdfAttachmentRepository).delete(any(PdfAttachment.class));
        doNothing().when(carRepository).delete(any(Car.class));

        carService.deleteCar(1L);

        verify(carRepository, times(1)).findById(1L);
        verify(repairRepository, times(1)).deleteAll(anyList());
        verify(pdfAttachmentRepository, times(1)).delete(any(PdfAttachment.class));
        verify(carRepository, times(1)).delete(testCar);
    }

    @Test
    void testDeleteCar_ShouldThrowException_WhenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.deleteCar(1L));

        verify(carRepository, times(1)).findById(1L);
    }
}
