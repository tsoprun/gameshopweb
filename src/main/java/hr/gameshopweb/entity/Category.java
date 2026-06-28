package hr.gameshopweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}
