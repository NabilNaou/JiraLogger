package com.example.jiratimer

import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

class GitPushDetectorFactory : CheckinHandlerFactory() {
    /**
     * Makes checkinHandler instances (Factory). This is a custom checkin handler for our
     * time logging. Panel is the checkin panel, and commit context is some info about the commit.
     */
    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return GitPushDetector(panel)
    }
}
