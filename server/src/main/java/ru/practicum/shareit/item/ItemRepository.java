package ru.practicum.shareit.item;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(long userId, Pageable pageable);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (lower(it.description) like lower(concat('%', ?1,'%'))" +
            "or lower(it.name) like lower(concat('%', ?1,'%')) )")
    List<Item> searchItems(String text, Pageable pageable);

    @Query("select item " +
            "from Item item " +
            "where item.request.id in :ids")
    List<Item> searchByRequestsId(@Param("ids") List<Long> ids);

    @Query("select item " +
            "from Item item " +
            "where item.request.id = ?1")
    List<Item> findByItemRequestId(long requestId);
}
