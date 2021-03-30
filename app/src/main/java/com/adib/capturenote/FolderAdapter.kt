package com.adib.capturenote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(private val context: Context, private val items: ArrayList<Folder>):
    RecyclerView.Adapter<FolderAdapter.FolderHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class FolderHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvFolderName: TextView = view.findViewById(R.id.tv_folder_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        return FolderHolder(
            LayoutInflater.from(context).inflate(R.layout.card_folder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val item = items[position]
        holder.tvFolderName.text = item.name

        holder.itemView.setOnClickListener {onItemClickCallback.onItemClicked(items[holder.adapterPosition])}
    }

    override fun getItemCount(): Int = items.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Folder)
    }
}