package com.example.jiratimer

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.CheckinProjectPanel
import git4idea.repo.GitRepositoryManager

class GitPushDetector(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val projectTimer: ProjectTimer = panel.project.getService(ProjectTimer::class.java)


    /**
     * Get the current repo and branch. Get the time spent, and format it. We ask the user to verify and then
     * send in.
     */
    override fun checkinSuccessful() {
        val gitRepository = GitRepositoryManager.getInstance(panel.project).repositories.firstOrNull()
        val currentBranch = gitRepository?.currentBranch?.name
        val jiraIssueId = extractJiraIssueId(currentBranch)

        if (jiraIssueId != null && currentBranch != null) {
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
     * Extract the ID from our branchname. Example pattern is SCRUM-1: or _ Test. Will extract SCRUM-1.
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
