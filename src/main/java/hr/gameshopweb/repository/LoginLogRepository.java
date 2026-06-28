package hr.gameshopweb.repository;

import hr.gameshopweb.entity.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    Page<LoginLog>findAllByOrderByLoggedInAtDesc(Pageable pageable);
}
