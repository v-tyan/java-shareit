package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end, Sort sort);

    @Query("select b from " +
            "Booking b " +
            "where ?2 between b.start and b.end " +
            "and b.booker.id = ?1 ")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now, Sort sort);

    @Query("select b from " +
            "Booking b " +
            "where b.end < ?2 " +
            "and b.booker.id = ?1 ")
    List<Booking> findByBookerPast(long userId, LocalDateTime end, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where b.start > ?2 " +
            "and b.booker.id = ?1 ")
    List<Booking> findByBookerFuture(long userId, LocalDateTime start, Sort sort);

    @Query("select b from " +
            "Booking b " +
            "where b.status = ?2 " +
            "and b.booker.id = ?1 ")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status, Sort sort);

    @Query("select b from " +
            "Booking b " +
            "where ?2 between b.start and b.end " +
            "and b.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now, Sort sort);

    @Query("select b from " +
            "Booking b " +
            "where b.end < ?2 " +
            "and b.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where b.start > ?2 " +
            "and b.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start, Sort sort);

    @Query("select b from " +
            "Booking b " +
            "where b.status = ?2 " +
            "and b.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerAndStatus(long userId, BookingStatus status, Sort sort);

    @Query("select distinct b " +
            "from Booking b " +
            "where b.start <= :now " +
            "and b.item.id in :ids " +
            "and b.status = 'APPROVED' " +
            "and b.item.owner.id = :userId ")
    List<Booking> findBookingsLast(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId,
                                   Sort sort);

    @Query("select distinct b " +
            "from Booking b " +
            "where b.start > :now " +
            "and b.status = 'APPROVED' " +
            "and b.item.id in :ids " +
            "and b.item.owner.id = :userId ")
    List<Booking> findBookingsNext(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId,
                                   Sort sort);
}