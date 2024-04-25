package ru.practicum.shareit.request;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(long userId);

    @Query("select itemRequest " +
            "from ItemRequest itemRequest " +
            "where itemRequest.requestor.id != ?1")
    List<ItemRequest> findAllPageable(long userId, Pageable pageable);
}
