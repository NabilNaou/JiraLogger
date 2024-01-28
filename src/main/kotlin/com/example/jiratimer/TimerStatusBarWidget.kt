package com.example.jiratimer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component

class TimerStatusBarWidget(project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var projectTimer: ProjectTimer = project.getService(ProjectTimer::class.java)
    private var statusBar: StatusBar? = null

    /**
     * We make sure the widget always displays in format: 00:00 (4 numbers).
     */
    private fun Int.formatTime() = this.toString().padStart(2, '0')

    /**
     * Sets up the widget with an initial project, and its branch.
     */
    fun setup(project: Project, initialBranch: String) {
        projectTimer = project.getService(ProjectTimer::class.java)

        projectTimer.onTimeElapsed = {
            ApplicationManager.getApplication().invokeLater {
                statusBar?.updateWidget(ID())
            }
        }
        projectTimer.setProject(project)
        projectTimer.switchBranch(initialBranch)
        statusBar?.updateWidget(ID())
    }

    /**
     * Unique ID of the widget.
     */
    override fun ID() = "com.example.jiratimer.TimerStatusBarWidget"

    /**
     * Set statusbar so we can later update/interact.
     */
    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    /**
     * When our projectTimer gets disposed, stop.
     */
    override fun dispose() {
        projectTimer.stopTimer()
    }

    override fun getTooltipText() = "Time Elapsed"

    /**
     * The text that is displayed by the widget.
     */
    override fun getText(): String {
        val timeElapsed = projectTimer.getTimeElapsedForBranch(projectTimer.currentBranch)
        val minutes = timeElapsed / 60
        val seconds = timeElapsed % 60
        return "Timer: ${minutes.formatTime()}:${seconds.formatTime()}"
    }


    /**
     * The alignment of our widget.
     */
    override fun getAlignment() = Component.CENTER_ALIGNMENT

    /**
     * Obtains presentation object which has the info to disp]ay and manage the widget.
     */
    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }
}
