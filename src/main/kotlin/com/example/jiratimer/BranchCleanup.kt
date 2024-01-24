package com.example.jiratimer

import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class BranchCleanup(private val project: Project, private val projectTimer: ProjectTimer, private val widget: TimerStatusBarWidget) {
    private val cleanupIntervalDays = 3

    /**
     * Periodically check for branches to delete from our storage.
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
     * Remove branches that do not exist anymore.
     * Flow is: Get a list of all local branches -> Get branches from our storage -> identify what branches are gone
     * locally -> delete them from storage.
     */
    private fun cleanup() {
        val manager = GitRepositoryManager.getInstance(project)
        val repositories = manager.repositories

        // Get a list of all local branch names from Git repos in project.
        val localBranches = repositories.flatMap { it.branches.localBranches.map { it.name } }

        // Get the branches stored in your data storage
        val storedBranches = projectTimer.getStoredBranches()

        // Identify branches that no longer exist locally but are still in storage
        val branchesToRemove = storedBranches.filter { it !in localBranches }

        // Remove the associated data for the branches
        branchesToRemove.forEach { branch ->
            projectTimer.removeBranchData(branch)
        }
    }

    /**
     * Delay until next cleanup session.
     */
    private suspend fun delayUntilNextCleanup() {
        val intervalMillis = TimeUnit.DAYS.toMillis(cleanupIntervalDays.toLong())
        delay(intervalMillis)
    }
}
