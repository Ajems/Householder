package com.example.householder.data

import androidx.lifecycle.ViewModel

class ProductViewModel: ViewModel() {

    var productsArr = ArrayList<Product>()
    private var total = 0

    fun getProduct(): ArrayList<Product> {
        return productsArr
    }

    fun addProduct(product: Product){
        productsArr.add(product)
        total++
    }

    fun delProduct(product: Product){
        productsArr.removeAt(productsArr.indexOf(product))
        total--
    }

}