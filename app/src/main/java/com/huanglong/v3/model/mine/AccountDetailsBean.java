package com.huanglong.v3.model.mine;

/**
 * Created by bin on 2018/1/31.
 * 账户明细
 */

public class AccountDetailsBean {

    private String title;
    private int type;
    private String timestamp;
    private String amount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
