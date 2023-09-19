package com.uzbekovve.romeojullietaapp.viewmodel

import com.uzbekovve.romeojullietaapp.model.RecordUiModel
import com.uzbekovve.romeojullietaapp.model.Sorting

data class MainViewModelState(val records: List<RecordUiModel>, val isLoading: Boolean, val sortBy: Sorting) {
    fun getLog(): String {
        return "records size: ${records.size}, isLoading: $isLoading, sortBy: $sortBy"
    }
}