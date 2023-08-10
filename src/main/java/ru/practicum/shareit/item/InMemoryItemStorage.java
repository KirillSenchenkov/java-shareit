package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private long itemId = 0;

    private final Map<Long, Item> items = new HashMap<>();
    @Override
    public void createItem(Item item) {
        itemId++;
        item.setId(itemId);
        items.put(itemId, item);
    }

    @Override
    public void updateItem(Long id, Item updatedItem, Long ownerId) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет не найден");
        }
        items.put(id, new Item(id, updatedItem.getName(),
                updatedItem.getDescription(), updatedItem.getAvailable(), ownerId));
    }

    @Override
    public void deleteItem(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет не найден");
        }
        items.remove(id);

    }

    @Override
    public Item getTargetItem(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет не найден");
        }
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemByText(String text) {
        if (text.isBlank()) {
            throw new NotFoundException("Ничего не найдено, необходимо задать текст поиска");
        }
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
