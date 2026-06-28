package hr.gameshopweb.controller.mvc;


import hr.gameshopweb.dto.CategoryForm;
import hr.gameshopweb.dto.ProductForm;
import hr.gameshopweb.entity.Category;
import hr.gameshopweb.entity.Product;
import hr.gameshopweb.repository.CategoryRepository;
import hr.gameshopweb.repository.LoginLogRepository;
import hr.gameshopweb.service.OrderService;
import hr.gameshopweb.service.ProductService;
import hr.gameshopweb.service.RawgSyncService;
import hr.gameshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {

    private static final String ATTR_CATEGORIES = "categories";
    private static final String REDIRECT_PRODUCTS = "redirect:/admin/products";

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final LoginLogRepository loginLogRepository;
    private final CategoryRepository categoryRepository;
    private final RawgSyncService rawgSyncService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute(ATTR_CATEGORIES, productService.findAllCategories());
        return "admin/dashboard";
    }

    //CRUD za products
    @GetMapping("/products")
    public String products(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("products", productService.findAll(PageRequest.of(page, 20)));
        return "admin/products/list";
    }


    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute(ATTR_CATEGORIES, productService.findAllCategories());
        return "admin/products/form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute(ATTR_CATEGORIES, productService.findAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute ProductForm form,
                              @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                              @RequestParam(value = "imageUrls", required = false) List<String> imageUrls) {
        Product product = form.toEntity();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            product.setCategories(new HashSet<>(categoryRepository.findAllById(categoryIds)));
        } else {
            product.setCategories(new HashSet<>());
        }
        Product saved = productService.save(product);
        if (imageUrls != null) {
            productService.saveImages(saved.getId(), imageUrls);
        }
        return REDIRECT_PRODUCTS;
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return REDIRECT_PRODUCTS;
    }

    // Categories CRUD
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute(ATTR_CATEGORIES, productService.findAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category",
                productService.findAllCategories().stream()
                        .filter(c -> c.getId().equals(id)).findFirst().orElseThrow());
        return "admin/categories/form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute CategoryForm form) {
        productService.saveCategory(form.toEntity());
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        productService.deleteCategoryById(id);
        return "redirect:/admin/categories";
    }

    // Orders pregled s filterima
    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) Long userId,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                         LocalDateTime from,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                             LocalDateTime to,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        model.addAttribute("orders",
                orderService.findAllWithFilters(userId, from, to, PageRequest.of(page, 20)));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "admin/orders/list";
    }

    // Login logs
    @GetMapping("/logs")
    public String loginLogs(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("logs",
                loginLogRepository.findAllByOrderByLoggedInAtDesc(PageRequest.of(page, 30)));
        return "admin/logs";
    }

    // Sync s RAWG API-em
    @PostMapping("/sync-games")
    public String syncGames() {
        rawgSyncService.syncGames();
        return REDIRECT_PRODUCTS;
    }

}
