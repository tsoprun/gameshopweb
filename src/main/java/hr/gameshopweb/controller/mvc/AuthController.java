package hr.gameshopweb.controller.mvc;

import hr.gameshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false)String error, Model model) {
        model.addAttribute("error", error!=null);
        return "auth/login";
    }
    @GetMapping("/register")
    public String registerPage() { return "auth/register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password, Model model) {
        try {
            userService.register(username, email, password);
            return "redirect:/auth/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

}
