package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.validator.AddItemValidator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;
    @NotEmpty(groups = AddItemValidator.class)
    private String name;
    @NotEmpty(groups = AddItemValidator.class)
    private String description;
    @NotNull(groups = AddItemValidator.class)
    private Boolean available;
    private Integer requestId;
}
