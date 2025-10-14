package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.PlanterService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planters")
public class PlanterController {
    private final PlanterService planterService;

    public PlanterController(PlanterService planterService) {
        this.planterService = planterService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<PlanterDTO>> getPlanters(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlanterDTO> responseDTO = planterService.findAllPaged(pageable);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/by_supervisor")
    public ResponseEntity<PaginationResponseDTO<PlanterDTO>> getPlantersBySupervisor(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam Long supervisorId)
    {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlanterDTO> responseDTO = planterService.findPlanterBySupervisor(pageable, supervisorId);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlanterDTO>> getAllPlanters() {
        List<PlanterDTO> planterDTO = planterService.findAll();
        if (planterDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planterDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanterDTO> getPlanterById(@PathVariable Long id) {
        PlanterDTO planterDTO = planterService.findOne(id);
        if (planterDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planterDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<PlanterDTO>> searchPlanters(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlanterDTO> responseDTO = planterService.findAllPagedByParams(pageable, search);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    @XSSProtected
    public ResponseEntity<PlanterDTO> createPlanter(@RequestBody PlanterDTO planterDTO) {
        PlanterDTO planter = planterService.save(planterDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(planter);
    }

    @PutMapping
    @XSSProtected
    public ResponseEntity<PlanterDTO> updatePlanter(@RequestBody PlanterDTO planterDTO) {
        PlanterDTO planter = planterService.update(planterDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    @PatchMapping
    @XSSProtected
    public ResponseEntity<PlanterDTO> patchPlanter(@RequestBody PlanterDTO planterDTO) {
        PlanterDTO planter = planterService.partialUpdate(planterDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlanterDTO> deletePlanter(@PathVariable Long id) {
        PlanterDTO planterDTO = planterService.findOne(id);
        if (planterDTO == null) {
            return ResponseEntity.notFound().build();
        }
        planterService.delete(id);
        return ResponseEntity.ok().build();
    }
}
