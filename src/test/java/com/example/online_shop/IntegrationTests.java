package com.example.online_shop;

import com.example.online_shop.model.dto.ItemsWithPagingDto;
import com.example.online_shop.repository.ItemRepository;
import com.example.online_shop.repository.OrderRepository;
import com.example.online_shop.service.CartService;
import com.example.online_shop.service.ItemService;
import com.example.online_shop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests extends OnlineShopApplicationTests {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;

    @ParameterizedTest
    @ValueSource(strings = {"NO", "ALPHA", "PRICE"})
    void testGetItemsCheckSort(String sort) throws Exception {
        ItemsWithPagingDto items = itemService.getItems(null, sort, 1, 10);
        assertNotNull(items);
        assertNotNull(items.getItems());
        assertNotNull(items.getPaging());

        if ("ALPHA".equalsIgnoreCase(sort)) {
            assertEquals(jdbcTemplate.queryForObject("SELECT min(title) FROM items", String.class),
                    items.getItems().getFirst().getFirst().getTitle());
        } else if ("PRICE".equalsIgnoreCase(sort)) {
            assertEquals(jdbcTemplate.queryForObject("SELECT min(price) FROM items", BigDecimal.class),
                    items.getItems().getFirst().getFirst().getPrice());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Товар №1"})
    void testGetItemsCheckSearch(String search) throws Exception {
        ItemsWithPagingDto items = itemService.getItems(null, "NO", 1, 10);
        assertNotNull(items);
        assertNotNull(items.getItems());
        assertNotNull(items.getPaging());

        if (search != null && !search.isBlank()) {
            boolean found = items.getItems().stream().flatMap(List::stream).anyMatch(item -> item.getTitle().contains(search));
            assertTrue(found);
        } else {
            assertTrue(items.getItems().size() <= itemRepository.count());
        }
    }


}
