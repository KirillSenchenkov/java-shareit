package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    void createItem(Item item);

    void updateItem(Long id, Item item, Long ownerId);

    void deleteItem(Long id);

    Item getTargetItem(Long id);

    List<Item> getItemsByOwnerId(Long ownerId);

    List<Item> searchItemByText(String text);
}
