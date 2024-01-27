package com.example.jiratimer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component

class TimerStatusBarWidget(project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var projectTimer: ProjectTimer = project.getService(ProjectTimer::class.java)
    private var statusBar: StatusBar? = null

    private fun Int.formatTime() = this.toString().padStart(2, '0')

    /**
     * Sets up the widget with an initial project, its branch, and timer.
     */
    fun setup(project: Project, initialBranch: String, externalProjectTimer: ProjectTimer? = null) {
        projectTimer = externalProjectTimer ?: ProjectTimer(project)
        projectTimer.onTimeElapsed = {
            ApplicationManager.getApplication().invokeLater {
                statusBar?.updateWidget(ID())
            }
        }
        projectTimer.setProject(project)
        projectTimer.switchBranch(initialBranch)
        statusBar?.updateWidget(ID())
    }

    override fun ID() = "com.example.jiratimer.TimerStatusBarWidget"

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        projectTimer.stopTimer()
    }

    override fun getTooltipText() = "Time Elapsed"

    override fun getText(): String {
        val timeElapsed = projectTimer.getTimeElapsedForBranch(projectTimer.currentBranch)
        val minutes = timeElapsed / 60
        val seconds = timeElapsed % 60
        return "Timer: ${minutes.formatTime()}:${seconds.formatTime()}"
    }


    override fun getAlignment() = Component.CENTER_ALIGNMENT

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }
}
