package com.publicmethod.ericdewildt.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment

inline fun <reified C : ViewModel> Fragment.getViewModel(): C {
    return ViewModelProviders.of(this).get(C::class.java)
}




