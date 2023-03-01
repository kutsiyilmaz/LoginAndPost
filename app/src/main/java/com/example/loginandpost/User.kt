package com.example.loginandpost

import android.os.Parcelable

class User (val uid: String, val username: String, val profileImageUrl: String) {
    constructor(): this("","","")//default constr.
}