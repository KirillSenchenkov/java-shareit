package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public final class CommentInputDto {

    @NotEmpty
    private final String text;

    @JsonCreator
    public CommentInputDto(@JsonProperty("text") String text) {
        this.text = text;
    }
}
