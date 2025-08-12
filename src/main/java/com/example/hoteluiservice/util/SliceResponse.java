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
public class SliceResponse<T> {
    private List<T> content;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;     // Главное для infinite scroll
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;

    // Статический метод для создания пустого ответа
    public static <T> SliceResponse<T> empty() {
        return SliceResponse.<T>builder()
                .content(List.of())
                .currentPage(0)
                .pageSize(0)
                .hasNext(false)
                .hasPrevious(false)
                .isFirst(true)
                .isLast(true)
                .build();
    }

    // Статический метод для создания из данных
    public static <T> SliceResponse<T> of(List<T> content, int currentPage, int pageSize,
                                          boolean hasNext) {
        return SliceResponse.<T>builder()
                .content(content)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .hasNext(hasNext)
                .hasPrevious(currentPage > 0)
                .isFirst(currentPage == 0)
                .isLast(!hasNext)
                .build();
    }
}