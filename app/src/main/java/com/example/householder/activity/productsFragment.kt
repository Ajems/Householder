package com.example.householder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.householder.data.Product
import com.example.householder.data.ProductViewModel
import com.example.householder.databinding.FragmentProductsBinding
import com.example.householder.databinding.FragmentShoppingBinding
import com.example.householder.databinding.ProductItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.format.DateTimeFormatter


class productsFragment  : Fragment() {

    //private var productArr = arrayListOf<Product>()
    private val productViewModel: ProductViewModel by lazy {
        ViewModelProviders.of(this).get(ProductViewModel::class.java)
    }
    private val productArr = ArrayList<Product>()
    private var adapter = ProductAdapter()
    private lateinit var _binding: FragmentProductsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        binding.productList.layoutManager = LinearLayoutManager(this.context)
        binding.productList.adapter = adapter
        binding.buttonAdd.setOnClickListener {
            addProduct()
        }
        return binding.root
    }



    private inner class ProductHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ProductItemBinding.bind(view)
        private lateinit var product: Product

        @SuppressLint("SetTextI18n")
        fun bind(index: Int){
            this.product = adapter.getProductIndex(index)
            binding.name.text = product.name
            binding.count.text = product.count.toString()
            //binding.time.text = product.time.format(DateTimeFormatter.ofPattern("HH:mm dd-mm-yy"))

            binding.remove.setOnClickListener {
                if (product.count == 1){
                    val size = adapter.arrSize()
                    dialogDeleting(product, adapterPosition)
                } else {
                    product.count--
                    binding.count.text = product.count.toString()
                }
            }

            binding.name.setOnClickListener{
                addProduct(product.name, product.count, index)
            }

            binding.add.setOnClickListener {
                product.count++
                binding.count.text = product.count.toString()
            }

            binding.name.setOnLongClickListener{
                val size = adapter.arrSize()
                dialogDeleting(product, adapterPosition)
                true
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
        fun addProduct(product: Product, index: Int = 0){
            productArr.add(adapter.arrSize() - 1, product)
            productViewModel.addProduct(product)
            notifyItemInserted(adapter.arrSize() - 1)
        }

        fun addProductIndex(product: Product, index: Int = 0){
            productArr.add(index, product)
            productViewModel.addProduct(product)
            notifyItemInserted(index)
        }

        fun getProductIndex(index: Int): Product{
            return productArr[index]
        }

        fun delProduct(product: Product){
            productArr.remove(product)
            productViewModel.delProduct(product)
        }

        fun isEmpty(): Boolean{
            return productArr.isEmpty()
        }

        fun setArr(arr: ArrayList<Product>){
            arr.forEach { productArr.add(it) }
        }

        fun arrSize(): Int{
            return productArr.size
        }

        fun delProductIndex(index: Int){
            productArr.removeAt(index)
            adapter.notifyItemRemoved(index)
            productViewModel.delProductIndex(index)
        }

        override fun getItemCount(): Int {
            return productArr.size
        }

    }

    private fun dialogDeleting(product: Product, position: Int){
        activity?.let { it1 ->
            MaterialAlertDialogBuilder(it1)
                .setTitle("Deleting a product")
                .setMessage("Are you sure you want to remove ${product.count} ${if(product.count > 1) "${product.name}\'s" else product.name} from the product list?")
                .setPositiveButton("Ok"){dialog, which ->
                    adapter.delProduct(product)
                    adapter.notifyItemRemoved(position)
                }
                .setNegativeButton("Cancel"){dialog, which ->

                }
                .show()
        }
    }


    @SuppressLint("CutPasteId")
    private fun addProduct(name: String = "", count: Int = 0, index: Int = -1) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.add_product_dialog, null)
        val builder = AlertDialog.Builder(activity)
            .setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val inputName = alertDialog.findViewById<TextInputEditText>(R.id.product_name_filed)
        val inputCount = alertDialog.findViewById<TextInputEditText>(R.id.product_count_field)
        if (name != ""){
            inputName.setText(name)
            inputCount.setText(count.toString())
        }
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
                    initProduct(productName, productCount, index)
                    return@OnEditorActionListener true
                }
                false
            }
        )

        alertDialog.findViewById<TextView>(R.id.button_apply).setOnClickListener {
            alertDialog.dismiss()
            initProduct(inputName, inputCount, index)
        }

        alertDialog.findViewById<TextView>(R.id.button_cancel).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun initProduct(productName: EditText, productCount: EditText, index: Int = -1) {
        if (!productName.text.isNullOrEmpty() && !productCount.text.isNullOrEmpty()) {
            val name = productName.text.toString()
            val count = productCount.text.toString().toInt()
            if (name != "" && count > 0) {
                val newProduct = Product(name, count)
                if (index > -1){
                    adapter.delProductIndex(index)
                    adapter.addProductIndex(newProduct, index)
                }else {
                    adapter.addProductIndex(newProduct)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
        if (adapter.isEmpty()) { adapter.setArr(productViewModel.getProductArr()) }
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

}