package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public ItemDto itemToItemDto(Item item) {
        return new ItemDto(item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item ItemDtoToItem(Long id, ItemDto itemDto, Long ownerId) {
        return new Item(id, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), ownerId);
    }

    public List<ItemDto> ItemToItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}
