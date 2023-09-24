package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService = new ItemService(itemRepository, userRepository, bookingRepository,
            commentRepository, itemMapper, commentMapper, userMapper);

    @Test
    void createItem_StandardBehavior() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemMapper.itemDtoToItem(any(), any())).thenReturn(item);
        when(itemMapper.itemToItemDto(any(), any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.createItem(owner.getId(), itemDto);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void createItem_WrongUser() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = 1L;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> itemService.createItem(userId, createItemDto()));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь, для которого создается предмет, не найден в системе"));
    }

    @Test
    void updateItem_ItemIdNotExist() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = 1L;
        Long itemId = 1L;
        Map<String, Object> updates = Map.of("name", "ноутбук");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId, updates));
        assertThat(notFoundException.getMessage(), equalTo("Предмет отсутствует в системе"));
    }

    @Test
    void updateItem_ItemIdExists_NotOwner() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = 1L;
        Item item = createItem();
        Long itemId = item.getId();
        Map<String, Object> updates = Map.of("name", "ноутбук");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemNotOwnedByUserException itemNotOwnedByUserException
                = assertThrows(ItemNotOwnedByUserException.class, () -> itemService.updateItem(userId, itemId, updates));

        assertThat(itemNotOwnedByUserException.getMessage(),
                equalTo("Предмет не принадлежит пользователю"));
    }

    @Test
    void updateItem_ItemIdExist_ByOwner_NewName() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);

        Map<String, Object> updates = Map.of("name", "ноутбук");

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDtoUpdatedName((String) updates.get("name"));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemMapper.itemDtoToItem(any(), any())).thenReturn(item);
        when(itemMapper.itemToItemDto(any(), any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.updateItem(owner.getId(), itemDto.getId(), updates);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void updateItem_ItemIdExist_ByOwner_NewDescription() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);

        Map<String, Object> updates = Map.of("description", "10 скоростей");

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDtoUpdatedDescription((String) updates.get("description"));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemMapper.itemDtoToItem(any(), any())).thenReturn(item);
        when(itemMapper.itemToItemDto(any(), any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.updateItem(owner.getId(), itemDto.getId(), updates);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void updateItem_ItemIdExist_ByOwner_NewAvailable() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);

        Map<String, Object> updates = Map.of("available", false);

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDtoUpdatedAvailable((boolean) updates.get("available"));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemMapper.itemDtoToItem(any(), any())).thenReturn(item);
        when(itemMapper.itemToItemDto(any(), any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.updateItem(owner.getId(), itemDto.getId(), updates);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void getItem_StandardBehavior() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);

        Item item = createItem();
        ItemDto itemDto = createItemDto();
        Long itemId = itemDto.getId();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemMapper.itemToItemDto(any(), any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.getItemById(itemId);

        assertThat(expectedItemDto.getId(), equalTo(itemId));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("ультратонкий ноутбук")
                .available(true)
                .owner(createOwner())
                .requestId(3L)
                .owner(createOwner())
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("ноутбук")
                .description("ультратонкий ноутбук")
                .available(true)
                .owner(createOwnerDto())
                .requestId(3L)
                .build();
    }

    private ItemDto createItemDtoUpdatedName(String name) {
        return ItemDto.builder()
                .id(1L)
                .name(name)
                .description("ультратонкий ноутбук")
                .available(true)
                .owner(createOwnerDto())
                .requestId(3L)
                .build();
    }

    private ItemDto createItemDtoUpdatedDescription(String description) {
        return ItemDto.builder()
                .id(1L)
                .name("ноутбук")
                .description(description)
                .available(true)
                .owner(createOwnerDto())
                .requestId(3L)
                .build();
    }

    private ItemDto createItemDtoUpdatedAvailable(Boolean available) {
        return ItemDto.builder()
                .id(1L)
                .name("ноутбук")
                .description("ультратонкий ноутбук")
                .available(available)
                .owner(createOwnerDto())
                .requestId(3L)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(33L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }

    private User createOwner() {
        return User.builder()
                .id(2L)
                .name("Mariya")
                .email("Mariya@nmicrk.ru")
                .build();
    }

    private UserDto createOwnerDto() {
        return UserDto.builder()
                .id(2L)
                .name("Mariya")
                .email("Mariya@nmicrk.ru")
                .build();
    }
}
