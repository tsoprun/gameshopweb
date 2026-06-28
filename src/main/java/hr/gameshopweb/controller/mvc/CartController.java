package hr.gameshopweb.controller.mvc;


import hr.gameshopweb.entity.CartItem;
import hr.gameshopweb.entity.Product;
import hr.gameshopweb.service.CartService;
import hr.gameshopweb.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor

public class CartController {

    private static final String REDIRECT_CART = "redirect:/cart";

    private final CartService cartService;
    private final ProductService productService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("total", cartService.getTotal(session));
        return "cart/cart";
    }
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        Product p = productService.findById(productId);
        cartService.addItem(session,
                new CartItem(p.getId(), p.getName(), p.getImageUrl(), p.getPrice(), quantity));
        return REDIRECT_CART;
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity, HttpSession session) {
        cartService.updateQuantity(session, productId, quantity);
        return REDIRECT_CART;
    }

    @PostMapping("/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        cartService.removeItem(session, productId);
        return REDIRECT_CART;
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return REDIRECT_CART;
    }

}
