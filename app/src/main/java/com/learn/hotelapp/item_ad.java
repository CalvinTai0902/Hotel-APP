package com.learn.hotelapp;

import android.graphics.drawable.Drawable;

public class item_ad {
    Drawable  itemImg;
    String itemDesc;

    public item_ad(Drawable itemImg, String itemDesc) {
        this.itemImg = itemImg;
        this.itemDesc = itemDesc;
    }

    public Drawable getItemImg() {
        return itemImg;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemImg(Drawable itemImg) {
        this.itemImg = itemImg;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }
}
