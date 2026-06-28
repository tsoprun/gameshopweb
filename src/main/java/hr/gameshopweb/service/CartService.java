package hr.gameshopweb.service;

import hr.gameshopweb.entity.CartItem;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_KEY = "CART";

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    public void addItem(HttpSession session, CartItem newItem) {
        List<CartItem> cart = getCart(session);
        cart.stream()
                .filter(i -> i.getProductId().equals(newItem.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        i -> i.incrementQuantity(newItem.getQuantity()),
                        () -> cart.add(newItem)
                );
        session.setAttribute(CART_KEY, cart);
    }

    public void removeItem(HttpSession session, Long productId) {
        getCart(session).removeIf(i -> i.getProductId().equals(productId));
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        if (quantity <= 0) { removeItem(session, productId); return; }
        getCart(session).stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }

    public BigDecimal getTotal(HttpSession session) {
        return getCart(session).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount(HttpSession session) {
        return getCart(session).stream().mapToInt(CartItem::getQuantity).sum();
    }
}