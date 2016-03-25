package com.autumn.examples;

import java.util.Date;

/**
 * <p>Title: DEMO数据模型</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author Hibernate Tools 3.2.1.GA
 * @version 2.0
 *
 */

public class Demo implements java.io.Serializable {

    static long serialVersionUID = 3981923233990180760L;
    private Integer id;
    private String name;
    private int age;
    private boolean sex;
    private String mobile;
    private Date regtime;
    private String memo;

    @Override
    public String toString() {
        return "Demo{" + "id=" + id + ", name=" + name + ", age=" + age + ", sex=" + sex + ", mobile=" + mobile + ", regtime=" + regtime + ", memo=" + memo + '}';
    }

    public Demo() {
    }

    public Demo(String name, int age, boolean sex, Date regtime) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.regtime = regtime;
    }

    public Demo(String name, int age, boolean sex, String mobile, Date regtime, String memo) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.mobile = mobile;
        this.regtime = regtime;
        this.memo = memo;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return this.sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getRegtime() {
        return this.regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
