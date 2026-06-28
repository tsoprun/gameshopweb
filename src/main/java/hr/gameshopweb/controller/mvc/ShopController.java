package hr.gameshopweb.controller.mvc;

import hr.gameshopweb.entity.Product;
import hr.gameshopweb.integration.rawg.RawgClient;
import hr.gameshopweb.service.CartService;
import hr.gameshopweb.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;
    private final CartService cartService;


    @GetMapping
    public String catalog(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "") String search,
                          @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                          HttpSession session, Model model) {
        PageRequest pageable = PageRequest.of(page, 12);
        Page<Product> products;
        if (!search.isBlank()) {
            products = productService.search(search, pageable);
        } else if (categoryIds != null && !categoryIds.isEmpty()) {
            products = productService.findByCategories(categoryIds, pageable);
        } else {
            products = productService.findAll(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("cartCount", cartService.getItemCount(session));
        model.addAttribute("search", search);
        model.addAttribute("selectedCategoryIds", categoryIds != null ? categoryIds : List.of());
        return "shop/catalog";
    }

    @GetMapping("/product/{slug}")
    public String productDetail(@PathVariable String slug, HttpSession session, Model model) {
        Product product = productService.findBySlug(slug);
        model.addAttribute("product", product);
        model.addAttribute("screenshots", product.getImages());
        model.addAttribute("cartCount", cartService.getItemCount(session));
        return "shop/product-detail";
    }
}