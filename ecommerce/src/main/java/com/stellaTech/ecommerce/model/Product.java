package com.stellaTech.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product", schema = "product_data")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "average_rating", precision = 4, scale = 2)
    private BigDecimal average_rating;

    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "md_format_description")
    private String md_format_description;

    @Column(name = "main_image_url")
    private String main_image_url;

    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private Boolean deleted = false;

    public Product() {

    }

    public Product(Integer id, String name, BigDecimal average_rating, BigDecimal price, String md_format_description, String main_image_url, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.average_rating = average_rating;
        this.price = price;
        this.md_format_description = md_format_description;
        this.main_image_url = main_image_url;
        this.deleted = deleted;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getMain_image_url() {
        return main_image_url;
    }

    public void setMain_image_url(String main_image_url) {
        this.main_image_url = main_image_url;
    }

    public String getMd_format_description() {
        return md_format_description;
    }

    public void setMd_format_description(String md_format_description) {
        this.md_format_description = md_format_description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(BigDecimal average_rating) {
        this.average_rating = average_rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @PrePersist
    @PreUpdate
    private void validateRating() throws Exception {
        if (average_rating.compareTo(BigDecimal.ZERO) < 0 ||
                average_rating.compareTo(new BigDecimal("10.00")) > 0) {
            throw new Exception("Rating out of range");
        }
    }

    @PrePersist
    @PreUpdate
    public void validatePrice() {
        if (price.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Invalid price");
        }
    }
}
