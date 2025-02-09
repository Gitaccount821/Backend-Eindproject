package nl.novi.eindprojectbackend.service;

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
    void testAddRepairType_ShouldSaveAndReturnRepairType() {
        when(repairTypeRepository.save(any(RepairType.class))).thenReturn(testRepairType);

        RepairType result = repairTypeService.addRepairType(testRepairType);

        assertNotNull(result);
        assertEquals("Oil Change", result.getName());
        verify(repairTypeRepository, times(1)).save(testRepairType);
    }

    @Test
    void testGetAllRepairTypes_ShouldReturnListOfRepairTypes() {
        when(repairTypeRepository.findAll()).thenReturn(List.of(testRepairType));

        List<RepairType> result = repairTypeService.getAllRepairTypes();

        assertEquals(1, result.size());
        assertEquals("Oil Change", result.getFirst().getName());
        verify(repairTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetRepairTypeById_ShouldReturnRepairType_WhenExists() {
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));

        Optional<RepairType> result = repairTypeService.getRepairTypeById(1L);

        assertTrue(result.isPresent());
        assertEquals("Oil Change", result.get().getName());
        verify(repairTypeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRepairTypeById_ShouldReturnEmpty_WhenNotExists() {
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<RepairType> result = repairTypeService.getRepairTypeById(1L);

        assertFalse(result.isPresent());
        verify(repairTypeRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateRepairType_ShouldUpdateAndReturn_WhenExists() {
        RepairType updatedRepairType = new RepairType();
        updatedRepairType.setName("Brake Pad Change");
        updatedRepairType.setCost(120.0);
        updatedRepairType.setDescription("Replacement of brake pads");

        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));
        when(repairTypeRepository.save(any(RepairType.class))).thenReturn(updatedRepairType);

        RepairType result = repairTypeService.updateRepairType(1L, updatedRepairType);

        assertEquals("Brake Pad Change", result.getName());
        assertEquals(120.0, result.getCost());
        verify(repairTypeRepository, times(1)).findById(1L);
        verify(repairTypeRepository, times(1)).save(testRepairType);
    }

    @Test
    void testUpdateRepairType_ShouldThrowException_WhenNotExists() {
        RepairType updatedRepairType = new RepairType();
        updatedRepairType.setName("Brake Pad Change");

        when(repairTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> repairTypeService.updateRepairType(1L, updatedRepairType));

        verify(repairTypeRepository, times(1)).findById(1L);
    }

    @Test
    void testPatchRepairType_ShouldModifyFieldsAndSave() {
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.of(testRepairType));
        when(repairTypeRepository.save(any(RepairType.class))).thenReturn(testRepairType);

        Map<String, Object> updates = Map.of(
                "name", "Transmission Repair",
                "cost", 500.0
        );

        RepairType patchedRepairType = repairTypeService.patchRepairType(1L, updates);

        assertEquals("Transmission Repair", patchedRepairType.getName());
        assertEquals(500.0, patchedRepairType.getCost());

        verify(repairTypeRepository, times(1)).findById(1L);
        verify(repairTypeRepository, times(1)).save(testRepairType);
    }

    @Test
    void testPatchRepairType_ShouldThrowException_WhenNotExists() {
        when(repairTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> repairTypeService.patchRepairType(1L, Map.of("cost", 500.0)));

        verify(repairTypeRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteRepairType_ShouldCallRepositoryDeleteById() {
        doNothing().when(repairTypeRepository).deleteById(1L);

        repairTypeService.deleteRepairType(1L);

        verify(repairTypeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteRepairType_ShouldNotThrow_WhenIdDoesNotExist() {
        doNothing().when(repairTypeRepository).deleteById(999L);

        assertDoesNotThrow(() -> repairTypeService.deleteRepairType(999L));

        verify(repairTypeRepository, times(1)).deleteById(999L);
    }
}
