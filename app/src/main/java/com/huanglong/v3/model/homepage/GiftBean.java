package com.huanglong.v3.model.homepage;

import java.io.Serializable;

/**
 * Created by bin on 2018/5/11.
 * 礼物的bean
 */

public class GiftBean implements Serializable {

    private String id;
    private int type;
    private String name;
    private String img_url;
    private int v_coin;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean seleted) {
        isSelected = seleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getV_coin() {
        return v_coin;
    }

    public void setV_coin(int v_coin) {
        this.v_coin = v_coin;
    }
}
