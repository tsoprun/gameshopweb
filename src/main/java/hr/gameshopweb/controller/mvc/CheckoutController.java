package hr.gameshopweb.controller.mvc;


import com.paypal.api.payments.Payment;
import hr.gameshopweb.entity.Order;
import hr.gameshopweb.entity.User;
import hr.gameshopweb.integration.paypal.PayPalService;
import hr.gameshopweb.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;

import java.security.Principal;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private static final String REDIRECT_PAYPAL_ERROR = "redirect:/checkout?paypalError=true";

    private final OrderService orderService;
    private final CartService cartService;
    private final PayPalService payPalService;
    private final UserService userService;


    @GetMapping
    public String checkoutPage(HttpSession session, Model model) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("total", cartService.getTotal(session));
        return "checkout/checkout";
    }

    @PostMapping("/cash")
    public String cashOnDelivery(Principal principal, HttpSession session) {
        User user = userService.findByUsername(principal.getName());
        Order order = orderService.createOrder(user, session,
                Order.PaymentMethod.GOTOVINA_POUZECE,
                Order.OrderStatus.COMPLETED, null);
        return "redirect:/orders/" + order.getId() + "?success=true";

    }


    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping("/paypal")
    public String initiatePaypal(HttpSession session) {
        try {
            Payment payment = payPalService.createPayment(
                    cartService.getTotal(session), "USD", "GameShop purchase",
                    baseUrl + "/checkout/paypal/cancel",
                    baseUrl + "/checkout/paypal/success"
            );
            return "redirect:" + payPalService.getApprovalUrl(payment);
        } catch (Exception e) {
            return REDIRECT_PAYPAL_ERROR;
        }
    }

    @GetMapping("/paypal/success")
    public String paypalSuccess(@RequestParam String paymentId,
                                @RequestParam("PayerID") String payerId,
                                Principal principal, HttpSession session) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if ("approved".equals(payment.getState())) {
                User user = userService.findByUsername(principal.getName());
                Order order = orderService.createOrder(user, session,
                        Order.PaymentMethod.PAYPAL,
                        Order.OrderStatus.COMPLETED, paymentId);
                return "redirect:/orders/" + order.getId() + "?success=true";
            }
        } catch (Exception e) {
            return REDIRECT_PAYPAL_ERROR;
        }
        return REDIRECT_PAYPAL_ERROR;
    }

    @GetMapping("/paypal/cancel")
    public String paypalCancel() {
        return "redirect:/checkout?paypalCancelled=true";
    }

}

