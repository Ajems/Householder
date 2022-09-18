package com.example.householder.data

import androidx.lifecycle.ViewModel

class ProductViewModel: ViewModel() {

    private var productArr = ArrayList<Product>()

    fun getProductArr(): ArrayList<Product> {
        return productArr
    }

    fun setProductArr(productArr: ArrayList<Product>){
        this.productArr = ArrayList<Product>()
        productArr.forEach { addProduct(it, arrSize()) }
    }

    fun addProduct(product: Product, index: Int = 0){
        productArr.add(index, product)
    }

    fun summarizeProduct(product: Product){
        productArr.forEach {
            if(it.name == product.name){
                it.count+=product.count
                return
            }
        }
    }

    fun delProductIndex(index: Int){
        productArr.removeAt(index)
    }

    fun delProduct(product: Product){
        productArr.remove(product)
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