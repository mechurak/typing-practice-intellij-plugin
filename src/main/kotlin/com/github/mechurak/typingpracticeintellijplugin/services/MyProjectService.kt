package com.github.mechurak.typingpracticeintellijplugin.services

import com.intellij.openapi.project.Project
import com.github.mechurak.typingpracticeintellijplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
