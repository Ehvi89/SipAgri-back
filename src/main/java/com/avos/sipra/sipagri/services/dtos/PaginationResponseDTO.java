package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseDTO<T> {
    private int currentPage;

    private int totalPages;

    private int totalElements;

    private List<T> data;
}
