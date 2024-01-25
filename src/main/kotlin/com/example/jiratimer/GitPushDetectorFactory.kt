package com.example.jiratimer

import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

class GitPushDetectorFactory : CheckinHandlerFactory() {
    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return GitPushDetector(panel)
    }
}
