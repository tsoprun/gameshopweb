package hr.gameshopweb.service;


import hr.gameshopweb.entity.*;
import hr.gameshopweb.exception.ResourceNotFoundException;
import hr.gameshopweb.repository.OrderRepository;
import hr.gameshopweb.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final EmailService emailService;


    public Order createOrder(User user, HttpSession session,
                             Order.PaymentMethod paymentMethod,
                             Order.OrderStatus status,
                             String paypalPaymentId) {
        List<CartItem> cartItems = cartService.getCart(session);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(status);
        order.setPaypalPaymentId(paypalPaymentId);

        List<OrderItem> items = cartItems.stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setProductNameSnapshot(cartItem.getProductName());
            item.setProductImageSnapshot(cartItem.getProductImage());
            item.setQuantity(cartItem.getQuantity());
            item.setPriceAtPurchase(cartItem.getPrice());
            return item;
        }).toList();

        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(items);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(session);

        emailService.sendOrderConfirmation(user.getEmail(), saved);
        return saved;
    }

    public List<Order> findUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Page<Order> findAllWithFilters(Long userId, LocalDateTime from,
                                          LocalDateTime to, Pageable pageable) {
        return orderRepository.findWithFilters(userId, from, to, pageable);
    }

    public Order findById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }
}