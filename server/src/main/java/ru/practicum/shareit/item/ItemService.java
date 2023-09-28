package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.BadEntityException;
import ru.practicum.shareit.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;


    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        checkItemIsAvailable(itemDto);
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Пользователь, для которого создается предмет, не найден в системе");
        }
        Item item = itemMapper.itemDtoToItem(itemDto, userRepository.findById(ownerId).get());
        item.setOwner(userRepository.findById(ownerId).get());
        itemRepository.save(item);
        return itemMapper.itemToItemDto(item, userMapper.usertoUserDto(item.getOwner()));
    }

    public ItemDto updateItem(Long userId, Long id, Map<String, Object> updates) {
        checkUserExists(userId);
        checkItemExists(id);
        checkItemOwnerId(userId, id);
        Item item = itemMapper.itemDtoToItem(getTargetItem(id, userId), userRepository.getReferenceById(userId));
        if (updates.containsKey("name")) {
            item.setName(String.valueOf(updates.get("name")));
        }
        if (updates.containsKey("description")) {
            item.setDescription(String.valueOf(updates.get("description")));
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.parseBoolean(String.valueOf(updates.get("available"))));
        }
        itemRepository.save(item);
        return itemMapper.itemToItemDto(item, userMapper.usertoUserDto(item.getOwner()));
    }


    public ItemDto deleteItem(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        Item item = itemRepository.findById(id).get();
        ItemDto itemDto = itemMapper.itemToItemDto(item,
                userMapper.usertoUserDto(userRepository.findById(item.getOwner().getId())
                        .orElseThrow(() -> new NotFoundException("Пользователь отсутствует в системе"))));
        itemRepository.delete(item);
        return itemDto;
    }

    public ItemDto getItemById(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        Item item = itemRepository.findById(itemId).get();
        return itemMapper.itemToItemDto(item, userMapper.usertoUserDto(item.getOwner()));
    }

    public ItemDto getTargetItem(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет или пользователь отсутствуют в системе");
        }
        Item item = itemRepository.findById(itemId).get();
        Booking lastBooking = bookingRepository.searchFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED).orElse(null);
        Booking nextBooking = bookingRepository.searchFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED).orElse(null);
        Set<CommentDto> comments;
        if (commentRepository.findAllByItemId(itemId).isEmpty()) {
            comments = new HashSet<>();
        } else {
            comments = commentMapper.commentsToCommentsDto(commentRepository.findAllByItemId(itemId));
        }
        if (Objects.equals(userId, item.getOwner().getId())) {
            if (lastBooking != null && nextBooking != null) {
                return itemMapper.itemToItemDtoWithBookingAndComments(item, userMapper
                                .usertoUserDto(userRepository.getReferenceById(userId)),
                        new ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                        new ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId()), comments);
            } else if (lastBooking == null && nextBooking != null) {
                return itemMapper.itemToItemDtoWithBookingAndComments(item, userMapper
                                .usertoUserDto(userRepository.getReferenceById(userId)),
                        null,
                        new ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId()), comments);
            } else if (lastBooking != null) {
                return itemMapper.itemToItemDtoWithBookingAndComments(item, userMapper
                                .usertoUserDto(userRepository.getReferenceById(userId)),
                        new ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                        null, comments);
            } else {
                return itemMapper.itemToItemDtoWithBookingAndComments(item, userMapper
                                .usertoUserDto(userRepository.getReferenceById(userId)),
                        null, null, comments);
            }
        } else {
            return itemMapper.itemToItemDtoWithComments(item, userMapper.usertoUserDto(item.getOwner()), comments);
        }
    }


    public List<ItemDto> getItemsByOwnerId(Long ownerId, Pageable pageable) {
        List<Item> items = new ArrayList<>(itemRepository.findByOwnerIdOrderById(ownerId, pageable));
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Booking> previousBookings = bookingRepository
                .searchAllByItemIdInAndStartBeforeAndStatusOrderByItemIdAsc(
                        items.stream()
                                .map(Item::getId)
                                .collect(Collectors.toList()),
                        LocalDateTime.now(), BookingStatus.APPROVED
                );
        List<Booking> subsequentBookings = bookingRepository
                .searchAllByItemIdInAndStartAfterAndStatusOrderByItemIdAsc(
                        items.stream()
                                .map(Item::getId)
                                .collect(Collectors.toList()),
                        LocalDateTime.now(), BookingStatus.APPROVED
                );
        Map<Long, List<Booking>> previousBookingsMap = previousBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Booking>> subsequentBookingsMap = subsequentBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        for (Item item : items) {
            List<Booking> previousBooking = previousBookingsMap.get(item.getId());
            List<Booking> subsequentBooking = subsequentBookingsMap.get(item.getId());
            if ((previousBooking != null && !previousBooking.isEmpty())
                    && (subsequentBooking != null && !subsequentBookings.isEmpty())) {
                Booking lastBooking = previousBooking.stream()
                        .max(Comparator.comparing(Booking::getEnd))
                        .get();
                Booking nextBooking = subsequentBooking.stream()
                        .min(Comparator.comparing(Booking::getStart))
                        .get();
                itemsDto.add(itemMapper.itemToItemDtoWithBookings(item, userMapper.usertoUserDto(item.getOwner()),
                        new ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                        new ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId())));
            } else if ((previousBooking != null && !previousBooking.isEmpty())
                    && (subsequentBooking == null && subsequentBookings.isEmpty())) {
                Booking lastBooking = previousBooking.stream()
                        .max(Comparator.comparing(Booking::getEnd))
                        .get();
                itemsDto.add(itemMapper.itemToItemDtoWithBookings(item, userMapper.usertoUserDto(item.getOwner()),
                        new ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId()), null));
            } else if (subsequentBooking != null && !subsequentBookings.isEmpty()) {
                Booking nextBooking = subsequentBooking.stream()
                        .min(Comparator.comparing(Booking::getStart))
                        .get();
                itemsDto.add(itemMapper.itemToItemDtoWithBookings(item, userMapper.usertoUserDto(item.getOwner()),
                        null, new ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId())));
            } else {
                itemsDto.add(itemMapper.itemToItemDtoWithBookings(item, userMapper.usertoUserDto(item.getOwner()),
                        null, null));
            }
        }
        return itemsDto;
    }


    public List<ItemDto> getItemsFoundByText(Long userId, String keyWord, Integer from, Integer size) {
        if (keyWord.trim().isEmpty()) {
            return Collections.emptyList();
        }
        checkUserExists(userId);
        String query = "%" + keyWord.trim().toLowerCase() + "%";

        int firstId = Objects.isNull(from) ? 0 : from;

        List<Item> items = itemRepository.findByNameOrDescription(query);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.itemToItemDto(item, userMapper.usertoUserDto(item.getOwner())));
        }

        if (size != null) {
            int lastId = Math.min((from + size - 1), itemsDto.size());
            itemsDto = itemsDto.subList(firstId, lastId);
        }

        return itemsDto;
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        checkUserExists(userId);
        checkItemExists(itemId);
        checkCommentEmpty(commentDto);
        Optional<User> user = userRepository.findById(userId);
        User author = user.orElse(null);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        Item item = itemOptional.orElse(null);
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadEntityException("Не возможно добавить комментарий к предмету, находящемуся в бронировании");
        }
        Comment comment = commentMapper.commentDtoToComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.commentToCommentDto(comment);
    }

    public List<ItemDto> getItemsByRequestId(Long requestId) {
        List<Item> items = itemRepository.findByRequestIdOrderById(requestId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.itemToItemDto(item, userMapper.usertoUserDto(item.getOwner())));
        }
        return itemsDto;
    }

    private void checkUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь отсутствует в системе");
        }
    }

    private void checkItemIsAvailable(ItemDto item) {
        if (!item.isAvailable()) {
            throw new BadEntityException("Поле Available отсутствует");
        }
    }

    private void checkItemExists(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Предмет отсутствует в системе");
        }
    }

    private void checkCommentEmpty(CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new BadEntityException("Комментарий не может быть пустым");
        }
    }

    private void checkItemOwnerId(Long userId, Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Предмет отсутствует в системе");
        }
        if (!Objects.equals(userId, itemRepository.findById(id).get().getOwner().getId())) {
            throw new ItemNotOwnedByUserException("Предмет не принадлежит пользователю");
        }
    }
}
