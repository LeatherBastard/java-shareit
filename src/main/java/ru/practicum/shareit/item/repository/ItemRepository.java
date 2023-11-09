package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {


    List<Item> findAllByOwner(User owner);


    @Query(
            value = "select * " +
                    "from items " +
                    "where (lower(name) ilike %:text% " +
                    "OR lower(description) ilike %:text%) " +
                    "AND available=true"
            ,
            nativeQuery = true
    )
    List<Item> findAllByText(@Param("text") String text);


}
