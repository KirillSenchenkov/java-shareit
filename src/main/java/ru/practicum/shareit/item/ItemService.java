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
import java.util.*;
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
        itemDto.setOwner(userMapper.usertoUserDto(userRepository.findById(ownerId).get()));
        Item item = itemRepository.save(itemMapper.itemDtotoItem(itemDto));
        return itemMapper.itemToItemDto(item);
    }

    public ItemDto updateItem(Long userId, Long id, Map<String, Object> updates) {
        checkUserExists(userId);
        checkItemExists(id);
        checkItemOwnerId(userId, id);
        Item item = itemMapper.itemDtotoItem(getTargetItem(id, userId));
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
        return itemMapper.itemToItemDto(item);
    }


    public ItemDto deleteItem(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        Item item = itemRepository.findById(id).get();
        ItemDto itemDto = itemMapper.itemToItemDto(item);
        itemRepository.delete(item);
        return itemDto;
    }


    public Item getItemById(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        return itemRepository.findById(itemId).get();
    }


    public ItemDto getTargetItem(Long itemId, Long userId) {
        checkUserExists(userId);
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет отсутствует в системе");
        }
        Item item = itemRepository.findById(itemId).get();
        ItemDto itemDto = itemMapper.itemToItemDto(item);
        itemDto.setComments(commentMapper.commentsToCommentsDto(item.getItemComments()));
        if (Objects.equals(userId, item.getOwner().getId())) {
            itemDto.setLastBooking(getLastBookingForItem(itemId));
            itemDto.setNextBooking(getFutureBookingFotItem(itemId));
        }
        return itemDto;
    }


    public List<ItemDto> getItemsByOwnerId(Long ownerId, Pageable pageable) {
        List<Item> items = new ArrayList<>(itemRepository.findByOwnerIdOrderById(ownerId, pageable));
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
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

        for (ItemDto itemDto : itemsDto) {
            List<Booking> previousBooking = previousBookingsMap.get(itemDto.getId());
            if (previousBooking != null && !previousBooking.isEmpty()) {
                Booking booking = previousBooking.stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .limit(1)
                        .collect(Collectors.toList()).get(0);
                itemDto.setLastBooking(new ItemBookingDto(booking.getId(), booking.getBooker().getId()));
            } else {
                itemDto.setLastBooking(null);
            }

            List<Booking> subsequentBooking = subsequentBookingsMap.get(itemDto.getId());
            if (subsequentBooking != null && !subsequentBookings.isEmpty()) {
                Booking booking = subsequentBooking.stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .limit(1)
                        .collect(Collectors.toList()).get(0);
                itemDto.setNextBooking(new ItemBookingDto(booking.getId(), booking.getBooker().getId()));
            } else {
                itemDto.setNextBooking(null);
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

        List<ItemDto> itemsDto = itemRepository.findByNameOrDescription(query)
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

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
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore
                (itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadEntityException
                    ("Не возможно добавить комментарий к предмету, находящемуся в бронировании");
        }
        Comment comment = commentMapper.commentDtoToComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.commentToCommentDto(comment);
    }

    private ItemBookingDto getLastBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.searchByItemIdAndEndBeforeDate(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            return null;
        }
        Comparator<Booking> byDateEnd = Comparator.comparing(Booking::getEnd).reversed();
        List<Booking> bookingsSorted = bookings.stream()
                .sorted(byDateEnd)
                .limit(1)
                .collect(Collectors.toList());
        Booking booking = bookingsSorted.get(0);
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    private ItemBookingDto getFutureBookingFotItem(Long itemId) {
        List<Booking> bookings = bookingRepository.searchByItemIdAndStartAfterDate(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            return null;
        }
        Comparator<Booking> byDateStart = Comparator.comparing(Booking::getStart);
        List<Booking> bookingsOrdered = bookings.stream()
                .sorted(byDateStart)
                .limit(1)
                .collect(Collectors.toList());
        Booking booking = bookingsOrdered.get(0);
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemRepository.findByRequestIdOrderById(requestId)
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
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
