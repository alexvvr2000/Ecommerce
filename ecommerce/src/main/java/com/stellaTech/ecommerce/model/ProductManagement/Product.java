package com.stellaTech.ecommerce.model.ProductManagement;

import com.stellaTech.ecommerce.model.inheritance.LogicallyDeletableEntity;
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

    @Setter
    @Column(name = "average_rating", precision = 4, scale = 2)
    private BigDecimal averageRating = null;

    @Setter
    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    public Product(@NonNull String name, @NonNull BigDecimal price, String mdFormatDescription, String mainImageUrl) {
        this.setName(name);
        this.setPrice(price);
        this.setMdFormatDescription(mdFormatDescription);
        this.setMainImageUrl(mainImageUrl);
    }
}
