package com.mypill.common.factory;

import com.mypill.domain.address.dto.request.AddressRequest;

public class AddressFactory {

    private AddressFactory(){}
    public static AddressRequest mockAddressRequest(String addressName){
        return new AddressRequest(addressName, "testUser", "testAddress", "testDetailAddress", "10101", "01012341234", true);
    }

}
