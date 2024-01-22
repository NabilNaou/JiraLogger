package com.example.jiratimer

import com.intellij.openapi.project.Project
import kotlinx.coroutines.*

class ProjectTimer() {
    private lateinit var project: Project
    private var job: Job? = null
    var onTimeElapsed: ((Int) -> Unit)? = null

    fun setProject(project: Project) {
        this.project = project
    }

    fun startTimer() {
        job = CoroutineScope(Dispatchers.Main).launch {
            var timeElapsed = 0
            while (isActive) {
                onTimeElapsed?.invoke(timeElapsed)
                timeElapsed++
                println(timeElapsed)
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        job?.cancel()
    }
}
