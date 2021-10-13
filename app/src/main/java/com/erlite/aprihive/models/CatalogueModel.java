// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.erlite.aprihive.models;

public class CatalogueModel {

    private String itemId, itemName, itemImageLink, itemPrice, itemDescription, itemUrl, itemAuthor;


    public CatalogueModel(){}

    public CatalogueModel(String itemId, String itemName, String itemImageLink, String itemPrice, String itemDescription, String itemUrl, String itemAuthor) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageLink = itemImageLink;
        this.itemPrice = itemPrice;
        this.itemDescription = itemDescription;
        this.itemUrl = itemUrl;
        this.itemAuthor = itemAuthor;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImageLink() {
        return itemImageLink;
    }

    public void setItemImageLink(String itemImageLink) {
        this.itemImageLink = itemImageLink;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getItemAuthor() {
        return itemAuthor;
    }

    public void setItemAuthor(String itemAuthor) {
        this.itemAuthor = itemAuthor;
    }
}
