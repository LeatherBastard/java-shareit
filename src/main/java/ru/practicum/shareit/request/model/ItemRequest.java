package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    @Column(name = "requestor_id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
