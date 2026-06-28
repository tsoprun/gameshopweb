package hr.gameshopweb.controller.rest;

import hr.gameshopweb.service.OrderService;
import hr.gameshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminRestController {

    private final OrderService orderService;
    private final UserService userService;


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        var recentOrders = orderService
                .findAllWithFilters(null, null, null, PageRequest.of(0, 5))
                .getContent();
        return ResponseEntity.ok(Map.of(
                "totalUsers", userService.findAll().size(),
                "recentOrders", recentOrders.size()
        ));
    }
}