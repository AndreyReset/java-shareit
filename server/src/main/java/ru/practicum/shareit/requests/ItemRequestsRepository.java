package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemShortData;

import java.util.List;

public interface ItemRequestsRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_id(long ownerId, Sort sort);

    @Query("SELECT i " +
            "FROM ItemRequest AS i " +
            "WHERE i.requester.id<>?1 ")
    Page<ItemRequest> findRequestsOtherUsersWithPagination(long userId, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.requests.ItemShortData" +
            "(i.id, i.name, i.description, i.available, i.requestId, i.owner.id) " +
            "FROM Item AS i " +
            "WHERE i.requestId=?1 ")
    List<ItemShortData> findItemShortDataForItemRequestById(long requestId);
}
