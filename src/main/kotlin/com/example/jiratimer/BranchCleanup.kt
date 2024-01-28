package com.example.jiratimer

import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import java.io.BufferedReader
import java.io.InputStreamReader

class BranchCleanup(private val project: Project, private val projectTimer: ProjectTimer, private val widget: TimerStatusBarWidget) {
    private val cleanupIntervalHours = 2L

    /**
     * Start a cleanup scheduler that runs every 2 days to old branches that dont exist any more.
     */
    fun startCleanupScheduler() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                cleanup()
                delayUntilNextCleanup()
            }
        }
    }

    /**
     * Remove branches that no longer exist locally.
     * The flow is: fetching a list of all local branches, getting branches stored in
     * [ProjectTimer] and comparing local branches from the earlier list. The outdated branches
     * are deleted to save storage.
     */
    private fun cleanup() {
        val localBranches = getLocalGitBranches()

        val storedBranches = projectTimer.getStoredBranches()

        val branchesToRemove = storedBranches.filter { it !in localBranches }

        branchesToRemove.forEach { branch ->
            projectTimer.removeBranchData(branch)
        }
    }

    /**
     * Get a list of all local Git branches with the git branch command.
     * Note: git4idea is incompatible, so branch commands seemed like the only solution(?)
     */
    private fun getLocalGitBranches(): List<String> {
        val processBuilder = ProcessBuilder("git", "branch")
        processBuilder.directory(project.basePath?.let { java.io.File(it) })
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val lines = reader.readLines()
        reader.close()
        return lines.map { it.trimStart('*').trim() }
    }

    /**
     * Delay the cleanup until the next session.
     */
    private suspend fun delayUntilNextCleanup() {
        val intervalMillis = TimeUnit.HOURS.toMillis(cleanupIntervalHours)
        delay(intervalMillis)
    }
}