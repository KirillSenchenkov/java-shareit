package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    @NotBlank(groups = {Create.class, Update.class})
    private String text;

    private Item item;

    private User author;

    private LocalDateTime created = LocalDateTime.now();

    private String authorName;
}
