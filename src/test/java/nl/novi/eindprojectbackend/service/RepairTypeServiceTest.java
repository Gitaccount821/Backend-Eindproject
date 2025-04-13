package nl.novi.eindprojectbackend.service;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import nl.novi.eindprojectbackend.services.RepairTypeService;
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

class RepairTypeServiceTest {

    @Mock
    private RepairTypeRepository repairTypeRepository;

    @InjectMocks
    private RepairTypeService repairTypeService;

    private RepairType testRepairType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testRepairType = new RepairType();
        testRepairType.setId(1L);
        testRepairType.setName("Oil Change");
        testRepairType.setCost(50.0);
        testRepairType.setDescription("Basic oil change service");
    }

    @Test
    void addRepairType_ShouldSaveAndReturnRepairType() {
        // Arrange
        when(repairTypeRepository.save(any(RepairType.class))).thenReturn(testRepairType);

        // Act
        RepairType result = repairTypeService.addRepairType(testRepairType);

        // Assert
        assertNotNull(result);
        assertEquals("Oil Change", result.getName());
        verify(repairTypeRepository).save(testRepairType);
    }

    @Test
    void getAllRepairTypes_ShouldReturnListOfRepairTypes() {
        // Arrange
        when(repairTypeRepository.findAll()).thenReturn(List.of(testRepairType));

        // Act
        List<RepairType> result = repairTypeService.getAllRepairTypes();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Oil Change", result.getFirst().getName());
        verify(repairTypeRepository).findAll();
    }

    @Test
    void getRepairTypeById_ShouldReturn_WhenExists() {
        // Arrange
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));

        // Act
        RepairType result = repairTypeService.getRepairTypeById(1L);

        // Assert
        assertEquals("Oil Change", result.getName());
        verify(repairTypeRepository).findById(1L);
    }

    @Test
    void getRepairTypeById_ShouldThrow_WhenNotFound() {
        // Arrange
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> repairTypeService.getRepairTypeById(1L));
        verify(repairTypeRepository).findById(1L);
    }

    @Test
    void updateRepairType_ShouldUpdate_WhenExists() {
        // Arrange
        RepairType update = new RepairType();
        update.setName("Brake Repair");
        update.setCost(100.0);
        update.setDescription("Updated");

        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));
        when(repairTypeRepository.save(any(RepairType.class))).thenReturn(update);

        // Act
        RepairType result = repairTypeService.updateRepairType(1L, update);

        // Assert
        assertEquals("Brake Repair", result.getName());
        assertEquals(100.0, result.getCost());
        verify(repairTypeRepository).save(any(RepairType.class));
    }

    @Test
    void updateRepairType_ShouldThrow_WhenNotFound() {
        // Arrange
        RepairType update = new RepairType();
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> repairTypeService.updateRepairType(1L, update));
        verify(repairTypeRepository).findById(1L);
    }

    @Test
    void patchRepairType_ShouldUpdateFields() {
        // Arrange
        Map<String, Object> updates = Map.of(
                "name", "Transmission Repair",
                "cost", 300.0,
                "description", "Transmission service"
        );

        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));
        when(repairTypeRepository.save(any(RepairType.class))).thenReturn(testRepairType);

        // Act
        RepairType result = repairTypeService.patchRepairType(1L, updates);

        // Assert
        assertEquals("Transmission Repair", result.getName());
        assertEquals(300.0, result.getCost());
        assertEquals("Transmission service", result.getDescription());
    }

    @Test
    void patchRepairType_ShouldThrow_WhenNotFound() {
        // Arrange
        when(repairTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> repairTypeService.patchRepairType(99L, Map.of()));
    }

    @Test
    void patchRepairType_ShouldThrow_WhenCostInvalid() {
        // Arrange
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));
        Map<String, Object> updates = Map.of("cost", -100.0);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> repairTypeService.patchRepairType(1L, updates));
        verify(repairTypeRepository, never()).save(any(RepairType.class));
    }

    @Test
    void deleteRepairType_ShouldDelete_WhenExists() {
        // Arrange
        when(repairTypeRepository.existsById(1L)).thenReturn(true);
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));

        // Act
        repairTypeService.deleteRepairType(1L);

        // Assert
        verify(repairTypeRepository).delete(testRepairType);
    }



    @Test
    void deleteRepairType_ShouldThrow_WhenNotExists() {
        // Arrange
        when(repairTypeRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> repairTypeService.deleteRepairType(1L));
    }
}
