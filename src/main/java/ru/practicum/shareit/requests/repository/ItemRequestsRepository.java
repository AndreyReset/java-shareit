package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.model.ItemShortData;

import java.util.List;

public interface ItemRequestsRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT i " +
            "FROM ItemRequest AS i " +
            "WHERE i.requester.id=?1 ")
    List<ItemRequest> findRequestsByOwnerId(long ownerId, Sort sort);

    @Query("SELECT i " +
            "FROM ItemRequest AS i " +
            "WHERE i.requester.id<>?1 ")
    Page<ItemRequest> findRequestsOtherUsersWithPagination(long userId, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.requests.model.ItemShortData" +
            "(i.id, i.name, i.description, i.available, i.requestId, i.owner.id) " +
            "FROM Item AS i " +
            "WHERE i.requestId=?1 ")
    List<ItemShortData> findItemShortDataForItemRequestById(long requestId);
}
