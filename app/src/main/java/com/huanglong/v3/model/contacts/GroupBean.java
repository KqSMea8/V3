package com.huanglong.v3.model.contacts;

/**
 * Created by bin on 2018/4/17.
 */

public class GroupBean {

    private String id;
    private String GroupId;
    private String Operator_Account;
    private String Owner_Account;
    private String Name;
    private int is_fee;
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getOperator_Account() {
        return Operator_Account;
    }

    public void setOperator_Account(String operator_Account) {
        Operator_Account = operator_Account;
    }

    public String getOwner_Account() {
        return Owner_Account;
    }

    public void setOwner_Account(String owner_Account) {
        Owner_Account = owner_Account;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getIs_fee() {
        return is_fee;
    }

    public void setIs_fee(int is_fee) {
        this.is_fee = is_fee;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
