package com.example.jiratimer

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

/**
 * The management and creation of a timerWidget.
 */
class TimerWidgetFactory : StatusBarWidgetFactory {
    // Map of ProjectTimers.
    private val projectTimers = mutableMapOf<Project, ProjectTimer>()

    /**
     * Identifier that we need for the widget.
     */
    override fun getId() = "com.example.jiratimer.TimerStatusBarWidget"

    /**
     * Display name for the Widget.
     */
    override fun getDisplayName() = "Timer Widget"

    /**
     * Is the timer widget available for the project; needed implemented method.
     */
    override fun isAvailable(project: Project) = true

    /**
     * Make a new status bar widget for the project.
     */
    override fun createWidget(project: Project): StatusBarWidget {
        val widget = TimerStatusBarWidget(project)
        val projectTimer = projectTimers.getOrPut(project) { ProjectTimer(project) }

        BranchSwitchDetector(project, projectTimer, widget)

        return widget
    }

    /**
     * Dispose of our widget.
     */
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
}