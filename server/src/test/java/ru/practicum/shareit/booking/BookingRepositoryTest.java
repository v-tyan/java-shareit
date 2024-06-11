package ru.practicum.shareit.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    User itemOwner = User.builder()
            .id(null)
            .name("itemOwner")
            .email("itemOwner@user.ru")
            .build();
    User booker = User.builder()
            .id(null)
            .name("booker")
            .email("booker@user.ru")
            .build();
    Item item = Item.builder()
            .id(null)
            .name("name1")
            .description("description first")
            .available(true)
            .owner(itemOwner)
            .request(null)
            .build();
    Booking booking = Booking.builder()
            .id(null)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(item)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();
    PageRequest pageRequest = PageRequest.ofSize(1);
    Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void testFindAllByBookerIdOrderByStartDesc() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByItemOwnerId() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerId(itemOwner.getId(), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByBookerIdAndItemIdAndEndBefore() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(booker.getId(), item.getId(),
                booking.getEnd(), sort);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByBookerCurrent() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerCurrent(booker.getId(),
                booking.getEnd().minusMinutes(30), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByBookerPast() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerPast(booker.getId(),
                booking.getEnd().plusMinutes(30), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByBookerFuture() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerFuture(booker.getId(),
                booking.getStart().minusMinutes(30), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByBookerAndStatus() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerAndStatus(booker.getId(), booking.getStatus(), sort,
                pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByItemOwnerCurrent() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerCurrent(itemOwner.getId(),
                booking.getEnd().minusMinutes(30), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByItemOwnerPast() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerPast(itemOwner.getId(),
                booking.getEnd().plusMinutes(30), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByItemOwnerFuture() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerFuture(itemOwner.getId(),
                booking.getStart().minusMinutes(30), sort, pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindByItemOwnerAndStatus() {
        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerAndStatus(itemOwner.getId(), booking.getStatus(),
                sort,
                pageRequest);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindBookingsLast() {
        booking.setStatus(BookingStatus.APPROVED);

        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findBookingsLast(List.of(item.getId()),
                booking.getStart().plusMinutes(30), item.getOwner().getId(), sort);
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void testFindBookingsNext() {
        booking.setStatus(BookingStatus.APPROVED);

        em.persist(itemOwner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findBookingsNext(List.of(item.getId()),
                booking.getStart().minusMinutes(30), item.getOwner().getId(), sort);
        assertEquals(booking, bookings.get(0));
    }
}