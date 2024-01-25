package com.example.jiratimer

import net.minidev.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

class JiraApiClient(private val apiToken: String, private val jiraBaseUrl: String) {

    private val httpClient = HttpClient.newHttpClient()
    fun logTime(issueId: String, timeSpentSeconds: Int) {
        val uri = URI("$jiraBaseUrl/rest/api/3/issue/$issueId/worklog")
        val authHeader = "Basic ${Base64.getEncoder().encodeToString("$apiToken:".toByteArray())}"
        val payload = JSONObject().apply {
            put("timeSpentSeconds", timeSpentSeconds)
        }.toString()

        val request = HttpRequest.newBuilder(uri)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }
}
