package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithOfferDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
class ItemRequestMapper {

    public ItemRequestDto itemRequestToRequestDto(ItemRequest itemRequest, UserDto requester) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .requester(requester)
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .build();
    }

    public ItemRequest itemRequestDtoToRequest(ItemRequestDto itemRequestDto, User requester) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requester(requester)
                .build();
    }

    public ItemRequestWithOfferDto itemRequestToRequestWithOfferDto(ItemRequest itemRequest, UserDto requester,
                                                                    List<ItemDto> items) {
        return ItemRequestWithOfferDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(requester)
                .items(items)
                .build();
    }

}
