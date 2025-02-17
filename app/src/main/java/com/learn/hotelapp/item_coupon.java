package com.learn.hotelapp;

import android.graphics.drawable.Drawable;

public class item_coupon {
    int itemImg;
    int itemDiscount;
    String itemTitle;
    String itemDate;

    public item_coupon(int itemImg,int itemDiscount, String itemTitle, String itemDate) {
        this.itemImg = itemImg;
        this.itemDiscount = itemDiscount;
        this.itemTitle = itemTitle;
        this.itemDate = itemDate;
    }

    // Getter and Setter methods //
    public int getItemImg() {
        return itemImg;
    }

    public int getItemDiscount() {
        return itemDiscount;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemImg(int itemImg) {
        this.itemImg = itemImg;
    }

    public void setItemDiscount(int itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

}
