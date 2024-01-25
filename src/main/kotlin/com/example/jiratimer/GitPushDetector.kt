package com.example.jiratimer

import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.CheckinProjectPanel
import git4idea.repo.GitRepositoryManager

class GitPushDetector(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val projectTimer: ProjectTimer = panel.project.getService(ProjectTimer::class.java)

    override fun checkinSuccessful() {
        super.checkinSuccessful()

        val gitRepository = GitRepositoryManager.getInstance(panel.project).repositories.firstOrNull()
        val currentBranch = gitRepository?.currentBranch?.name
        val jiraIssueId = extractJiraIssueId(currentBranch)
        if (jiraIssueId != null) {
            projectTimer.pushTimeToJira(jiraIssueId)
        }
    }

    private fun extractJiraIssueId(branchName: String?): String? {
        if (branchName != null) {
            val pattern = """([A-Z]+-\d+)""".toRegex()
            val matchResult = pattern.find(branchName)
            return matchResult?.value
        }
        return null
    }
}
