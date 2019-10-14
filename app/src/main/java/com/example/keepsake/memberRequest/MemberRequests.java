package com.example.keepsake.memberRequest;

public class MemberRequests {

    private String name;

    private String userId;
    public MemberRequests(){}

    public MemberRequests(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
