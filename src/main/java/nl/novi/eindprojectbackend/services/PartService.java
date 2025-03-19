package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.mappers.PartMapper;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.repositories.PartRepository;
import nl.novi.eindprojectbackend.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part getPartEntityById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));
    }

    public PartDetailDto getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));
        return PartMapper.toDto(part);
    }

    public PartDetailDto addPart(PartDetailDto dto) {
        Part part = PartMapper.toEntity(dto);
        Part savedPart = partRepository.save(part);
        return PartMapper.toDto(savedPart);
    }

    public List<PartDetailDto> getAllParts() {
        return partRepository.findAll()
                .stream()
                .map(PartMapper::toDto)
                .collect(Collectors.toList());
    }

    public PartDetailDto updatePart(Long id, PartDetailDto dto) {
        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        existingPart.setName(dto.getName());
        existingPart.setPrice(dto.getPrice());
        existingPart.setStock(dto.getStock());

        Part savedPart = partRepository.save(existingPart);
        return PartMapper.toDto(savedPart);
    }

    public PartDetailDto patchPart(Long id, Map<String, Object> updates) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        validatePartPatch(updates);

        if (updates.containsKey("name")) {
            part.setName((String) updates.get("name"));
        }

        if (updates.containsKey("price")) {
            part.setPrice(((Number) updates.get("price")).doubleValue());
        }

        if (updates.containsKey("stock")) {
            part.setStock(((Number) updates.get("stock")).intValue());
        }

        Part savedPart = partRepository.save(part);
        return PartMapper.toDto(savedPart);
    }

    public void deletePart(Long id) {
        if (!partRepository.existsById(id)) {
            throw new RecordNotFoundException("Part", id);
        }
        partRepository.deleteById(id);
    }

    private void validatePartPatch(Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            throw new BadRequestException("No data provided for update.");
        }

        if (updates.containsKey("name")) {
            Object nameObj = updates.get("name");
            if (!(nameObj instanceof String)) {
                throw new BadRequestException("Part name must be a string.");
            }
            ValidationUtils.validateNotEmpty((String) nameObj, "Part name");
        }

        if (updates.containsKey("price")) {
            ValidationUtils.validatePositiveNumber(updates.get("price"), "Part price");
        }

        if (updates.containsKey("stock")) {
            ValidationUtils.validateNonNegativeNumber(updates.get("stock"), "Part stock");
        }
    }
}
