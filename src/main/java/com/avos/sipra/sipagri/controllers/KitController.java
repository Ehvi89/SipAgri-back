package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.KitService;
import com.avos.sipra.sipagri.services.dtos.KitDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kits")
public class KitController {
    private final KitService kitService;

    public KitController(KitService kitService) {
        this.kitService = kitService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDTO<KitDTO>> findAllPaged(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<KitDTO> response = kitService.findAllPaged(pageable);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<KitDTO>> getAll() {
        List<KitDTO> kitDTOList = kitService.findAll();
        if (kitDTOList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kitDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitDTO> getById(@PathVariable Long id) {
        KitDTO kit = kitService.findOne(id);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kit);
    }

    @PostMapping
    public ResponseEntity<KitDTO> save(@RequestBody KitDTO kitDTO) {
        KitDTO kit = kitService.save(kitDTO);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(kit);
    }

    @PutMapping
    public ResponseEntity<KitDTO> update(@RequestBody KitDTO kitDTO) {
        KitDTO kit = kitService.update(kitDTO);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(kit);
    }

    @PatchMapping
    public ResponseEntity<KitDTO> patch(@RequestBody KitDTO kitDTO) {
        KitDTO kit = kitService.partialUpdate(kitDTO);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(kit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<KitDTO> delete(@PathVariable Long id) {
        KitDTO kit = kitService.findOne(id);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        kitService.delete(id);
        return ResponseEntity.ok().build();
    }
}
