package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {


    private final ItemService itemService;

    @PostMapping
    public Item creteItem(@RequestBody @Validated(Create.class) ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable Long itemId,
                           @RequestBody @Validated(Update.class) ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.updateItem(itemId, itemDto, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public String deleteItem(@PathVariable Long itemId) {
        return itemService.deleteItem(itemId);
    }

    @GetMapping
    public List<Item> getItemByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/{itemId}")
    public Item getTargetItem(@PathVariable Long itemId) {
        return itemService.getTargetItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> getItemsFoundByText(@RequestParam String text) {
        return itemService.getItemsFoundByText(text);
    }

}
