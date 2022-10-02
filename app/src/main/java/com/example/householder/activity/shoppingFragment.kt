package com.example.householder.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class shoppingFragment : Fragment() {

    private val shoppingViewModel: ProductViewModel by lazy {
        ViewModelProviders.of(this).get(ProductViewModel::class.java)
    }
    private var adapter = ShoppingAdapter()
    private lateinit var _binding: FragmentShoppingBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingBinding.inflate(inflater, container, false)
        binding.shoppingList.layoutManager = LinearLayoutManager(this.context)
        binding.shoppingList.adapter = adapter
        binding.buttonAdd.setOnClickListener {
            dialogAddProduct()
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

            binding.remove.setOnClickListener {
                if (product.count == 1){
                    dialogDeleting(product, adapterPosition)
                } else {
                    product.count--
                    binding.count.text = product.count.toString()
                }
            }

            binding.add.setOnClickListener {
                product.count++
                binding.count.text = product.count.toString()
            }

            binding.name.setOnLongClickListener{
                dialogDeleting(product, adapterPosition)
                true
            }

        }
    }

    private inner class ShoppingAdapter(): RecyclerView.Adapter<ProductHolder>(){

        private val shoppingArr = ArrayList<Product>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
            return ProductHolder(view)
        }

        override fun onBindViewHolder(holder: ProductHolder, position: Int) {
            holder.bind(position)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun addProduct(product: Product, index: Int = 0){
            shoppingArr.add(index, product)
            shoppingViewModel.addProduct(product, index)
            notifyItemInserted(index)
        }

        fun delProduct(product: Product){
            val index = shoppingArr.indexOf(product)
            shoppingArr.remove(product)
            shoppingViewModel.delProduct(product)
            notifyItemRemoved(index)
        }

        fun delProductIndex(index: Int){
            shoppingArr.removeAt(index)
            shoppingViewModel.delProductIndex(index)
            notifyItemRemoved(index)
        }

        fun setAdapterArr(arr: ArrayList<Product>){
            arr.forEach { shoppingArr.add(it) }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun summarizeProduct(product: Product){
            shoppingArr.forEach {
                if(it.name == product.name){
                    it.count+=product.count
                    adapter.notifyDataSetChanged()
                    return
                }
            }
            shoppingViewModel.summarizeProduct(product)
        }

        fun getProductIndex(index: Int): Product{
            return shoppingArr[index]
        }

        fun isProductInArr(product: Product): Boolean{
            return shoppingArr.find { it.name == product.name } != null
        }

        fun isEmpty(): Boolean{
            return shoppingArr.isEmpty()
        }

        fun arrSize(): Int{
            return shoppingArr.size
        }

        override fun getItemCount(): Int {
            return shoppingArr.size
        }

    }


    @SuppressLint("CutPasteId")
    private fun dialogAddProduct() {
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
            val name = productName.text.toString().trim()
            val count = productCount.text.toString().toInt()
            if (name != "" && count > 0) {
                val newProduct = Product(name, count)
                if(adapter.isProductInArr(newProduct)){
                    dialogAddSameProduct(newProduct)
                } else adapter.addProduct(newProduct)
            }
        }
    }


    private fun dialogDeleting(product: Product, position: Int){
        activity?.let { it1 ->
            MaterialAlertDialogBuilder(it1)
                .setTitle("Deleting a product")
                .setMessage("Are you sure you want to remove ${product.count} ${if(product.count > 1) "${product.name}\'s" else product.name} from the shopping list?")
                .setPositiveButton("Ok"){dialog, which ->
                    adapter.delProduct(product)
                }
                .setNegativeButton("Cancel"){dialog, which ->
                }
                .setNeutralButton("Bought") { dialog, which ->
                    adapter.delProduct(product)
                    //TODO product to product list
                }.show()
        }
    }

    private fun dialogAddSameProduct(product: Product){
        activity?.let { it ->
            MaterialAlertDialogBuilder(it)
                .setTitle("Same product")
                .setMessage(getString(R.string.alreadyExistName))
                .setPositiveButton("Summarize"){ dialog, which ->
                    adapter.summarizeProduct(product)
                }
                .setNeutralButton("Add new"){dialog, which ->
                    adapter.addProduct(product)
                }
                .setNegativeButton("Cancel"){dialog, which ->}
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
        if (adapter.isEmpty()) { adapter.setAdapterArr(shoppingViewModel.getProductArr()) }    }

    override fun onStop() {
        saveData()
        super.onStop()
    }

    private fun saveData(){
        val sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(shoppingViewModel.getProductArr())
        editor.putString("shopping list", json)
        editor.apply()
    }

    private fun loadData(){
        val sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("shopping list", null)
        val type: Type = object: TypeToken<ArrayList<Product>>(){}.type
        val productArr = gson.fromJson<ArrayList<Product>>(json, type)

        if (productArr == null) shoppingViewModel.setProductArr(ArrayList<Product>())
        else if(productArr.size > 0) shoppingViewModel.setProductArr(productArr)
        else shoppingViewModel.setProductArr(arrayListOf<Product>())
    }


}