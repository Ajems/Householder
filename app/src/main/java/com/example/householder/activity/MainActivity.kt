package com.example.householder

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    private val productViewModel: ProductViewModel by lazy{
        ViewModelProvider(this).get(ProductViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

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

}

