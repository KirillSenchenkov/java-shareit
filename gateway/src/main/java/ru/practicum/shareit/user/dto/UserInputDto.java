package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public final class UserInputDto {

    @NotBlank
    private final String name;

    @NotBlank
    @Email
    private final String email;
}
