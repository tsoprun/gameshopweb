package hr.gameshopweb.controller.mvc;


import hr.gameshopweb.entity.Order;
import hr.gameshopweb.entity.User;
import hr.gameshopweb.service.OrderService;
import hr.gameshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String myOrders(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("orders", orderService.findUserOrders(user));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id,
                              @RequestParam(required = false) Boolean success,
                              Principal principal, Model model) {

        Order order = orderService.findById(id);

        if (!order.getUser().getUsername().equals(principal.getName()))
            return "redirect:/orders";

        model.addAttribute("order", order);
        model.addAttribute("success", Boolean.TRUE.equals(success));
        return "orders/detail";
    }

}
