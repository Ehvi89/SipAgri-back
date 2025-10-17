package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.PlantationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plantations")
public class PlantationController {

    private final PlantationService plantationService;

    public PlantationController(PlantationService plantationService) {
        this.plantationService = plantationService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<PlantationDTO>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<PlantationDTO> response = plantationService.findAllPaged(pageable);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantationDTO> getPlantation(@PathVariable Long id){
        PlantationDTO plantationDTO = plantationService.findOne(id);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(plantationDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlantationDTO>> getAllPlantations(){
        List<PlantationDTO> plantationDTOs = plantationService.findAll();
        if(plantationDTOs == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(plantationDTOs);
    }

    @GetMapping("/by_supervisor")
    public ResponseEntity<PaginationResponseDTO<PlantationDTO>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam Long supervisorId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<PlantationDTO> response = plantationService.findAllPagedPlantationByPlanterSupervisor(pageable, supervisorId);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<PlantationDTO>> searchPlantations(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "false") Boolean village,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlantationDTO> responseDTO = Boolean.TRUE.equals(village) ?
                plantationService.findAllPagedByVillage(pageable, search) :
                plantationService.findAllPagedByParams(pageable, search);

        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    @XSSProtected
    public ResponseEntity<PlantationDTO> createPlantation(@RequestBody PlantationDTO dto){
        PlantationDTO plantationDTO = plantationService.save(dto);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(plantationDTO);
    }

    @PutMapping
    @XSSProtected
    public ResponseEntity<PlantationDTO> updatePlantation(@RequestBody PlantationDTO dto){
        PlantationDTO plantationDTO = plantationService.update(dto);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(plantationDTO);
    }

    @PatchMapping
    public ResponseEntity<PlantationDTO> patchPlantation(@RequestBody PlantationDTO dto){
        PlantationDTO plantationDTO = plantationService.partialUpdate(dto);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(plantationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlantationDTO> deletePlantation(@PathVariable Long id){
        PlantationDTO plantationDTO = plantationService.findOne(id);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        plantationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
