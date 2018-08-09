package com.publicmethod.ericdewildt.ui.eric

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.publicmethod.ericdewildt.R

class EricActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eric_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, EricFragment.newInstance())
                    .commitNow()
        }
    }

}
