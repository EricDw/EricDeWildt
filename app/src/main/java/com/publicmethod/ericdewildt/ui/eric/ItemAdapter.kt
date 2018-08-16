package com.publicmethod.ericdewildt.ui.eric

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.publicmethod.ericdewildt.R
import com.publicmethod.ericdewildt.data.Skill

class ItemAdapter(
    private var items: List<Skill> = listOf()
) : RecyclerView.Adapter<ItemViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder = ItemViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.skill_card,
                parent,
                false
            ) as MaterialCardView
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.skill_name).text = items[position]
    }

    fun updateItems(items: List<Skill>) {
        this.items = items
        notifyDataSetChanged()
    }

}