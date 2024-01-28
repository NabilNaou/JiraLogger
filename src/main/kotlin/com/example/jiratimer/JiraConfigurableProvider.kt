package com.example.jiratimer

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsConfigurableProvider

class JiraVcsConfigurableProvider : VcsConfigurableProvider {
    /**
     * Need this to make a configurable component.
     */
    override fun getConfigurable(project: Project): Configurable {
        return JiraPluginSettings()
    }
}