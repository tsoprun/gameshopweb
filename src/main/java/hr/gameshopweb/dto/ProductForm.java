package hr.gameshopweb.dto;

import hr.gameshopweb.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductForm {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String trailerUrl;
    private String genre;
    private String platform;
    private Double metacriticScore;

    public Product toEntity() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setTrailerUrl(trailerUrl);
        product.setGenre(genre);
        product.setPlatform(platform);
        product.setMetacriticScore(metacriticScore);
        return product;
    }
}
