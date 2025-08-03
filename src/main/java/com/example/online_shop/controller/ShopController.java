package com.example.online_shop.controller;

import com.example.online_shop.model.dto.CartDto;
import com.example.online_shop.model.dto.ItemsWithPagingDto;
import com.example.online_shop.service.CartService;
import com.example.online_shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ShopController {
    private final ItemService itemService;
    private final OrderService orderService;
    private final CartService cartService;

    /**
     * GET "/" — редирект на "/main/items".
     */

    @GetMapping("/")
    public String redirectItems() {
        return "redirect:/main/items";
    }

    /**
     * GET "/main/items" -список всех товаров плиткой на главной странице
     * Параметры:
     * search - строка с поиском по названию/описанию товара (по умолчанию, пустая строка - все товары)
     * sort - сортировка. Перечисление NO, ALPHA, PRICE (по умолчанию, NO - не использовать сортировку)
     * pageSize - максимальное число товаров на странице (по умолчанию, 10)
     * pageNumber - номер текущей страницы (по умолчанию, 1)
     * Возвращает: шаблон "main.html"
     * используется модель для заполнения шаблона:
     * "items" - List<List<Item>> - список товаров по N в ряд (id, title, description, imagePath, count, price)
     * "search" - строка поиска (по умолчанию, пустая строка - все товары)
     * "sort" - сортировка. Перечисление NO, ALPHA, PRICE (по умолчанию, NO - не использовать сортировку)
     * "paging":
     * "pageNumber" - номер текущей страницы (по умолчанию, 1)
     * "pageSize" - максимальное число товаров на странице (по умолчанию, 10)
     * "hasNext" - можно ли пролистнуть вперед
     * "hasPrevious" - можно ли пролистнуть назад
     */

    @GetMapping("/main/items")
    public String getItems(Model model,
                           @RequestParam(defaultValue = "", name = "search") String search,
                           @RequestParam(defaultValue = "NO", name = "sort") String sort,
                           @RequestParam(defaultValue = "1", name = "pageNumber") int pageNumber,
                           @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {

        ItemsWithPagingDto items = itemService.getItems(search, sort, pageNumber, pageSize);
        model.addAttribute("items", items.getItems());
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", items.getPaging());
        return "main";

    }

    /**
     * POST "/main/items/{id}" - изменить количество товара в корзине
     *
     * @param id товара
     * @param action значение из перечисления PLUS|MINUS|DELETE (добавить товар, удалить один товар, удалить товар из корзины)
     * @return редирект на "/main/items"
     */
    @PostMapping("/main/items/{id}")
    public String changeItemCount(@PathVariable("id") Long id,
                                  @RequestParam(name = "action") String action) {
        itemService.actionWithItemInCart(id, action);
        return "redirect:/main/items";
    }

    /**
     * GET "/cart/items" - список товаров в корзине
     *
     * @param model "items" - List<Item> - список товаров в корзине (id, title, description, imgPath, count, price)
     *              "total" - суммарная стоимость заказа
     *              "empty" - trueб если в корзину не добавлен ни один товар
     * @return шаблон "cart.html"
     */
    @GetMapping("/cart/items")
    public String getChart(Model model) {
        CartDto cartCopy = cartService.getCart();
        model.addAttribute("items", cartCopy.getItems().values());
        model.addAttribute("total", cartCopy.getTotal());
        model.addAttribute("empty", cartCopy.isEmpty());
        return "cart";

    }

    /**
     * POST "/cart/items/{id}" - изменить количество товара в корзине
     *
     * @param id товара
     * @param action значение из перечисления PLUS|MINUS|DELETE (PLUS - добавить один товар, MINUS - удалить один товар,
     *               DELETE - удалить товар из корзины)
     * @return "redirect:/cart/items"
     */

    @PostMapping("/cart/items/{id}")
    public String changeItemCountInCart(@PathVariable("id") Long id,
                                        @RequestParam(name = "action") String action) {
        itemService.actionWithItemInCart(id, action);
        return "redirect:/cart/items";
    }

    /**
     * DET "/items/{id}" - карточка товара
     *
     * @param id товара
     * @param model "item" товар(id, title, description, imgPath, count, price)
     * @return "item"
     */
    @GetMapping("/items/{id}")
    public String getItem(@PathVariable("id") Long id, Model model) {
        model.addAttribute("item", itemService.getItemDtoById(id));
        return "item";
    }

    /**
     * POST "/items/{id}" - изменить количество товара в корзине
     * @param id товара
     * @param action значение из перечисления PLUS|MINUS|DELETE (PLUS - добавить один товар, MINUS - удалить один товар, DELETE - удалить товар из корзины)
     * @return "redirect:/items/"
     */
    @PostMapping("/items/{id}")
    public String changeItemsCount(@PathVariable("id") Long id,
                                   @RequestParam(name = "action") String action) {
        itemService.actionWithItemInCart(id, action);
        return "redirect:/items/" + id;
    }
}


