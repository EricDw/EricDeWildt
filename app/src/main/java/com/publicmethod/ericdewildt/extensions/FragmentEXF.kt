package com.publicmethod.ericdewildt.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment

inline fun <reified C : ViewModel> Fragment.getViewModel(): C {
    return ViewModelProviders.of(this).get(C::class.java)
}




