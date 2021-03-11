package com.enablex.multiconferencequickapp.model;

import java.io.Serializable;
import java.util.List;

public class PrivateChatModel implements Serializable {


    /**
     * broadcast : false
     * sender : Android
     * senderId : d1f9720d-6ea9-49a5-ae75-e1607a87985e
     * type : chat
     * message : hi
     * timestamp : 1571036137660
     * receipients : ["f7d96453-4884-42d0-b17a-d33327a8e0d9"]
     */

    private boolean broadcast;
    private String sender;
    private String senderId;
    private String type;
    private String message;
    private String timestamp;
    private List<String> receipients;
    private boolean isReceived;
    private String fileName;
    private String jsondata;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getReceipients() {
        return receipients;
    }

    public void setReceipients(List<String> receipients) {
        this.receipients = receipients;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getJsondata() {
        return jsondata;
    }

    public void setJsondata(String jsondata) {
        this.jsondata = jsondata;
    }
}
