package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {


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


}
