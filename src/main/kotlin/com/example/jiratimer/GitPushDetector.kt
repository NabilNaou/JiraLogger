package com.example.jiratimer

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.CheckinProjectPanel

class GitPushDetector(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val projectTimer: ProjectTimer = panel.project.getService(ProjectTimer::class.java)

    /**
     * Get the current branch from ProjectTimer and proceed with the check-in process.
     */
    override fun checkinSuccessful() {
        val currentBranch = projectTimer.currentBranch
        val jiraIssueId = extractJiraIssueId(currentBranch)

        if (jiraIssueId != null && currentBranch.isNotEmpty()) {
            val timeSpentInSeconds = projectTimer.getTimeElapsedForBranch(currentBranch)
            val formattedTime = formatTime(timeSpentInSeconds)
            val editedTimeString = Messages.showInputDialog(
                    panel.component,
                    "Is this time spent correct? (in minutes)",
                    "Confirm Time Spent",
                    Messages.getQuestionIcon(),
                    formattedTime,
                    null
            )
            val editedTimeInt = editedTimeString?.toIntOrNull() ?: (timeSpentInSeconds / 60)

            projectTimer.pushTimeToJira(jiraIssueId, editedTimeInt).thenAccept { success ->
                if (success) {
                    projectTimer.resetTimeForBranch(currentBranch)
                }
            }
        }
    }

    private fun formatTime(timeInSeconds: Int): String {
        val minutes = timeInSeconds / 60
        return minutes.toString()
    }

    /**
     * Extract the ID from our branch name. Example pattern is SCRUM-1: or _ Test. Will extract SCRUM-1.
     */
    private fun extractJiraIssueId(branchName: String?): String? {
        if (branchName != null) {
            val pattern = """([A-Z]+-\d+)""".toRegex()
            val matchResult = pattern.find(branchName)
            return matchResult?.value
        }
        return null
    }
}
