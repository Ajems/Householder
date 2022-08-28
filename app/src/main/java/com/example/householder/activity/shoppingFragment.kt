package com.example.householder.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.householder.R
import com.example.householder.data.Product
import com.example.householder.data.ProductViewModel
import com.example.householder.databinding.FragmentShoppingBinding
import com.example.householder.databinding.ProductItemBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


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

        if (!isDarkModeOn()) activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        return binding.root
    }

    fun updateUI(productArr: ArrayList<Product>){
        binding.shopingList.adapter = adapter
    }

    private inner class ProductHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ProductItemBinding.bind(view)
        private lateinit var product: Product

        @SuppressLint("SetTextI18n")
        fun bind(index: Int){
            this.product = productViewModel.getProduct(index)
            binding.name.text = product.name
            binding.count.text = product.count.toString()

            binding.remove.setOnClickListener {
                product.count -= 1
                binding.count.text = product.count.toString()
                if (product.count == 0) {
                    productViewModel.delProduct(product)
                    updateUI(productViewModel.getProductArr())
                }
            }

            binding.add.setOnClickListener {
                product.count++
                binding.count.text = product.count.toString()
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
            holder.bind(position)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun addProduct(product: Product){
            productViewModel.addProduct(product)
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun delProductIndex(position: Int){
            productViewModel.delProductIndex(position)
            notifyDataSetChanged()
        }

        fun delProductName(name: String){
            //TODO оптимизировать 26.08.22
            delProductName(name)
        }

        fun isEmpty(): Boolean{
            return productViewModel.isEmpty()
        }


        override fun getItemCount(): Int {
            return productViewModel.arrSize()
        }

    }


    @SuppressLint("CutPasteId")
    private fun addProduct() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.add_product_dialog, null)
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                updateUI(productViewModel.getProductArr())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
        if (adapter.isEmpty()) productViewModel.getProductArr().forEach {adapter.addProduct(it) }
    }

    override fun onStop() {
        saveData()
        super.onStop()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
    }

    private fun saveData(){
        val sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(productViewModel.getProductArr())
        editor.putString("product list", json)
        editor.apply()
    }

    private fun loadData(){
        val sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("product list", null)
        val type: Type = object: TypeToken<ArrayList<Product>>(){}.type
        val productArr = gson.fromJson<ArrayList<Product>>(json, type)

        if (productArr == null) productViewModel.setProductArr(ArrayList<Product>())
        else if(productArr.size > 0) productViewModel.setProductArr(productArr)
        else productViewModel.setProductArr(arrayListOf<Product>())
    }

    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

}