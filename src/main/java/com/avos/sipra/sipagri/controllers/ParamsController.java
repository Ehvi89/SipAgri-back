package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.ParamsService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ParamsDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/params")
public class ParamsController {
    private final ParamsService planterService;

    public ParamsController(ParamsService planterService) {
        this.planterService = planterService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<ParamsDTO>> getParams(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<ParamsDTO> responseDTO = planterService.findAllPaged(pageable);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ParamsDTO>> getAllParams() {
        List<ParamsDTO> paramsDTO = planterService.findAll();
        if (paramsDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paramsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParamsDTO> getParamsById(@PathVariable Long id) {
        ParamsDTO paramsDTO = planterService.findOne(id);
        if (paramsDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paramsDTO);
    }
    
    @GetMapping("/name")
    public ResponseEntity<ParamsDTO> getParamsByName(@RequestParam String name) {
        ParamsDTO paramsDTO = planterService.findByName(name);
        if (paramsDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paramsDTO);
    }

    @PostMapping
    @XSSProtected
    public ResponseEntity<ParamsDTO> createParams(@RequestBody ParamsDTO paramsDTO) {
        ParamsDTO planter = planterService.save(paramsDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(planter);
    }

    @PutMapping
    @XSSProtected
    public ResponseEntity<ParamsDTO> updateParams(@RequestBody ParamsDTO paramsDTO) {
        ParamsDTO planter = planterService.update(paramsDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    @PatchMapping
    @XSSProtected
    public ResponseEntity<ParamsDTO> patchParams(@RequestBody ParamsDTO paramsDTO) {
        ParamsDTO planter = planterService.partialUpdate(paramsDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ParamsDTO> deleteParams(@PathVariable Long id) {
        ParamsDTO paramsDTO = planterService.findOne(id);
        if (paramsDTO == null) {
            return ResponseEntity.notFound().build();
        }
        planterService.delete(id);
        return ResponseEntity.ok().build();
    }
}
