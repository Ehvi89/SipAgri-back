package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supervisors")
public class SupervisorController {
    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<SupervisorDTO>> findSupervisors(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<SupervisorDTO> response = supervisorService.findAllPaged(pageable);

        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupervisorDTO>> findAll() {
        List<SupervisorDTO> supervisors = supervisorService.findAll();
        if (supervisors == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(supervisors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupervisorDTO> findById(@PathVariable long id) {
        SupervisorDTO supervisorDTO = supervisorService.findOne(id);
        if (supervisorDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(supervisorDTO);
    }

    @PostMapping
    public ResponseEntity<SupervisorDTO> save(@RequestBody SupervisorDTO supervisorDTO) {
        SupervisorDTO supervisor = supervisorService.save(supervisorDTO);
        if (supervisor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(supervisor);
    }

    @PutMapping
    public ResponseEntity<SupervisorDTO> update(@RequestBody SupervisorDTO supervisorDTO) {
        SupervisorDTO supervisor = supervisorService.update(supervisorDTO);
        if (supervisor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(supervisor);
    }

    @PatchMapping
    public ResponseEntity<SupervisorDTO> patch(@RequestBody SupervisorDTO supervisorDTO) {
        SupervisorDTO supervisor = supervisorService.partialUpdate(supervisorDTO);
        if (supervisor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(supervisor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SupervisorDTO> delete(@PathVariable long id) {
        SupervisorDTO supervisorDTO = supervisorService.findOne(id);
        if (supervisorDTO == null) {
            return ResponseEntity.notFound().build();
        }
        supervisorService.delete(id);
        return ResponseEntity.ok().build();
    }
}
