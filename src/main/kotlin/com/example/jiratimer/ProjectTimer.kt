package com.example.jiratimer

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture

@Service(Service.Level.PROJECT)
class ProjectTimer(private var project: Project) {
    private val SEC_DELAY: Long = 1000
    private var job: Job? = null
    private var timeElapsedMap = mutableMapOf<String, Int>()
    private var _currentBranch = ""
    var onTimeElapsed: ((Int) -> Unit)? = null
    val currentBranch: String
        get() = _currentBranch

    init {
        loadTimerData()
    }

    /**
     * Set the current project (for the timer).
     */
    fun setProject(project: Project) {
        this.project = project
    }

    /**
     * Push time to jira and convert minutes to seconds.
     */
    fun pushTimeToJira(issueId: String, editedTime: Int): CompletableFuture<Boolean> {
        val jiraApiClient = JiraApiClient()
        return if (editedTime > 0) {
            jiraApiClient.logTime(issueId, editedTime * 60)
        } else {
            CompletableFuture.completedFuture(false)
        }
    }

    /**
     * Reset our time for specificed branch. Used for resetting after committing.
     */
    fun resetTimeForBranch(branch: String) {
        stopTimer()
        timeElapsedMap[branch] = 0
        saveTimerData()
        onTimeElapsed?.invoke(0)
        startTimer()
    }

    /**
     * Switch the active branch (for the timer).
     */
    fun switchBranch(newBranch: String) {
        stopTimer()
        _currentBranch = newBranch
        startTimer()
    }

    /**
     * Get the elapsed time for a specific branch.
     */
    fun getTimeElapsedForBranch(branch: String): Int = timeElapsedMap.getOrDefault(branch, 0)

    /**
     * Start our timer, and start our coroutine for elapsing time.
     */
    private fun startTimer() {
        stopTimer()

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
     * Stop the timer.
     */
    fun stopTimer() {
        job?.cancel()
        job = null
    }

    /**
     * Load the timer data from storage. Populates the timeElapsedMap with elapsed times
     * for each branch that has been stored.
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
     * Save the timer data to storage. Maps the branch names and their elapsed times
     * for later use.
     */
    private fun saveTimerData() {
        val propertiesComponent = PropertiesComponent.getInstance()
        val keyPrefix = "project_timer_${project.name}"

        val branchNames = timeElapsedMap.keys.joinToString(",")
        propertiesComponent.setValue("$keyPrefix.branches", branchNames)

        timeElapsedMap.forEach { (branch, elapsedTime) ->
            propertiesComponent.setValue("$keyPrefix.$branch", elapsedTime.toString())
        }
    }

    /**
     * Get the set of branches that have been stored.
     */
    fun getStoredBranches(): Set<String> {
        val propertiesComponent = PropertiesComponent.getInstance()
        val keyPrefix = "project_timer_${project.name}"
        val branchNames = propertiesComponent.getValue("$keyPrefix.branches")

        return if (!branchNames.isNullOrBlank()) {
            branchNames.split(',').toSet()
        } else {
            emptySet()
        }
    }

    /**
     * Remove data associated with a specific branch.
     */
    fun removeBranchData(branch: String) {
        val propertiesComponent = PropertiesComponent.getInstance()
        val keyPrefix = "project_timer_${project.name}"

        // Remove branch name first.
        val storedBranches = getStoredBranches().toMutableSet()
        storedBranches.remove(branch)
        propertiesComponent.setValue("$keyPrefix.branches", storedBranches.joinToString(","))

        // Remove time data.
        propertiesComponent.unsetValue("$keyPrefix.$branch")
    }
}
