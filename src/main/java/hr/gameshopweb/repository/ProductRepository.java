package hr.gameshopweb.repository;

import hr.gameshopweb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    Optional<Product> findByRawgId(Integer rawgId);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByRawgId(Integer rawgId);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds")
    Page<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);
}
