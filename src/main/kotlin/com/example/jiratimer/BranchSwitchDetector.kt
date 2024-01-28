package com.example.jiratimer

import com.intellij.openapi.project.Project
import java.util.Timer
import java.util.TimerTask
import java.io.BufferedReader
import java.io.InputStreamReader

class BranchSwitchDetector(private val project: Project, private val projectTimer: ProjectTimer, private val widget: TimerStatusBarWidget) {
    private var currentBranchName: String? = null

    /**
     * Checks every 3 seconds; task; for changes.
     */
    init {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                checkForBranchChange()
            }
        }, 0, 3000)
    }

    /**
     * Check for changes in the Git branch and notify branch changes. This is done through git commands
     * as git4idea is incompatible.
     */
    private fun checkForBranchChange() {
        val processBuilder = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
        processBuilder.directory(project.basePath?.let { java.io.File(it) })
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val newBranchName = reader.readLine()
        reader.close()

        if (newBranchName != null && currentBranchName != newBranchName) {
            currentBranchName = newBranchName
            projectTimer.switchBranch(newBranchName)
            widget.setup(project, newBranchName)
        }
    }
}
