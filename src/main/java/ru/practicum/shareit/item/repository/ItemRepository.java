package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Integer>, QuerydslPredicateExecutor<Item> {
    List<Item> findAllByOwner(User owner);

    List<Item> findAllByRequest_Id(int requestId);
}
