package hr.gameshopweb.dto;

import hr.gameshopweb.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    private Long id;
    private String name;
    private String slug;

    public Category toEntity() {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        return category;
    }
}
