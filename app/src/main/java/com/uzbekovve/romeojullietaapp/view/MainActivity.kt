package com.uzbekovve.romeojullietaapp.view

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.uzbekovve.romeojullietaapp.R
import com.uzbekovve.romeojullietaapp.model.Sorting
import com.uzbekovve.romeojullietaapp.recycler.RecordsRecyclerViewAdapter
import com.uzbekovve.romeojullietaapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    companion object {
        private const val FILE_NAME = "Romeo-and-Juliet.txt"
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private val viewScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val dataScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var switchLayout: LinearLayout
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = RecordsRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchLayout = findViewById(R.id.switchLayout)
        recyclerView = findViewById(R.id.wordsRecyclerView)
        recyclerView.adapter = adapter
        progressBar = findViewById(R.id.progressbar)
        toggleGroup = findViewById(R.id.toggleGroup)
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked.not()) return@addOnButtonCheckedListener
            viewModel.changeSorting(
                when (checkedId) {
                    R.id.btn_alphabet -> Sorting.BY_ALPHABET
                    R.id.btn_length -> Sorting.BY_LENGTH
                    R.id.btn_occurrences -> Sorting.BY_OCCURRENCES
                    else -> throw IllegalArgumentException("Unknown id")
                },
            )
        }
        readFile()
    }

    override fun onStart() {
        super.onStart()
        viewScope.launch {
            viewModel.screenState.collectLatest { state ->
                Log.d(TAG, state.getLog())
                progressBar.isVisible = state.isLoading
                switchLayout.isVisible = state.isLoading.not()
                recyclerView.isVisible = state.isLoading.not()
                toggleGroup.check(
                    when (state.sortBy) {
                        Sorting.BY_ALPHABET -> R.id.btn_alphabet
                        Sorting.BY_LENGTH -> R.id.btn_length
                        Sorting.BY_OCCURRENCES -> R.id.btn_occurrences
                        else -> throw IllegalArgumentException("Unknown Sorting")
                    },
                )
                adapter.submitList(state.records)
            }
        }
    }

    private fun readFile() {
        dataScope.launch {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(
                    InputStreamReader(assets.open(FILE_NAME), "UTF-8"),
                )

                while (true) {
                    val line = reader.readLine()
                    if (line.isNullOrEmpty()) {
                        viewModel.onTextProcessingEnded()
                        return@launch
                    }
                    viewModel.processLine(line)
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.orEmpty())
            } finally {
                try {
                    reader?.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.orEmpty())
                }
            }
        }.start()
    }

    override fun onStop() {
        super.onStop()
        viewScope.coroutineContext.cancelChildren()
    }

    override fun onDestroy() {
        super.onDestroy()
        dataScope.coroutineContext.cancelChildren()
    }
}
