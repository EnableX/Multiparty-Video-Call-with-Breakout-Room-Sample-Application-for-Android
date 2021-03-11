package com.enablex.multiconferencequickapp.model;

import java.io.Serializable;
import java.util.Comparator;

public class UserListModels implements Serializable {

    private String clientId;
    private String name;
    private boolean isBreakoutRoomJoined;
    private String role;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public boolean isBreakoutRoomJoined() {
        return isBreakoutRoomJoined;
    }


    public void setBreakoutRoomJoined(boolean breakoutRoomJoined) {
        isBreakoutRoomJoined = breakoutRoomJoined;
    }


    public static Comparator<UserListModels> userNameComparator = new Comparator<UserListModels>() {

        public int compare(UserListModels s1, UserListModels s2) {

            String name1 = s1.getName().toUpperCase();
            String name2 = s2.getName().toUpperCase();

            return name1.compareTo(name2);
        }
    };
}
