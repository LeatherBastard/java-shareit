package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestResponseDto {
    private Integer id;
    private String description;
    private LocalDateTime created;

}
