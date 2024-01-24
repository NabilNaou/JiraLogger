package com.example.jiratimer

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*

class ProjectTimer(private var project: Project) {
    private val SEC_DELAY: Long = 1000
    private var job: Job? = null
    private var timeElapsedMap = mutableMapOf<String, Int>()
    private var currentBranch = ""
    var onTimeElapsed: ((Int) -> Unit)? = null

    /**
     * Load the data if it exists.
     */
    init {
        loadTimerData()
    }

    /**
     * Set the project (in case of multiple projects).
     */
    fun setProject(project: Project) {
        this.project = project
    }

    /**
     * Switch the timer to a new branch
     */
    fun switchBranch(newBranch: String) {
        stopTimer()
        currentBranch = newBranch
        startTimer()
    }

    /**
     * Get the elapsed time for a specific branch.
     */
    fun getTimeElapsedForBranch(branch: String): Int = timeElapsedMap.getOrDefault(branch, 0)

    /**
     * Start the timer
     */
    private fun startTimer() {
        var timeElapsed = timeElapsedMap.getOrDefault(currentBranch, 0)
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                timeElapsed++
                timeElapsedMap[currentBranch] = timeElapsed
                onTimeElapsed?.invoke(timeElapsed)
                delay(SEC_DELAY)
                saveTimerData()
            }
        }
    }

    /**
     * Stop the job timer, and save data.
     */
    fun stopTimer() {
        job?.cancel()
        job = null
        saveTimerData()
    }

    /**
     * Load saved timer data from persistent storage on startup.
     */
    private fun loadTimerData() {
        val propertiesComponent = PropertiesComponent.getInstance()
        val keyPrefix = "project_timer_${project.name}"
        val branchNames = propertiesComponent.getValue("$keyPrefix.branches")

        if (!branchNames.isNullOrBlank()) {
            branchNames.split(',').forEach { branch ->
                val elapsedTimeInSeconds = propertiesComponent.getInt("$keyPrefix.$branch", 0)
                timeElapsedMap[branch] = elapsedTimeInSeconds
            }
        }
    }

    /**
     * Save timer data to persistent storage.
     */
    private fun saveTimerData() {
        val propertiesComponent = PropertiesComponent.getInstance()
        val keyPrefix = "project_timer_${project.name}"

        // Save branch names to elapsed map
        val branchNames = timeElapsedMap.keys.joinToString(",")
        propertiesComponent.setValue("$keyPrefix.branches", branchNames)

        // Save elapsed times for each branch to elapsed map
        timeElapsedMap.forEach { (branch, elapsedTime) ->
            propertiesComponent.setValue("$keyPrefix.$branch", elapsedTime.toString())
        }
    }
}
