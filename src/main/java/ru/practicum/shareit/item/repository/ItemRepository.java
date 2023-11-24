package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Integer>, QuerydslPredicateExecutor<Item> {

    @Query(
            value = "select * " +
                    "from items AS i " +
                    "where i.user_id=:userId " +
                    "LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Item> findAllByOwnerFromAndLimit(@Param("userId") int userId, @Param("from") int from, @Param("size") int size);

    List<Item> findAllByOwner(User owner);

    List<Item> findAllByRequest_Id(int requestId);

    @Query(
            value = "select * " +
                    "from items " +
                    "where (lower(name) ilike %:text% " +
                    "OR lower(description) ilike %:text%) " +
                    "AND available=true " +
                    "LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Item> findAllByText(@Param("text") String text, @Param("from") int from, @Param("size") int size);
}
