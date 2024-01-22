package com.example.jiratimer

import com.intellij.openapi.project.Project
import kotlinx.coroutines.*

class ProjectTimer() {
    private lateinit var project: Project
    private var job: Job? = null
    private var timeElapsedMap = mutableMapOf<String, Int>()
    private var currentBranch = ""
    var onTimeElapsed: ((Int) -> Unit)? = null

    fun setProject(project: Project) {
        this.project = project
    }

    fun switchBranch(newBranch: String) {
        stopTimer()
        currentBranch = newBranch
        startTimer()
    }

    private fun startTimer() {
        var timeElapsed = timeElapsedMap.getOrDefault(currentBranch, 0)
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                timeElapsed++
                timeElapsedMap[currentBranch] = timeElapsed
                onTimeElapsed?.invoke(timeElapsed)
                println("Timer is running for branch $currentBranch: $timeElapsed seconds")
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        job?.cancel()
        job = null
    }
}
