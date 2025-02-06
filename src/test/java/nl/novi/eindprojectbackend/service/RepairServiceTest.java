package nl.novi.eindprojectbackend.service;

import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.services.RepairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepairServiceTest {

    @Mock
    private RepairRepository repairRepository;

    @InjectMocks
    private RepairService repairService;

    private Repair testRepair;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testRepair = new Repair();
        testRepair.setId(1L);
    }

    @Test
    void testAddRepair() {
        when(repairRepository.save(any(Repair.class))).thenReturn(testRepair);

        Repair result = repairService.addRepair(testRepair);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
