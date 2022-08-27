package com.example.householder.data

import androidx.lifecycle.ViewModel

class ProductViewModel: ViewModel() {

    var productArr = ArrayList<Product>()
    private var total = 0

    fun getProduct(): ArrayList<Product> {
        return productArr
    }

    fun addProduct(product: Product){
        productArr.add(product)
        total++
    }

    fun delProduct(product: Product){
        productArr.removeAt(productArr.indexOf(product))
        total--
    }

}