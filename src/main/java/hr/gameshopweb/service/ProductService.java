package hr.gameshopweb.service;

import hr.gameshopweb.entity.Category;
import hr.gameshopweb.entity.Product;
import hr.gameshopweb.entity.ProductImage;
import hr.gameshopweb.exception.ResourceNotFoundException;
import hr.gameshopweb.repository.CategoryRepository;
import hr.gameshopweb.repository.ProductImageRepository;
import hr.gameshopweb.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findByCategories(List<Long> categoryIds, Pageable pageable) {
        return productRepository.findByCategoryIds(categoryIds, pageable);
    }

    public Page<Product> search(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug));
    }


    @Transactional
    public Product save(Product product) {
        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(slugify(product.getName()));
        }
        product.setTrailerUrl(normalizeTrailerUrl(product.getTrailerUrl()));
        return productRepository.save(product);
    }

    private static final String YOUTU_BE = "youtu.be/";

    // Pretvara obične YouTube linkove (watch?v=, youtu.be) u ugradiv embed oblik,
    // jer YouTube blokira ugradnju watch-stranica u iframe (X-Frame-Options).
    public static String normalizeTrailerUrl(String url) {
        if (url == null || url.isBlank()) return url;
        String u = url.trim();
        String id = null;
        if (u.contains("youtube.com/watch")) {
            int i = u.indexOf("v=");
            if (i != -1) id = u.substring(i + 2);
        } else if (u.contains(YOUTU_BE)) {
            id = u.substring(u.indexOf(YOUTU_BE) + YOUTU_BE.length());
        } else {
            return u; // već embed, .mp4 ili nešto drugo - ostavi
        }
        if (id == null) return u;
        // odreži eventualne dodatne parametre (&t=, ?si=, putanje)
        for (String sep : new String[]{"&", "?", "/"}) {
            int idx = id.indexOf(sep);
            if (idx != -1) id = id.substring(0, idx);
        }
        return id.isBlank() ? u : "https://www.youtube.com/embed/" + id;
    }


    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }


    @Transactional
    public Category saveCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(slugify(category.getName()));
        }
        return categoryRepository.save(category);
    }

    public static String slugify(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s]+", "-");
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void saveImages(Long productId, List<String> urls) {
        productImageRepository.deleteByProductId(productId);
        Product product = findById(productId);
        for (String url : urls) {
            if (url == null || url.isBlank()) continue;
            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setImageUrl(url.trim());
            productImageRepository.save(img);
        }
    }

}