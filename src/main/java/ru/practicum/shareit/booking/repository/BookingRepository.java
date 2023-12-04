package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

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

    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.booker_id=:bookerId " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllByUser(@Param("bookerId") int bookerId, @Param("from") int from, @Param("size") int size);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.booker_id=:bookerId AND bk.status=:status " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllByUserAndStatus(@Param("bookerId") int bookerId, @Param("status") String status, @Param("from") int from, @Param("size") int size);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id=:itemId " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllByItemId(@Param("itemId") int itemId, @Param("from") int from, @Param("size") int size);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.booker_id=:bookerId and  CAST(bk.start_date AS DATE) >= CURRENT_DATE " +
            "order by bk.start_date desc " +
            "LIMIT :size OFFSET :from ", nativeQuery = true
    )
    List<Booking> findAllFutureBookingsByUser(@Param("bookerId") int bookerId, @Param("from") int from, @Param("size") int size);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.booker_id=:bookerId and bk.start_date <= CURRENT_TIMESTAMP AND bk.end_date > CURRENT_TIMESTAMP " +
            "order by bk.start_date desc " +
            "LIMIT :size OFFSET :from ", nativeQuery = true
    )
    List<Booking> findAllCurrentBookingsByUser(@Param("bookerId") int bookerId, @Param("from") int from, @Param("size") int size);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.booker_id=:bookerId AND bk.end_date < CURRENT_TIMESTAMP " +
            "order by bk.start_date desc " +
            "LIMIT :size OFFSET :from ", nativeQuery = true
    )
    List<Booking> findAllPastBookingsByUser(@Param("bookerId") int bookerId, @Param("from") int from, @Param("size") int size);


    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id IN (SELECT item_id from items AS i WHERE i.user_id= :userId) " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllByOwnerItems(@Param("userId") int userId, @Param("from") int from, @Param("size") int size);

    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id IN (SELECT item_id from items AS i WHERE i.user_id= :userId) " +
            "AND CAST(bk.start_date AS DATE) >= CURRENT_DATE " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllFutureBookingsByOwnerItems(@Param("userId") int userId, @Param("from") int from, @Param("size") int size);

    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id IN (SELECT item_id from items AS i WHERE i.user_id= :userId) " +
            "AND bk.start_date <= CURRENT_TIMESTAMP AND bk.end_date > CURRENT_TIMESTAMP " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllCurrentBookingsByOwnerItems(@Param("userId") int userId, @Param("from") int from, @Param("size") int size);

    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id IN (SELECT item_id from items AS i WHERE i.user_id= :userId) " +
            "AND bk.end_date < CURRENT_TIMESTAMP " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllPastBookingsByOwnerItems(@Param("userId") int userId, @Param("from") int from, @Param("size") int size);

    @Query(value = "select * " +
            "from bookings as bk " +
            "where bk.item_id IN (SELECT item_id from items AS i WHERE i.user_id= :userId) " +
            "AND bk.status=:status " +
            "ORDER BY bk.start_date DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<Booking> findAllBookingsByOwnerItemsAndStatus(@Param("userId") int userId, @Param("status") String status, @Param("from") int from, @Param("size") int size);
}
