package com.uzbekovve.romeojullietaapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.uzbekovve.romeojullietaapp.model.RecordUiModel
import com.uzbekovve.romeojullietaapp.model.Sorting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.SortedMap

class MainViewModel : ViewModel() {
    private val _screenState = MutableStateFlow(
        MainViewModelState(
            listOf(),
            isLoading = true,
            sortBy = Sorting.BY_OCCURRENCES,
        ),
    )
    val screenState: StateFlow<MainViewModelState> = _screenState.asStateFlow()

    private val wordsMap = HashMap<String, Int>()
    private val alphabeticRegex = Regex("[^A-Za-z0-9 ]")

    fun processLine(text: String) {
        val words = text.split(" ").map {
            alphabeticRegex.replace(it.lowercase(), "") // works
        }
        words.forEach { word ->
            wordsMap[word] = wordsMap.getOrDefault(word, 0).inc()
        }
    }

    fun onTextProcessingEnded() {
        Log.d("MainActivity", "onTextProcessingEnded")
        onDataChanged()
    }

    private fun onDataChanged() {
        val sortedMap = wordsMap.sortByDescending(screenState.value.sortBy)
        val records = sortedMap.toList().map { RecordUiModel(it.first, it.second) }
        _screenState.value = screenState.value.copy(records = records, isLoading = false)
    }

    fun changeSorting(type: Sorting) {
        if (screenState.value.isLoading) return
        _screenState.value = screenState.value.copy(sortBy = type, isLoading = true)
        onDataChanged()
    }

    fun HashMap<String, Int>.sortByDescending(sorting: Sorting): Map<String, Int> {
        if (sorting == Sorting.BY_OCCURRENCES) {
            return toList().sortedByDescending { (_, value) -> value }.toMap()
        }
        return toSortedMap { p0, p1 ->
            if (sorting == Sorting.BY_LENGTH && p0.length != p1.length) {
                p1.length.compareTo(p0.length)
            } else {
                p1.compareTo(
                    p0,
                )
            }
        }
    }
}
