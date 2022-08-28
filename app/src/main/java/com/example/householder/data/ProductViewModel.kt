package com.example.householder.data

import androidx.lifecycle.ViewModel

class ProductViewModel: ViewModel() {

    private var productArr = ArrayList<Product>()
    private var total = 0

    fun getProductArr(): ArrayList<Product> {
        return productArr
    }

    fun setProductArr(productArr: ArrayList<Product>){
        this.productArr = ArrayList<Product>()
        productArr.forEach { addProduct(it) }
    }

    fun addProduct(product: Product){
        productArr.add(product)
        total++
    }

    fun delProductIndex(index: Int){
        productArr.removeAt(index)
        total--
    }

    fun delProduct(product: Product){
        delProductIndex(productArr.indexOf(product))
        total--
    }

    fun isEmpty(): Boolean{
        return productArr.isEmpty()
    }

    fun arrSize(): Int{
        return productArr.size
    }

    fun getProduct(index: Int): Product{
        return productArr[index]
    }


}