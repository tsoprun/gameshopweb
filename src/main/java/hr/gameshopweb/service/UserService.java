package hr.gameshopweb.service;

import hr.gameshopweb.entity.LoginLog;
import hr.gameshopweb.entity.User;
import hr.gameshopweb.exception.ResourceNotFoundException;
import hr.gameshopweb.repository.LoginLogRepository;
import hr.gameshopweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(User.Role.KUPAC);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void logLogin(User user, String ipAddress) {
        loginLogRepository.save(new LoginLog(user, ipAddress));
    }
}
