package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {


    List<Item> findAllByOwner(User owner);


    @Query(
            "select it " +
                    "from Item as it " +
                    "where lower(it.name) like lower(?1) " +
                    "OR lower(it.description) like lower(?1)"
    )
    List<Item> findAllByText(String text);


}
