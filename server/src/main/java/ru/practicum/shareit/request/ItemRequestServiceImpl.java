package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRsp;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDtoRsp create(long userId, ItemRequestDtoReq itemRequestDtoRQ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        itemRequestDtoRQ.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDtoRQ, user));
        log.info("Request created");
        return ItemRequestMapper.toItemRequestDtoRsp(itemRequest);
    }

    @Override
    public List<ItemRequestDtoRsp> getAllInfo(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ItemRequestDtoRsp> responseList = requestRepository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDtoRsp)
                .collect(Collectors.toList());
        setItemsToRequests(responseList);
        return responseList;
    }

    @Override
    public ItemRequestDtoRsp getInfo(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));
        List<ItemDto> items = itemRepository.findByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDtoRsp itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoRsp(itemRequest);
        itemRequestDtoResponse.setItems(items);
        return itemRequestDtoResponse;
    }

    @Override
    public List<ItemRequestDtoRsp> getRequests(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequestDtoRsp> responseList = requestRepository.findAllPageable(userId, pageRequest).stream()
                .map(ItemRequestMapper::toItemRequestDtoRsp)
                .collect(Collectors.toList());
        setItemsToRequests(responseList);
        return responseList;
    }

    private void setItemsToRequests(List<ItemRequestDtoRsp> itemRequestDtoRsp) {
        Map<Long, ItemRequestDtoRsp> requests = itemRequestDtoRsp.stream()
                .collect(Collectors.toMap(ItemRequestDtoRsp::getId, itemRequest -> itemRequest, (a, b) -> b));
        List<Long> ids = requests.values().stream()
                .map(ItemRequestDtoRsp::getId)
                .collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepository.searchByRequestsId(ids).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        for (ItemDto itemDto : itemDtos) {
            requests.get(itemDto.getRequestId()).getItems().add(itemDto);
        }
    }
}
