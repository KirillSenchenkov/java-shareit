package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithOfferDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest itemRequestDtoToRequest(ItemRequestDto requestDto);

    ItemRequestWithOfferDto itemRequestToRequestWithOfferDto(ItemRequest request);

    ItemRequestDto itemRequestToRequestDto(ItemRequest request);
}
