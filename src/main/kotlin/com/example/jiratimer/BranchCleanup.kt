package com.example.jiratimer

import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class BranchCleanup(private val project: Project, private val projectTimer: ProjectTimer, private val widget: TimerStatusBarWidget) {
    private val cleanupIntervalHours = 2L

    /**
     * Periodically check for branches to delete from our storage. Every 2 hours we
     * get all branches locally and the ones stores in project timer. Delete ones that
     * are not in our branches. TBD: Make persistent and every day(?).
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

        val localBranches = repositories.flatMap { it.branches.localBranches.map { it.name } }

        val storedBranches = projectTimer.getStoredBranches()

        val branchesToRemove = storedBranches.filter { it !in localBranches }

        branchesToRemove.forEach { branch ->
            projectTimer.removeBranchData(branch)
        }
    }

    /**
     * Delay until next cleanup session.
     */
    private suspend fun delayUntilNextCleanup() {
        val intervalMillis = TimeUnit.HOURS.toMillis(cleanupIntervalHours)
        delay(intervalMillis)
    }
}
