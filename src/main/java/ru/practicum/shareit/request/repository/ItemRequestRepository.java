package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer>, QuerydslPredicateExecutor<ItemRequest> {
    List<ItemRequest> findAllByRequestor_Id(int userId);

    @Query(value = "select *  " +
            "from requests as r " +
            "where r.user_id NOT IN(:userId) " +
            "ORDER BY r.created DESC " +
            "LIMIT :size OFFSET :from ", nativeQuery = true)
    List<ItemRequest> findAllUsersItemRequest(@Param("userId") int userId, @Param("from") int from, @Param("size") int size);


}
