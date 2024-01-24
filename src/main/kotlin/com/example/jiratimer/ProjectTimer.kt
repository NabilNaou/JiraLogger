package com.example.jiratimer

import com.intellij.openapi.project.Project
import kotlinx.coroutines.*

class ProjectTimer(private var project: Project) {
    private val SEC_DELAY: Long = 1000
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

    fun getTimeElapsedForBranch(branch: String): Int = timeElapsedMap.getOrDefault(branch, 0)

    private fun startTimer() {
        var timeElapsed = timeElapsedMap.getOrDefault(currentBranch, 0)
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                timeElapsed++
                timeElapsedMap[currentBranch] = timeElapsed
                onTimeElapsed?.invoke(timeElapsed)
                delay(SEC_DELAY)
            }
        }
    }

    fun stopTimer() {
        job?.cancel()
        job = null
    }
}