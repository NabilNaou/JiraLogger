package com.example.jiratimer

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import git4idea.repo.GitRepositoryManager

class TimerWidgetFactory : StatusBarWidgetFactory {
    private val projectTimers = mutableMapOf<Project, ProjectTimer>()

    override fun getId() = "com.example.jiratimer.TimerStatusBarWidget"
    override fun getDisplayName() = "Timer Widget"

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project): StatusBarWidget {
        val widget = TimerStatusBarWidget()
        val projectTimer = projectTimers.getOrPut(project) { ProjectTimer() }

        val branchSwitchDetector = BranchSwitchDetector(project, projectTimer) // Create the detector first

        val gitRepositoryManager = GitRepositoryManager.getInstance(project)
        val currentBranch = gitRepositoryManager.repositories
                .firstOrNull()
                ?.currentBranch?.name ?: ""

        println("Creating TimerStatusBarWidget with currentBranch: $currentBranch")

        // Setup the widget with the current branch and the shared projectTimer
        widget.setup(project, currentBranch, projectTimer)

        return widget
    }
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
}
