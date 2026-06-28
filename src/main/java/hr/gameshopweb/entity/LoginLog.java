package hr.gameshopweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "login_logs")
@Getter
@Setter
@NoArgsConstructor
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime loggedInAt = LocalDateTime.now(ZoneId.systemDefault());

    public LoginLog(User user, String ipAddress) {
        this.user = user;
        this.ipAddress = ipAddress;
    }
}