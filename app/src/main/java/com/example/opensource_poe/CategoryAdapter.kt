package com.example.opensource_poe
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var categories: List<Category> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun setCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView: TextView = itemView.findViewById(R.id.catTitle)
        private val minHoursTextView: TextView = itemView.findViewById(R.id.minTxt)
        private val maxHoursTextView: TextView = itemView.findViewById(R.id.maxTxt)

        fun bind(category: Category) {
            categoryTextView.text = category.categoryName
            minHoursTextView.text = "Minimum hours: ${category.minHours}"
            maxHoursTextView.text = "Maximum hours: ${category.maxHours}"
        }
    }
}
