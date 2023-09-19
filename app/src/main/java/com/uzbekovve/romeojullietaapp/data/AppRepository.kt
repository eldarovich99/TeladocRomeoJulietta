package com.uzbekovve.romeojullietaapp.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class AppRepository(private val context: Context) {
    companion object {
        private const val FILE_NAME = "Romeo-and-Juliet.txt"
        private const val TAG = "AppRepository"
    }

    fun loadData(): Flow<String> {
        return flow {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(
                    InputStreamReader(context.assets.open(FILE_NAME), "UTF-8"),
                )

                var hasMoreLines = true
                while (hasMoreLines) {
                    val line = reader.readLine()
                    if (line.isNullOrEmpty()) {
                        hasMoreLines = false
                    } else {
                        emit(line)
                    }
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
        }.flowOn(Dispatchers.IO)
    }
}
