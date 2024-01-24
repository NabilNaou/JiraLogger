package com.example.jiratimer

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class TimerWidgetFactory : StatusBarWidgetFactory {
    private val projectTimers = mutableMapOf<Project, ProjectTimer>()

    override fun getId() = "com.example.jiratimer.TimerStatusBarWidget"
    override fun getDisplayName() = "Timer Widget"

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project): StatusBarWidget {
        val widget = TimerStatusBarWidget()
        val projectTimer = projectTimers.getOrPut(project) { ProjectTimer() }

        BranchSwitchDetector(project, projectTimer, widget)

        return widget
    }

    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
}
