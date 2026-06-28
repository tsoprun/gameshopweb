package hr.gameshopweb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void incrementQuantity(int amount) {
        this.quantity += amount;
    }
}
