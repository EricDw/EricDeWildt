package com.publicmethod.ericdewildt.ui.eric

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.publicmethod.ericdewildt.R
import kotlinx.android.synthetic.main.eric_activity.*

class EricActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eric_activity)
        setSupportActionBar(bottom_appbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EricFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.eric_menu, menu)
        return true
    }
}
