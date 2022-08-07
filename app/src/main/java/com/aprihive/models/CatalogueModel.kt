// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.models

class CatalogueModel {
    var itemId: String? = null
    var itemName: String? = null
    var itemImageLink: String? = null
    var itemPrice: String? = null
    var itemDescription: String? = null
    var itemUrl: String? = null
    var itemAuthor: String? = null

    constructor() {}

    constructor(itemId: String?, itemName: String?, itemImageLink: String?, itemPrice: String?, itemDescription: String?, itemUrl: String?, itemAuthor: String?) {
        this.itemId = itemId
        this.itemName = itemName
        this.itemImageLink = itemImageLink
        this.itemPrice = itemPrice
        this.itemDescription = itemDescription
        this.itemUrl = itemUrl
        this.itemAuthor = itemAuthor
    }


}