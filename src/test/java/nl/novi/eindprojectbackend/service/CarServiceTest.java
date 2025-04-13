package nl.novi.eindprojectbackend.service;

import nl.novi.eindprojectbackend.dtos.CarDto;
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

    private void mockSecurityContext(String username) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(username);

        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }


    @Mock private CarRepository carRepository;
    @Mock private UserRepository userRepository;
    @Mock private RepairRepository repairRepository;
    @Mock private PdfAttachmentRepository pdfAttachmentRepository;

    @InjectMocks private CarService carService;

    private Car testCar;
    private User testUser;
    private CarDto testCarDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("user1");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setCarType("Sedan");
        testCar.setRepairRequestDate("20-02-2025");
        testCar.setOwner(testUser);

        testCarDto = new CarDto();
        testCarDto.setCarType("Sedan");
        testCarDto.setRepairRequestDate("20-02-2025");
        testCarDto.setOwnerUsername("user1");
    }

    @Test
    void addCar_shouldReturnSavedCarDto_whenUserExists() {
        // Arrange
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        CarDto result = carService.addCar(testCarDto);

        // Assert
        assertEquals("Sedan", result.getCarType());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void addCar_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById("userX")).thenReturn(Optional.empty());
        testCarDto.setOwnerUsername("userX");

        assertThrows(RecordNotFoundException.class, () -> carService.addCar(testCarDto));
    }

    @Test
    void getCarById_shouldReturnCarDto_whenCarExists() {
        // Arrange
        mockSecurityContext("user1");
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act
        CarDto result = carService.getCarById(1L);

        // Assert
        assertEquals("Sedan", result.getCarType());
    }

    @Test
    void getCarById_shouldThrowException_whenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.getCarById(1L));
    }

    @Test
    void updateCar_shouldUpdateAndReturnDto_whenCarExists() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        CarDto result = carService.updateCar(1L, testCarDto);

        assertEquals("Sedan", result.getCarType());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void updateCar_shouldThrowException_whenCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.updateCar(1L, testCarDto));
    }

    @Test
    void patchCar_shouldUpdateFieldsAndReturnDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Map<String, Object> updates = Map.of(
                "carType", "Hatchback",
                "repairRequestDate", "25-03-2025",
                "ownerUsername", "user1"
        );

        CarDto result = carService.patchCar(1L, updates);

        assertEquals("Hatchback", result.getCarType());
        assertEquals("25-03-2025", result.getRepairRequestDate());
    }

    @Test
    void patchCar_shouldThrowException_whenCarNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.patchCar(99L, Map.of("carType", "Van")));
    }

    @Test
    void deleteCar_shouldDeleteCarAndAttachments() {
        PdfAttachment pdf = new PdfAttachment();
        testCar.setPdfAttachment(pdf);
        testCar.setRepairs(List.of(new Repair()));

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        carService.deleteCar(1L);

        verify(repairRepository).deleteAll(anyList());
        verify(pdfAttachmentRepository).delete(any(PdfAttachment.class));
        verify(carRepository).delete(testCar);
    }

    @Test
    void deleteCar_shouldThrowException_whenCarNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> carService.deleteCar(99L));
    }
}
