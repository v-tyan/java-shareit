package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDtoBooking getItem(long itemId, long userId) {
        log.info("Requested item with id = {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("ItemNotFound"));
        return setBookingsAndComments(userId, List.of(item)).get(0);
    }

    @Override
    public List<ItemDtoBooking> getItemsByUser(long userId, int from, int size) {
        log.info("Requsted items of user id = {}", userId);
        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, PageRequest.of(from / size, size));
        return setBookingsAndComments(userId, userItems);
    }

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.info("Request to create item = {}", itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Request not found"));
        }
        Item item = ItemMapper.fromItemDto(itemDto, user, itemRequest);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long id, ItemDto itemDto, long userId) {
        log.info("Request to update item = {} with id = {}", itemDto, id);
        return itemRepository.findById(id)
                .map(i -> {
                    if (i.getOwner().getId() != userId) {
                        throw new UserNotFoundException("User not found");
                    }
                    if (itemDto.getName() != null)
                        i.setName(itemDto.getName());
                    if (itemDto.getDescription() != null)
                        i.setDescription(itemDto.getDescription());
                    if (itemDto.getAvailable() != null)
                        i.setAvailable(itemDto.getAvailable());
                    return ItemMapper.toItemDto(itemRepository.save(i));
                }).orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        log.info("Search items with keyword = {}", text);
        if (text.isEmpty())
            return new ArrayList<>();
        return ItemMapper.mapToItemDto(itemRepository.searchItems(text, PageRequest.of(from / size, size)));
    }

    @Override
    @Transactional
    public CommentDto createComment(long itemId, CommentDto commentDto, long authorId) {
        log.info("Creating comment for itemId = {}, from authorId = {}, commentDto = {}", itemId, authorId, commentDto);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id = " + itemId + " not found"));
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + authorId + " not found"));

        Comment comment = CommentMapper.toComment(user, item, commentDto);

        if (!bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                authorId,
                itemId,
                LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start")).isEmpty()) {

            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else
            throw new BadRequestException("This user can't comment on this");
    }

    private List<ItemDtoBooking> setBookingsAndComments(long userId, List<Item> items) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, BookingDtoForItem> last = bookingRepository
                .findBookingsLast(ids, now, userId, Sort.by(Sort.Direction.DESC, "start")).stream()
                .map(BookingMapper::toBookingDtoForItem)
                .collect(Collectors.toMap(BookingDtoForItem::getItemId, item -> item, (a, b) -> a));
        Map<Long, BookingDtoForItem> next = bookingRepository
                .findBookingsNext(ids, now, userId, Sort.by(Sort.Direction.DESC, "start")).stream()
                .map(BookingMapper::toBookingDtoForItem)
                .collect(Collectors.toMap(BookingDtoForItem::getItemId, item -> item, (a, b) -> b));
        Map<Long, List<Comment>> comments = commentRepository.findByItemId_IdIn(ids).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemDtoBooking> result = items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toList());
        for (ItemDtoBooking item : result) {
            item.setLastBooking(last.get(item.getId()));
            item.setNextBooking(next.get(item.getId()));
            item.getComments().addAll(comments.getOrDefault(item.getId(), List.of()).stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }

        return result;
    }
}
