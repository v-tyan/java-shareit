package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item getItem(long id) {
        log.info("Requested item with id = {}", id);
        return itemRepository.getItem(id);
    }

    @Override
    public List<Item> getItemsByUser(long id) {
        log.info("Requsted items of user id = {}", id);
        return itemRepository.getItemsByUser(id);
    }

    @Override
    public Item createItem(Item item) {
        log.info("Request to create item = {}", item);
        userRepository.getUser(item.getOwner());
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItem(long id, Item item) {
        log.info("Request to update item = {} with id = {}", item, id);
        return itemRepository.updateItem(id, item);
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Search items with keyword = {}", text);
        if (text.isEmpty())
            return new ArrayList<>();
        return itemRepository.searchItems(text);
    }

}
