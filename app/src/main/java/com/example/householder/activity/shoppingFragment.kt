package com.example.householder.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.householder.R
import com.example.householder.data.Product
import com.example.householder.data.ProductViewModel
import com.example.householder.databinding.FragmentShoppingBinding
import com.example.householder.databinding.ProductItemBinding
import com.google.android.material.textfield.TextInputEditText


class shoppingFragment : Fragment() {

    //private var productArr = arrayListOf<Product>()
    private val productViewModel: ProductViewModel by lazy {
        ViewModelProviders.of(this).get(ProductViewModel::class.java)
    }
    private var adapter = ProductAdapter()
    private lateinit var _binding: FragmentShoppingBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingBinding.inflate(inflater, container, false)
        binding.shopingList.layoutManager = LinearLayoutManager(this.context)
        binding.shopingList.adapter = adapter
        binding.buttonAdd.setOnClickListener {
            addProduct()
        }

        return binding.root
    }

    fun updateUI(productArr: ArrayList<Product>){
        binding.shopingList.adapter = adapter
    }

    private inner class ProductHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ProductItemBinding.bind(view)
        private lateinit var product: Product

        @SuppressLint("SetTextI18n")
        fun bind(index: Int, productArr: ArrayList<Product>){
            this.product = productArr[index]
            binding.name.text = product.name
            binding.count.text = product.count.toString()

            binding.remove.setOnClickListener {
                binding.count.text = ((binding.count.text as String).toInt()-1).toString()
                product.count-=1
                if (product.count == 0){
                    productViewModel.delProduct(product)
                    //productArr.removeAt(productArr.indexOf(product))
                    updateUI(productViewModel.productArr)
            }

            binding.add.setOnClickListener {
                binding.count.text = ((binding.count.text as String).toInt()+1).toString()
            }
        }
    }
    }

    private inner class ProductAdapter():
        RecyclerView.Adapter<ProductHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
            return ProductHolder(view)
        }

        override fun onBindViewHolder(holder: ProductHolder, position: Int) {
            holder.bind(position, productViewModel.productArr)
        }

        fun addProduct(product: Product){
            productViewModel.productArr.add(0, product)
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun delProductIndex(position: Int){
            productViewModel.productArr.removeAt(position)
            notifyDataSetChanged()
        }

        fun delProductName(name: String){
            //TODO оптимизировать 26.08.22
            delProductIndex(productViewModel.productArr.indexOf(productViewModel.productArr.find{ it.name == name}))
        }

        fun isEmpty(): Boolean{
            return productViewModel.productArr.isEmpty()
        }


        override fun getItemCount(): Int {
            return productViewModel.productArr.size
        }

    }


    @SuppressLint("CutPasteId")
    private fun addProduct() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.add_product_dialog, null)
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alertDialog.show()

        val inputName = alertDialog.findViewById<TextInputEditText>(R.id.product_name_filed)
        val inputCount = alertDialog.findViewById<TextInputEditText>(R.id.product_count_field)
        inputName.requestFocus()


        inputName.setOnEditorActionListener(
            TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    inputCount.requestFocus()
                    return@OnEditorActionListener true
                }
                false
            }
        )

        inputCount.setOnEditorActionListener(
            TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    alertDialog.dismiss()
                    val productName = alertDialog.findViewById<EditText>(R.id.product_name_filed)
                    val productCount = alertDialog.findViewById<EditText>(R.id.product_count_field)
                    initProduct(productName, productCount)
                    return@OnEditorActionListener true
                }
                false
            }
        )

        alertDialog.findViewById<TextView>(R.id.button_apply).setOnClickListener {
            alertDialog.dismiss()
            initProduct(inputName, inputCount)
        }

        alertDialog.findViewById<TextView>(R.id.button_cancel).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun initProduct(productName: EditText, productCount: EditText) {
        if (!productName.text.isNullOrEmpty() && !productCount.text.isNullOrEmpty()) {
            val name = productName.text.toString()
            val count = productCount.text.toString().toInt()
            if (name != "" && count > 0) {
                val newProduct = Product(name, count)
                productViewModel.addProduct(newProduct)
                updateUI(productViewModel.productArr)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (adapter.isEmpty()) productViewModel.getProduct().forEach {adapter.addProduct(it) }
    }

    override fun onPause() {
        super.onPause()
    }
}