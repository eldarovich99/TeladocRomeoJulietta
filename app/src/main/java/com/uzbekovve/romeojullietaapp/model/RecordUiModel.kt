package com.uzbekovve.romeojullietaapp.model

import androidx.recyclerview.widget.DiffUtil

class RecordUiModel(val word: String, val occurrenceCount: Int) {

    object DiffUtilCallback : DiffUtil.ItemCallback<RecordUiModel>() {
        override fun areItemsTheSame(
            oldItem: RecordUiModel,
            newItem: RecordUiModel,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RecordUiModel,
            newItem: RecordUiModel,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
