package com.example.jiratimer

import git4idea.repo.GitRepositoryChangeListener
import git4idea.repo.GitRepositoryManager
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository

class BranchSwitchDetector(private val project: Project, private val projectTimer: ProjectTimer, private val widget: TimerStatusBarWidget) {
    private var isInitialSetupDone = false

    init {
        val messageBus = project.messageBus
        messageBus.connect().subscribe(GitRepository.GIT_REPO_CHANGE, GitRepositoryChangeListener {
            onRepositoryChange()
        })
    }

    private fun onRepositoryChange() {
        val manager = GitRepositoryManager.getInstance(project)
        val repositories = manager.repositories
        for (repo in repositories) {
            val currentBranch = repo.currentBranch?.name ?: "No Branch"

            if (!isInitialSetupDone) {
                widget.setup(project, currentBranch, projectTimer)
                isInitialSetupDone = true
            }

            widget.onBranchChange(currentBranch)
        }
    }
}
