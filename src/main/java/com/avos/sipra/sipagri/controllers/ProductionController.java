package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.ProductionService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productions")
public class ProductionController {
    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<ProductionDTO>> findProductions(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<ProductionDTO> response = productionService.findAllPaged(pageable);

        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductionDTO>> findAll() {
        List<ProductionDTO> productions = productionService.findAll();
        if (productions == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionDTO> findById(@PathVariable long id) {
        ProductionDTO productionDTO = productionService.findOne(id);
        if (productionDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productionDTO);
    }

    @PostMapping
    public ResponseEntity<ProductionDTO> save(@RequestBody ProductionDTO productionDTO) {
        ProductionDTO production = productionService.save(productionDTO);
        if (production == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(production);
    }

    @PutMapping
    public ResponseEntity<ProductionDTO> update(@RequestBody ProductionDTO productionDTO) {
        ProductionDTO production = productionService.update(productionDTO);
        if (production == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(production);
    }

    @PatchMapping
    public ResponseEntity<ProductionDTO> patch(@RequestBody ProductionDTO productionDTO) {
        ProductionDTO production = productionService.partialUpdate(productionDTO);
        if (production == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(production);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductionDTO> delete(@PathVariable long id) {
        ProductionDTO productionDTO = productionService.findOne(id);
        if (productionDTO == null) {
            return ResponseEntity.notFound().build();
        }
        productionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
