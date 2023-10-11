package com.mypill.common.factory;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.nutrient.entity.Nutrient;

import java.util.ArrayList;
import java.util.List;

public class MockMember {
    private MockMember() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String username;
        private String name;
        private String password;
        private Role role;
        private String email;
        private String providerTypeCode;
        private boolean emailVerified;
        private String businessNumber;
        private String nutrientBusinessNumber;
        private List<Nutrient> surveyNutrients = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public Member build() {
            return Member.builder()
                    .id(id)
                    .username(username)
                    .name(name)
                    .password(password)
                    .role(role)
                    .email(email)
                    .providerTypeCode(providerTypeCode)
                    .emailVerified(emailVerified)
                    .businessNumber(businessNumber)
                    .nutrientBusinessNumber(nutrientBusinessNumber)
                    .surveyNutrients(surveyNutrients)
                    .addresses(addresses)
                    .build();
        }
    }
}
