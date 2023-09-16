package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwnerIdOrderByIdDesc(Long ownerId);

    List<Booking> findByBookerIdOrderByIdDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusAndEndBeforeOrderByIdDesc(Long bookerId, BookingStatus status,
                                                                   LocalDateTime time);

    List<Booking> findByItemOwnerIdAndEndAfterAndStartBeforeOrderByStartDesc(Long owner, LocalDateTime end,
                                                                             LocalDateTime start);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(Long bookerId, LocalDateTime end,
                                                                          LocalDateTime start);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemIdAndStartAfterAndStatusOrderByStartAsc(Long id, LocalDateTime start, BookingStatus status);

    List<Booking> findByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long id, LocalDateTime end, BookingStatus status);
}
