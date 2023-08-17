package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public Item createItem(Long ownerId, ItemDto itemDto) {
        if (userStorage.getTargetUser(ownerId) == null) {
            throw new NotFoundException("Пользователь, для которого создается предмет, не найден в системе");
        }
        Item item = ItemMapper.ItemDtoToItem(null, itemDto, ownerId);
        itemStorage.createItem(item);
        return item;
    }

    public Item updateItem(Long id, ItemDto itemDto, Long ownerId) {
        itemStorage.updateItem(id,ItemMapper.ItemDtoToItem(id, itemDto, ownerId), ownerId);
        return itemStorage.getTargetItem(id);
    }

    public String deleteItem(Long id) {
        itemStorage.deleteItem(id);
        return String.format("Предмет с id %s удален из системы", id);
    }

    public Item getTargetItem(Long id) {
        return itemStorage.getTargetItem(id);
    }

    public List<Item> getItemsByOwnerId(Long ownerId) {
        return itemStorage.getItemsByOwnerId(ownerId);
    }

    public List<Item> getItemsFoundByText(String text) {
        return itemStorage.searchItemByText(text);
    }
}
