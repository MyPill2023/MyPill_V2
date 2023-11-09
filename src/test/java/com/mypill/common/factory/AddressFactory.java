package com.mypill.common.factory;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.member.entity.Member;

public class AddressFactory {

    private AddressFactory(){}
    public static AddressRequest mockAddressRequest(String addressName){
        return new AddressRequest(addressName, "testUser", "testAddress", "testDetailAddress", "10101", "01012341234", true);
    }

    public static Address address(Member member, String addressName){
        return Address.of(member, mockAddressRequest(addressName));
    }
}
