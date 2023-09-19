package com.uzbekovve.romeojullietaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uzbekovve.romeojullietaapp.data.AppRepository
import com.uzbekovve.romeojullietaapp.model.RecordUiModel
import com.uzbekovve.romeojullietaapp.model.Sorting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
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
    private val appRepository = AppRepository(application)

    init {
        viewModelScope.launch {
            appRepository.loadData().onEach {
                processLine(it)
            }.onCompletion {
                onTextProcessingEnded()
            }.collect()
        }
    }

    private fun processLine(text: String) {
        val words = text.split(" ").map {
            alphabeticRegex.replace(it.lowercase(), "")
        }
        words.forEach { word ->
            wordsMap[word] = wordsMap.getOrDefault(word, 0).inc()
        }
    }

    private fun onTextProcessingEnded() {
        Log.d("MainActivity", "onTextProcessingEnded")
        onDataChanged()
    }

    private fun onDataChanged() {
        val sortedMap = wordsMap.sort(screenState.value.sortBy)
        val records = sortedMap.toList().map { RecordUiModel(it.first, it.second) }
        _screenState.value = screenState.value.copy(records = records, isLoading = false)
    }

    fun changeSorting(type: Sorting) {
        if (screenState.value.isLoading) return
        _screenState.value = screenState.value.copy(sortBy = type, isLoading = true)
        onDataChanged()
    }

    private fun HashMap<String, Int>.sort(sorting: Sorting): Map<String, Int> {
        if (sorting == Sorting.BY_OCCURRENCES) {
            return toList().sortedByDescending { (_, value) -> value }.toMap()
        }
        return toSortedMap { p0, p1 ->
            if (sorting == Sorting.BY_LENGTH && p0.length != p1.length) {
                p0.length.compareTo(p1.length)
            } else {
                p0.compareTo(
                    p1,
                )
            }
        }
    }
}
