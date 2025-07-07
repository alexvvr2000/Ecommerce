package com.stellaTech.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product", schema = "product_data")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "average_rating", precision = 4, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "md_format_description")
    private String mdFormatDescription;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;

    public Product() {

    }

    public Product(String name, BigDecimal averageRating, BigDecimal price, String mdFormatDescription, String mainImageUrl) {
        this.name = name;
        this.averageRating = averageRating;
        this.price = price;
        this.mdFormatDescription = mdFormatDescription;
        this.mainImageUrl = mainImageUrl;
    }

    @PrePersist
    @PreUpdate
    private void validateColumns() throws Exception {
        if (averageRating.compareTo(BigDecimal.ZERO) < 0 ||
                averageRating.compareTo(new BigDecimal("10.00")) > 0) {
            throw new Exception("Rating out of range");
        }
        if (price.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Invalid price");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMdFormatDescription() {
        return mdFormatDescription;
    }

    public void setMdFormatDescription(String mdFormatDescription) {
        this.mdFormatDescription = mdFormatDescription;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
