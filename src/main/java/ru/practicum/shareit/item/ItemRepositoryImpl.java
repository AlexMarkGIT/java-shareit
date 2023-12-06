package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long identifier = 0L;
    private final HashMap<Long, Item> items = new HashMap<>();
    private final HashMap<Long, List<Long>> itemsIdsByUserIds = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(++identifier);
        items.put(item.getId(), item);
        List<Long> userItems;
        if (!itemsIdsByUserIds.containsKey(item.getOwnerId())) {
            userItems = new ArrayList<>();
            userItems.add(item.getId());
        } else {
            userItems = itemsIdsByUserIds.get(item.getOwnerId());
            userItems.add(item.getId());
        }
        itemsIdsByUserIds.put(item.getOwnerId(), userItems);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item itemFromRep = items.get(item.getId());
        if (item.getName() != null) {
            itemFromRep.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromRep.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromRep.setAvailable(item.getAvailable());
        }
        items.put(itemFromRep.getId(), itemFromRep);
        return itemFromRep;
    }

    @Override
    public Item getById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        List<Item> itemsByUser = new ArrayList<>();
        for (Long itemId : itemsIdsByUserIds.get(userId)) {
            itemsByUser.add(items.get(itemId));
        }
        return itemsByUser;
    }

    @Override
    public List<Item> search(String searchText) {
        List<Item> itemsForSearch = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if ((item.getName() + item.getDescription()).toLowerCase().contains(searchText)) {
                    itemsForSearch.add(item);
                }
            }
        }
        return itemsForSearch;
    }

    @Override
    public Boolean contains(Long itemId) {
        return items.containsKey(itemId);
    }

    @Override
    public Boolean isItemWasPostedByUser(Long itemId, Long userId) {
        if (!itemsIdsByUserIds.containsKey(userId)) {
            return false;
        } else {
            return itemsIdsByUserIds.get(userId).contains(itemId);
        }
    }
}
