package com.example.jiratimer

import git4idea.repo.GitRepositoryChangeListener
import git4idea.repo.GitRepositoryManager
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository

class BranchSwitchDetector(private val project: Project, private val projectTimer: ProjectTimer, private val widget: TimerStatusBarWidget) {
    private var isInitialSetupDone = false

    /**
     * Initialize the detector, and subscribe to git events.
     */
    init {
        val messageBus = project.messageBus
        messageBus.connect().subscribe(GitRepository.GIT_REPO_CHANGE, GitRepositoryChangeListener {
            onRepositoryChange()
        })
    }

    /**
     * Callback function that is called when the branch is changed.
     * Updates timer widget based on the current branch.
     */
    private fun onRepositoryChange() {
        val manager = GitRepositoryManager.getInstance(project)
        val repositories = manager.repositories
        for (repo in repositories) {
            val currentBranchName = repo.currentBranch?.name ?: "No Branch"
            projectTimer.switchBranch(currentBranchName)
            if (!isInitialSetupDone) {
                widget.setup(project, currentBranchName)
                isInitialSetupDone = true
            }
        }
    }
}
