package com.example.householder

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.householder.activity.shoppingFragment
import com.example.householder.activity.walletFragment
import com.example.householder.data.ProductViewModel
import com.example.householder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val ShoppingFragment = shoppingFragment()
    private val CoockingFragment = coockingFragment()
    private val PrductsFragment = productsFragment()
    private val WalletFragment = walletFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (!isDarkModeOn()) window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true)
        binding.bottomNavigation.setBackgroundColor(typedValue.data)


        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.shoping -> {
                    replaceFragment(ShoppingFragment)
                    true
                }
                R.id.cooking -> {
                    replaceFragment(CoockingFragment)
                    true
                }
                R.id.products -> {
                    replaceFragment(PrductsFragment)
                    true
                }
                R.id.wallet -> {
                    replaceFragment(WalletFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun replaceFragment (fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .commit()
    }

    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

}

