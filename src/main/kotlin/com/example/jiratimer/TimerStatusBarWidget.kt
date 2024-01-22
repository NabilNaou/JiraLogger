package com.example.jiratimer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component

class TimerStatusBarWidget : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var timeElapsed = 0
    private var projectTimer: ProjectTimer? = null
    private var statusBar: StatusBar? = null

    fun setup(project: Project, initialBranch: String, externalProjectTimer: ProjectTimer? = null) {
        println("Widget setup called for branch: $initialBranch")
        projectTimer = externalProjectTimer ?: ProjectTimer().apply {
            onTimeElapsed = { time ->
                println("onTimeElapsed callback set to: $onTimeElapsed") // Add this log
                updateTime(time)
                ApplicationManager.getApplication().invokeLater {
                    statusBar?.updateWidget(ID())
                    println("Widget updated")
                }
                println("Time elapsed callback invoked: $time seconds")
            }
            setProject(project)
            switchBranch(initialBranch)
            println("Widget setup for branch: $initialBranch")
        }
    }

    override fun ID() = "com.example.jiratimer.TimerStatusBarWidget"

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        projectTimer?.stopTimer()
    }

    override fun getTooltipText() = "Time Elapsed"

    override fun getText(): String {
        val minutes = timeElapsed / 60
        val seconds = timeElapsed % 60
        println("Updating widget text display: ${minutes.formatTime()}:${seconds.formatTime()}")
        return "Timer: ${minutes.formatTime()}:${seconds.formatTime()}"
    }


    override fun getAlignment() = Component.CENTER_ALIGNMENT

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }

    private fun updateTime(elapsed: Int) {
        println("updateTime called with elapsed: $elapsed")
        timeElapsed = elapsed
    }

    private fun Int.formatTime() = this.toString().padStart(2, '0')
}
