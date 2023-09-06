package com.mypill.domain.product.entity;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.image.entity.ImageOperator;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "delete_date IS NULL")
public class Product extends BaseEntity implements ImageOperator {

    @NotNull
    private String name;
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull
    private Long price;
    @NotNull
    private Long stock;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Builder.Default
    private Long sales = 0L;

    @ManyToMany(cascade = CascadeType.ALL)
    @Builder.Default
    @JoinTable(
            name = "product_nutrient", // 연결테이블
            joinColumns = @JoinColumn(name = "product_id"),  // Product 기본키
            inverseJoinColumns = @JoinColumn(name = "nutrient_id")  // Nutrient 기본키
    )
    private List<Nutrient> nutrients = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @Builder.Default
    @JoinTable(
            name = "product_category", // 연결테이블
            joinColumns = @JoinColumn(name = "product_id"),  // Product 기본키
            inverseJoinColumns = @JoinColumn(name = "category_id")  // Category 기본키
    )
    private List<Category> categories = new ArrayList<>();

    @Builder.Default
    private Long likeCount = 0L;

    public static Product of(ProductRequest request, List<Nutrient> nutrients, List<Category> categories,
                             Member seller) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .seller(seller)
                .nutrients(nutrients)
                .categories(categories)
                .build();
    }

    public void update(ProductRequest request, List<Nutrient> nutrients, List<Category> categories) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.price = request.getPrice();
        this.stock = request.getStock();
        this.nutrients = nutrients;
        this.categories = categories;
    }

    public void updateStockAndSalesByOrder(Long quantity) {
        this.stock -= quantity;
        this.sales += quantity;
    }

    public void plusLikeCount() {
        this.likeCount += 1;
    }

    public void minusLikeCount() {
        this.likeCount -= 1;
    }

    @Override
    public void addImage(Image image) {
        this.image = image;
    }

    @Override
    public String getFolderName() {
        return "product";
    }
}
