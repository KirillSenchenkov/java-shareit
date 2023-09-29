package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public final class ItemRequestInputDto {
    @NotEmpty
    private final String description;

    @JsonCreator
    public ItemRequestInputDto(@JsonProperty("description") String description) {
         this.description = description;
    }
}

