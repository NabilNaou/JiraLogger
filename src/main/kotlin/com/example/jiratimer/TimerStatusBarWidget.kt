package com.example.jiratimer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component

class TimerStatusBarWidget : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var timeElapsed = 0
    private val projectTimer = ProjectTimer()
    private var statusBar: StatusBar? = null

    init {
        projectTimer.onTimeElapsed = { time ->
            updateTime(time)
            ApplicationManager.getApplication().invokeLater {
                statusBar?.updateWidget(ID())
            }
        }
    }

    fun setup(project: Project) {
        projectTimer.setProject(project)
        projectTimer.startTimer()
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
        val minutes = timeElapsed / 60
        val seconds = timeElapsed % 60
        return "Timer: ${minutes.formatTime()}:${seconds.formatTime()}"
    }

    override fun getAlignment() = Component.CENTER_ALIGNMENT

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }

    private fun updateTime(elapsed: Int) {
        timeElapsed = elapsed
    }

    private fun Int.formatTime() = this.toString().padStart(2, '0')
}
