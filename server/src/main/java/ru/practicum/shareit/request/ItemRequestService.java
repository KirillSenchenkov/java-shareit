package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithOfferDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final ItemService itemService;

    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь отсутствует в системе");
        }
        User requester = userRepository.getReferenceById(userId);
        ItemRequest request = itemRequestMapper.itemRequestDtoToRequest(itemRequestDto, requester);
        request.setCreated(LocalDateTime.now());
        ItemRequest saveRequest = itemRequestRepository.save(request);
        return itemRequestMapper.itemRequestToRequestDto(saveRequest, userMapper.usertoUserDto(requester));
    }

    public ItemRequestWithOfferDto getTargetRequest(Long userId, Long requestId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Запрос не найден");
        }
        ItemRequest request = itemRequestRepository.findById(requestId).get();
        UserDto requesterDto = userMapper.usertoUserDto(userRepository.findById(userId).get());
        List<ItemDto> items = itemService.getItemsByRequestId(request.getId());
        return itemRequestMapper.itemRequestToRequestWithOfferDto(request, requesterDto, items);

    }

    public List<ItemRequestWithOfferDto> getRequests(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        UserDto requestOwner = userMapper.usertoUserDto(userRepository.findById(userId).get());
        List<ItemRequest> requests = itemRequestRepository.getAllByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsOfRequest = itemRepository.findAllByRequestIdInOrderById(requestId).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        List<ItemRequestWithOfferDto> requestWithOfferDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            if (itemsOfRequest.get(request.getId()) == null) {
                requestWithOfferDtos.add(itemRequestMapper
                        .itemRequestToRequestWithOfferDto(request,
                                requestOwner, List.of()));
            } else {
                requestWithOfferDtos.add(itemRequestMapper
                        .itemRequestToRequestWithOfferDto(request,
                                requestOwner, itemsOfRequest.get(request.getId()).stream()
                                        .map(item -> itemMapper.itemToItemDto(item, requestOwner))
                                        .collect(Collectors.toList())));
            }
        }
        return requestWithOfferDtos.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public List<ItemRequestWithOfferDto> getPageableRequests(Long userId, Pageable pageable) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        UserDto requestOwner = userMapper.usertoUserDto(userRepository.findById(userId).get());
        List<ItemRequest> requests = itemRequestRepository
                .getAllCreatedByOtherOrderByCreatedDesc(userId, pageable)
                .getContent()
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        List<Long> requestId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsOfRequest = itemRepository.findAllByRequestIdInOrderById(requestId).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        List<ItemRequestWithOfferDto> requestWithOfferDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestWithOfferDtos.add(itemRequestMapper
                    .itemRequestToRequestWithOfferDto(request,
                            requestOwner, itemsOfRequest.get(request.getId()).stream()
                                    .map(item -> itemMapper.itemToItemDto(item, requestOwner))
                                    .collect(Collectors.toList())));
        }
        return requestWithOfferDtos;
    }
}
