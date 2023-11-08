package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "description", length = 200)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @Column(name = "owner_id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private int ownerId;
    @Column(name = "request_id")
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
