package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithOfferDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestDto requestDto) {
        return itemRequestService.createRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithOfferDto getTargetRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long requestId) {
        return itemRequestService.getTargetRequest(userId, requestId);
    }

    @GetMapping()
    public List<ItemRequestWithOfferDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithOfferDto> getRequestsPageable(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestService.getPageableRequests(userId, pageable);
    }
}
