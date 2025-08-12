package com.example.hoteluiservice.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;

    // Статический метод для создания пустого ответа
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .currentPage(0)
                .totalPages(0)
                .totalElements(0)
                .pageSize(0)
                .hasNext(false)
                .hasPrevious(false)
                .isFirst(true)
                .isLast(true)
                .build();
    }

    // Статический метод для создания из данных
    public static <T> PageResponse<T> of(List<T> content, int currentPage, int totalPages,
                                         long totalElements, int pageSize) {
        return PageResponse.<T>builder()
                .content(content)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .pageSize(pageSize)
                .hasNext(currentPage + 1 < totalPages)
                .hasPrevious(currentPage > 0)
                .isFirst(currentPage == 0)
                .isLast(currentPage + 1 >= totalPages)
                .build();
    }
}