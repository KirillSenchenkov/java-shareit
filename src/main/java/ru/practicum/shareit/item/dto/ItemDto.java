package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemDto {

    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 200, message = "максимальная длина описания - 200 символов", groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
