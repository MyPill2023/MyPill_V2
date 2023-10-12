package com.mypill.common.factory;

import com.mypill.common.mock.MockMember;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;

public class MemberFactory {

    private MemberFactory(){}

    public static Member member(String username){
        return createMember(null, username, Role.MEMBER);
    }

    public static Member member(String username, Role role){
        return createMember(null, username, role);
    }

    public static Member member(Long id, String username){
        return createMember(id, username, Role.MEMBER);
    }

    public static Member createMember(Long id, String username, Role role){
        return MockMember.builder()
                .id(id)
                .name("name")
                .username(username)
                .password("1234")
                .email(username + "@test.com")
                .role(role)
                .build();
    }

}
