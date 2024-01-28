package com.example.jiratimer

import com.intellij.ide.util.PropertiesComponent
import net.minidev.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import java.util.concurrent.CompletableFuture

class JiraApiClient() {
    private val settings = PropertiesComponent.getInstance()

    private val email: String
        get() = settings.getValue("JIRA_EMAIL", "")

    private val token: String
        get() = settings.getValue("JIRA_API_TOKEN", "")

    private val jiraBaseUrl: String
        get() = settings.getValue("JIRA_BASE_URL", "")

    private val httpClient = HttpClient.newHttpClient()

    /**
     * Log our time to Jira. Return a status code of success, if succesful.
     */
    fun logTime(issueId: String, timeSpentSeconds: Int): CompletableFuture<Boolean> {
        val uri = URI("$jiraBaseUrl/rest/api/3/issue/$issueId/worklog")
        val authHeader = "Basic ${Base64.getEncoder().encodeToString("$email:$token".toByteArray())}"
        val payload = JSONObject().apply {
            put("timeSpentSeconds", timeSpentSeconds)
        }.toString()

        val request = HttpRequest.newBuilder(uri)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build()

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply { response ->
                    response.statusCode() == 201
                }
    }
}
