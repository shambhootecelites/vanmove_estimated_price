package com.vanmove.passesger.model

import java.util.*

class MessageEvent {
    var key: String? = null
    var message: String? = null
    var data: String? = null
    var arrayList: ArrayList<String>? = null

    constructor() {}
    constructor(key: String?, message: String?) {
        this.key = key
        this.message = message
    }

    constructor(key: String?, arrayList: ArrayList<String>?) {
        this.key = key
        this.arrayList = arrayList
    }

    constructor(key: String?, message: String?, data: String?) {
        this.key = key
        this.message = message
        this.data = data
    }

}