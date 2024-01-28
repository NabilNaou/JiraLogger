package com.example.jiratimer

import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.CheckinProjectPanel
import git4idea.repo.GitRepositoryManager

class GitPushDetector(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val projectTimer: ProjectTimer = panel.project.getService(ProjectTimer::class.java)

    /**
     * Get the current repo, and current branch. Extract the ID from it and pass it all to pushTimeToJira
     * for logging. After success, reset.
     */
    override fun checkinSuccessful() {
        val gitRepository = GitRepositoryManager.getInstance(panel.project).repositories.firstOrNull()
        val currentBranch = gitRepository?.currentBranch?.name
        val jiraIssueId = extractJiraIssueId(currentBranch)

        if (jiraIssueId != null && currentBranch != null) {
            projectTimer.pushTimeToJira(jiraIssueId, currentBranch).thenAccept { success ->
                if (success) {
                    projectTimer.resetTimeForBranch(currentBranch)
                }
            }
        }
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
