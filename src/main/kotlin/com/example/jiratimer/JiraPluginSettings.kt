package com.example.jiratimer

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import javax.swing.*

class JiraPluginSettings : Configurable {
    private var settingsPanel: JPanel? = null
    private val emailTextField = JTextField(20)
    private val tokenTextField = JTextField(20)
    private val baseUrlTextField = JTextField(20)

    /**
     * The settings panel
     */
    override fun createComponent(): JComponent? {
        settingsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Jira Email:"))
            add(emailTextField)
            add(Box.createVerticalStrut(5))
            add(JLabel("Jira API Token:"))
            add(tokenTextField)
            add(Box.createVerticalStrut(5))
            add(JLabel("Jira Base URL:"))
            add(baseUrlTextField)
        }
        return settingsPanel
    }

    /**
     * If the settings have been modified.
     */
    override fun isModified(): Boolean {
        val settings = PropertiesComponent.getInstance()
        return emailTextField.text != settings.getValue("JIRA_EMAIL") ||
                tokenTextField.text != settings.getValue("JIRA_API_TOKEN") ||
                baseUrlTextField.text != settings.getValue("JIRA_BASE_URL")
    }

    /**
     * Apply the changes.
     */
    override fun apply() {
        val settings = PropertiesComponent.getInstance()
        settings.setValue("JIRA_EMAIL", emailTextField.text)
        settings.setValue("JIRA_API_TOKEN", tokenTextField.text)
        settings.setValue("JIRA_BASE_URL", baseUrlTextField.text)
    }

    /**
     * Reset the settings.
     */
    override fun reset() {
        val settings = PropertiesComponent.getInstance()
        emailTextField.text = settings.getValue("JIRA_EMAIL", "")
        tokenTextField.text = settings.getValue("JIRA_API_TOKEN", "")
        baseUrlTextField.text = settings.getValue("JIRA_BASE_URL", "")
    }

    /**
     * Displau name of the tab in the settings.
     */
    override fun getDisplayName() = "Jira Settings"
}
