package com.example.jiratimer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component

class TimerStatusBarWidget(project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var projectTimer: ProjectTimer = ProjectTimer(project)
    private var statusBar: StatusBar? = null
    private var currentBranch: String = ""

    private fun Int.formatTime() = this.toString().padStart(2, '0')

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
        val timeElapsed = projectTimer.getTimeElapsedForBranch(currentBranch)
        val minutes = timeElapsed / 60
        val seconds = timeElapsed % 60
        println("Updating widget text display: ${minutes.formatTime()}:${seconds.formatTime()}")
        return "Timer: ${minutes.formatTime()}:${seconds.formatTime()}"
    }


    override fun getAlignment() = Component.CENTER_ALIGNMENT

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }
    fun onBranchChange(newBranch: String) {
        currentBranch = newBranch
        projectTimer.switchBranch(newBranch)
    }
}
