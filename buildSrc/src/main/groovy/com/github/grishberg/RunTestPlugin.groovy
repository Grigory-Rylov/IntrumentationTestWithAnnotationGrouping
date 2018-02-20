package com.github.grishberg

import com.github.grishberg.tests.DefaultInstrumentationArgsProvider
import com.github.grishberg.tests.InstrumentalPluginExtension
import com.github.grishberg.tests.InstrumentalTestTask

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by grishberg on 12.12.17.
 */
class RunTestPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        InstrumentalTestTask instrumentalTestTask = project.tasks.findByName(InstrumentalTestTask.NAME)
        InstrumentalPluginExtension extension = project
                .getExtensions()
                .findByType(InstrumentalPluginExtension.class)

        /**
         * Setup custom instrumentation test runner.
         */

        def runTestTask = project.tasks.create("runTestTask") {
            dependsOn('installDebug', 'installDebugAndroidTest')
            finalizedBy instrumentalTestTask
            group 'android'
            doLast {
                println "-------------------- setup instrumentation tests ------------------"
                println("----------- ext " + extension)
                instrumentalTestTask.commandProvider =
                        new CommandsForDeviceProvider(project, extension,
                                new DefaultInstrumentationArgsProvider())
                println("-------------------- finish setup instrumentation tests -----------")
                println("-------------------- EXT: " + extension.applicationId)
            }
        }
    }
}