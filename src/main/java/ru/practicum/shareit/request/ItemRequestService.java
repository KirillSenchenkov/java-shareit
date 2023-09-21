package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithOfferDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRequestMapper itemRequestMapper;

    private final UserService userService;

    private final ItemService itemService;

    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь отсутствует в системе");
        }
        ItemRequest request = itemRequestMapper.itemRequestDtoToRequest(itemRequestDto);
        request.setRequester(userService.getTargetUser(userId));
        request.setCreated(LocalDateTime.now());
        ItemRequest saveRequest = itemRequestRepository.save(request);
        return itemRequestMapper.itemRequestToRequestDto(saveRequest);
    }

    public ItemRequestWithOfferDto getTargetRequest(Long userId, Long requestId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Запрос не найден");
        }
        ItemRequest request = itemRequestRepository.findById(requestId).get();
        ItemRequestWithOfferDto requestWithOfferDto = itemRequestMapper.itemRequestToRequestWithOfferDto(request);
        requestWithOfferDto.setItems(itemService.getItemsByRequestId(request.getId()));
        return requestWithOfferDto;
    }

    public List<ItemRequestWithOfferDto> getRequests(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<ItemRequestWithOfferDto> requests = itemRequestRepository.getAllByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(itemRequestMapper::itemRequestToRequestWithOfferDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        requests.forEach(request -> request.setItems(itemService.getItemsByRequestId(request.getId())));
        return requests.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public List<ItemRequestWithOfferDto> getPageableRequests(Long userId, Pageable pageable) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequestWithOfferDto> requests = itemRequestRepository
                .getAllCreatedByOtherOrderByCreatedDesc(userId, pageable)
                .getContent()
                .stream()
                .map(itemRequestMapper::itemRequestToRequestWithOfferDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        requests.forEach(r -> r.setItems(itemService.getItemsByRequestId(r.getId())));

        return requests.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }
}
