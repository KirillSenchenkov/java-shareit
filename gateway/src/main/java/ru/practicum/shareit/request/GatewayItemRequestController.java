package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class GatewayItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long requesterId,
                                                @RequestBody @Valid ItemRequestInputDto itemRequestInputDto) {
        return itemRequestClient.createItemRequest(requesterId, itemRequestInputDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(
            @RequestHeader("X-Sharer-User-Id")
            @Positive Long requesterId,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(defaultValue = "20")
            @Positive int size) {
        return itemRequestClient.getOwnerRequests(requesterId, from, size);
    }


    @GetMapping("all")
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive Long requesterId,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(defaultValue = "20")
            @Positive int size) {

        return itemRequestClient.getUserRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable @Min(1) Long requestId) {

        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
