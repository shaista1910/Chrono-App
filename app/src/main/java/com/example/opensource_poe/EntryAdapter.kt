package com.example.opensource_poe
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.squareup.picasso.Picasso

class EntryAdapter(private val onItemClick: (Entry) -> Unit) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {
    private var entries: List<Entry> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timesheet_list_item, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.bind(entry)

        holder.itemView.setOnClickListener {
            onItemClick(entry)
        }
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    fun setEntries(entries: List<Entry>) {
        this.entries = entries
        notifyDataSetChanged()
    }


    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView: TextView = itemView.findViewById(R.id.catTV)
        private val descTextView: TextView = itemView.findViewById(R.id.descTV)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTV)
        private val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTV)
        private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTV)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTV)
        private val imageViews: ImageView = itemView.findViewById(R.id.imageView3)


        fun bind(entry: Entry) {
            categoryTextView.text = entry.category
            descTextView.text = entry.description
            dateTextView.text = entry.date
            startTimeTextView.text = entry.startTime
            endTimeTextView.text = entry.endTime
            durationTextView.text = "${entry.duration} minutes"


            if (entry.imageUrl != null) {
                Picasso.get()
                    .load(entry.imageUrl)
                    .into(imageViews)
            }
        }
    }
}
