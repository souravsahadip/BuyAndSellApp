package com.example.buyandsellapp;

import java.time.LocalDateTime;
import java.util.Date;

public class CartItem {
    private String productID;
    private String uid;
    private Date timestamp;

    public CartItem() {
    }

    public CartItem(Date date, String productID, String uid) {
        this.timestamp=date;
        this.productID = productID;
        this.uid = uid;
    }


    public String getProductID() {
        return productID;
    }

    public String getUid() {
        return uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
