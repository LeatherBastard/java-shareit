package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> findAllByBooker_IdAndItem_IdAndStatusAndEndIsBefore(int bookerId, int itemId, BookingStatus status, LocalDateTime end);


    @Query(value = "select *  " +
            "from bookings as bk " +
            "where bk.item_id=:itemId and bk.start_date=(select max(start_date) FROM bookings as bk " +
            "where bk.item_id=:itemId and bk.start_date < CURRENT_TIMESTAMP and bk.status='APPROVED') ", nativeQuery = true)
    Optional<Booking> findLastBookingDateForItem(@Param("itemId") int itemId);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id=:itemId and bk.start_date=( select min (start_date) FROM bookings as bk " +
            "where bk.item_id=:itemId and bk.start_date >= CURRENT_TIMESTAMP and bk.status='APPROVED') ", nativeQuery = true)
    Optional<Booking> findNextBookingDateForItem(@Param("itemId") int itemId);


    List<Booking> findAllByBookerOrderByStartDesc(User booker);


    List<Booking> findAllByItemOrderByStartDesc(Item item);


    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);


    List<Booking> findAllByItemAndStatusOrderByStartDesc(Item item, BookingStatus status);

    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.booker_id=? and  CAST(bk.start_date AS DATE) >= CURRENT_DATE " +
            "order by bk.start_date desc", nativeQuery = true
    )
    List<Booking> findAllFutureBookingsByUser(int bookerId);


    List<Booking> findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(
            Integer bookerId,
            LocalDateTime start,
            LocalDateTime end);


    List<Booking> findAllByBooker_IdAndEndLessThanOrderByStartDesc(int bookerId, LocalDateTime end);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id=? and  CAST(bk.start_date AS DATE) >= CURRENT_DATE " +
            "order by bk.start_date desc", nativeQuery = true
    )
    List<Booking> findAllFutureBookingsByItem(int itemId);


    List<Booking> findAllByItem_IdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(int itemId, LocalDateTime start, LocalDateTime end);


    List<Booking> findAllByItem_IdAndEndLessThanOrderByStartDesc(int itemId, LocalDateTime end);

}
