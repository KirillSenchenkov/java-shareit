package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.BadEntityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.comment.CommentMapper.commentDtoToComment;
import static ru.practicum.shareit.comment.CommentMapper.commentToCommentDto;

@Service
@AllArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;


    public Item createItem(Long ownerId, ItemDto itemDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь, для которого создается предмет, не найден в системе");
        }
        Item item = ItemMapper.itemDtoToItem(itemDto, ownerId);
        itemRepository.save(item);
        return item;
    }

    public Item updateItem(Long id, ItemDto itemDto, Long userId) {
        if ((!userRepository.existsById(userId)) || (!itemRepository.existsById(id))
                || (!Objects.equals(itemRepository.findById(id).get().getOwnerId(), userId))) {
            throw new NotFoundException("Пользователь или предмет отсутствуют в системе," +
                    " или предмет не принадлежит пользователю");
        }
        Item item = itemRepository.findById(id).get();
        return itemRepository.save(new Item(
                id,
                itemDto.getName() != null ? itemDto.getName() : item.getName(),
                itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(),
                userId,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : item.getRequestId()
        ));
    }


    public String deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        itemRepository.delete(itemRepository.getById(id));
        return String.format("Предмет с id %s удален из системы", id);
    }

    @Transactional(readOnly = true)
    public Item getItemById(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Предмет не найден");
        }
        return itemRepository.findById(itemId).get();
    }

    @Transactional(readOnly = true)
    public ItemDto getTargetItem(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        ItemDto itemDto = itemMapper.itemToItemDto(item);
        if (item.getOwnerId().equals(userId)) {
            itemDto.setLastBooking(getLastBookingForItem(itemId));
            itemDto.setNextBooking(getFutureBookingFotItem(itemId));
        }
        if (commentRepository.findByItemIdOrderByCreatedDesc(itemId) != null) {
            List<Comment> list = (commentRepository.findByItemIdOrderByCreatedDesc(itemId));
            List<CommentDto> commentDtoList = new ArrayList<>();
            for (Comment comment : list) {
                commentDtoList.add(commentToCommentDto(comment));
            }
            itemDto.setComments(commentDtoList);
        }
        return itemDto;
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        List<Item> items = new ArrayList<>(itemRepository.findByOwnerIdOrderById(ownerId));
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemsDto) {
            Long itemDtoId = itemDto.getId();
            itemDto.setLastBooking(getLastBookingForItem(itemDtoId));
            itemDto.setNextBooking(getFutureBookingFotItem(itemDtoId));
        }
        return itemsDto;
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsFoundByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String query = "%" + text.trim().toLowerCase() + "%";
        return new ArrayList<>(itemRepository.findByNameOrDescription(query)
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (commentDto.getText().equals("")) {
            throw new BadEntityException("Невозможно оставить пустой комментарий");
        }
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        List<Booking> list = bookingRepository.findByBookerIdAndStatusAndEndBeforeOrderByIdDesc(userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        boolean isItemBookingDetected = false;
        for (Booking booking : list) {
            if (booking.getItem().getId().equals(itemId)) {
                isItemBookingDetected = true;
                break;
            }
        }
        if (isItemBookingDetected) {
            Comment comment = commentDtoToComment(commentDto, userRepository.findById(userId).get(),
                    itemRepository.findById(itemId).get());
            return commentToCommentDto(commentRepository.save(comment));
        } else throw new BadEntityException("Бронь отсутствует");

    }


    private ItemBookingDto getLastBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId,
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
        List<Booking> bookings = bookingRepository.findByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
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

    @Transactional(readOnly = true)
    public Boolean findUserById(Long userId) {
        return userRepository.existsById(userId);
    }
}
