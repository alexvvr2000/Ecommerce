package com.stellaTech.ecommerce.model;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "product", schema = "product_data")
public class Product extends LogicallyDeletableEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "md_format_description")
    private String mdFormatDescription;

    @Setter
    @Column(name = "main_image_url")
    private String mainImageUrl;

    @Column(name = "average_rating", precision = 4, scale = 2)
    private BigDecimal averageRating = null;

    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    public Product(@NonNull String name, @NonNull BigDecimal price, String mdFormatDescription, String mainImageUrl) {
        this.setName(name);
        this.setPrice(price);
        this.setMdFormatDescription(mdFormatDescription);
        this.setMainImageUrl(mainImageUrl);
    }

    public void setAverageRating(@NonNull BigDecimal averageRating) throws InvalidInputException {
        if (averageRating.compareTo(BigDecimal.ZERO) < 0 ||
                averageRating.compareTo(new BigDecimal("10.00")) > 0) {
            throw new InvalidInputException("Rating out of range");
        }
        this.averageRating = averageRating;
    }

    public void setPrice(BigDecimal price) throws InvalidInputException {
        if (price.compareTo(new BigDecimal("0.01")) < 0) {
            throw new InvalidInputException("Invalid price");
        }
        this.price = price;
    }
}
