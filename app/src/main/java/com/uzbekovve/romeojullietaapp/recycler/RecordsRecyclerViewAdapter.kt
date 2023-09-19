package com.uzbekovve.romeojullietaapp.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uzbekovve.romeojullietaapp.R
import com.uzbekovve.romeojullietaapp.model.RecordUiModel

class RecordsRecyclerViewAdapter :
    ListAdapter<RecordUiModel, RecordsRecyclerViewAdapter.RecordsViewHolder>(
        RecordUiModel.DiffUtilCallback,
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        return RecordsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false),
        )
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordTextView = itemView.findViewById<TextView>(R.id.wordTextView)
        private val countTextView = itemView.findViewById<TextView>(R.id.occurrenceTextView)

        fun bind(item: RecordUiModel) {
            wordTextView.text = item.word
            countTextView.text = item.occurrenceCount.toString()
        }
    }
}
