package com.yy.sign.model;

import androidx.annotation.NonNull;

public class Member {
    public String name;
   public String number;

    public Member(String name, String number) {
        this.name = name;
        this.number = number;
    }

    @NonNull
    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
