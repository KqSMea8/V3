package com.huanglong.v3.model.home;

/**
 * Created by bin on 2018/3/5.
 * 职业分类的bean
 */

public class JobClassBean {

    private String id;
    private String name;
    private String desc;
    private int sort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
