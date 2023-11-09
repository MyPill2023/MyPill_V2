package com.mypill.common.factory;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;

import java.time.LocalDateTime;

public class AddressFactory {

    private AddressFactory(){}

    public static Address address(Member member){
        return createAddress(member, "testAddressName");
    }

    public static Address address(Member member, String addressName){
        return createAddress(member, addressName);
    }

    public static AddressRequest addressRequest(String addressName){
        return createAddressRequest(addressName, true);
    }

    public static AddressRequest addressRequest(String addressName, boolean isDefault){
        return createAddressRequest(addressName, isDefault);
    }

    public static Address deletedAddress(Member member){
        return createAddress(member, "deletedAddress")
                .toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();
    }

    private static Address createAddress(Member member, String addressName){
        return Address.of(member, addressRequest(addressName));
    }

    private static AddressRequest createAddressRequest(String addressName, boolean isDefault){
        return new AddressRequest(addressName, "testUser", "testAddress", "testDetailAddress", "10101", "01012341234", isDefault);
    }
}
