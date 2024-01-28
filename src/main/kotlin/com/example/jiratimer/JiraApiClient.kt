package com.example.jiratimer

import net.minidev.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import java.util.concurrent.CompletableFuture

class JiraApiClient(private val apiToken: String, private val jiraBaseUrl: String) {

    private val httpClient = HttpClient.newHttpClient()

    /**
     * Log our time to Jira. Return a status code of success, if succesful.
     */
    fun logTime(issueId: String, timeSpentSeconds: Int): CompletableFuture<Boolean> {
        val uri = URI("$jiraBaseUrl/rest/api/3/issue/$issueId/worklog")

        val authHeader = "Basic ${Base64.getEncoder().encodeToString("kallie12345@gmail.com:$apiToken".toByteArray())}"
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
