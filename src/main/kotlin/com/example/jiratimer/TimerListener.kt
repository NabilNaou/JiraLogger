package com.example.jiratimer

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.ProjectActivity
import kotlinx.coroutines.*

class TimerListener : ProjectActivity {
    private var job: Job? = null

    override suspend fun execute(project: Project) {
        println("Project opened: ${project.name}")
        startTimer(project)
        project.messageBus.connect().subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
            override fun projectClosed(project: Project) {
                stopTimer()
            }
        })
    }

    private fun startTimer(project: Project) {
        job = CoroutineScope(Dispatchers.Main).launch {
            var timeElapsed = 0
            while (isActive) {
                println("Time elapsed: ${timeElapsed++} seconds in project: ${project.name}")
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        job?.cancel()
    }
}
