package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public final class CommentDto {
    private final Long id;
    private final String text;
    private final Long authorId;
    private final String authorName;
    private final LocalDateTime created;
}
