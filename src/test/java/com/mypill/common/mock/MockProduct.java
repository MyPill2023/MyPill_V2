package com.mypill.common.mock;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.image.entity.Image;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class MockProduct {

    private MockProduct(){}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Long price;
        private Long stock;
        private Member seller;
        private Image image;
        private Long sales = 0L;
        private List<Nutrient> nutrients = new ArrayList<>();
        private List<Category> categories = new ArrayList<>();
        private Long likeCount = 0L;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Long price) {
            this.price = price;
            return this;
        }

        public Builder stock(Long stock) {
            this.stock = stock;
            return this;
        }

        public Builder seller(Member seller) {
            this.seller = seller;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Product build(){
            return Product.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .price(price)
                    .stock(stock)
                    .seller(seller)
                    .image(image)
                    .sales(sales)
                    .nutrients(nutrients)
                    .categories(categories)
                    .likeCount(likeCount)
                    .build();
        }
    }


}
