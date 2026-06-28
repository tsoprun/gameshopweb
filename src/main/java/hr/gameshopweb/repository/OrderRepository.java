package hr.gameshopweb.repository;

import hr.gameshopweb.entity.Order;
import hr.gameshopweb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    // Admin: filter by user and date range
    @Query("SELECT o FROM Order o WHERE " +
            "(:userId IS NULL OR o.user.id = :userId) AND " +
            "(:from IS NULL OR o.createdAt >= :from) AND " +
            "(:to IS NULL OR o.createdAt <= :to) " +
            "ORDER BY o.createdAt DESC")
    Page<Order> findWithFilters(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    // Ucitaj narudzbu zajedno sa stavkama (izbjegava LazyInitializationException
    // pri prikazu detalja narudzbe izvan transakcije).
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
