package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@Component
@AllArgsConstructor
public class ItemMapper {

    public ItemDto itemToItemDto(Item item, UserDto owner) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .requestId(item.getRequestId())
                .build();
    }

    public Item itemDtoToItem(ItemDto itemDto, User owner) {
        if (itemDto.getId() == 0) {
            return Item.builder()
                    .name(itemDto.getName())
                    .description(itemDto.getDescription())
                    .available(itemDto.isAvailable())
                    .owner(owner)
                    .requestId(itemDto.getRequestId())
                    .build();
        } else {
            return Item.builder()
                    .id(itemDto.getId())
                    .name(itemDto.getName())
                    .description(itemDto.getDescription())
                    .available(itemDto.isAvailable())
                    .owner(owner)
                    .requestId(itemDto.getRequestId())
                    .build();
        }
    }

    public ItemDto itemToItemDtoWithBookingAndComments(Item item, UserDto owner, ItemBookingDto lastBooking,
                                                       ItemBookingDto nextBooking, Set<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .requestId(item.getRequestId())
                .comments(comments)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public ItemDto itemToItemDtoWithBookings(Item item, UserDto owner, ItemBookingDto lastBooking,
                                             ItemBookingDto nextBooking) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .requestId(item.getRequestId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public ItemDto itemToItemDtoWithComments(Item item, UserDto owner, Set<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .requestId(item.getRequestId())
                .comments(comments)
                .build();
    }
}

