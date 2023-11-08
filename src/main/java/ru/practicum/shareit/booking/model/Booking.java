package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @Column(name = "item_id")
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @Column(name = "booker_id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User booker;
    @Column(name = "status", nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
