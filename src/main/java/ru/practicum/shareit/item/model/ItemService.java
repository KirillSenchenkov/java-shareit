package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        if (userStorage.getTargetUser(ownerId) == null) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        Item item = ItemMapper.ItemDtoToItem(null, itemDto, ownerId);
        itemStorage.createItem(item);
        return ItemMapper.itemToItemDto(item);
    }


}
