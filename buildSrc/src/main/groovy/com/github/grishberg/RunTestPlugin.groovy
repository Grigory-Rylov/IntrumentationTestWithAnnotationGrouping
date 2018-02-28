package com.github.grishberg

import com.github.grishberg.tests.DefaultInstrumentationArgsProvider
import com.github.grishberg.tests.InstrumentalPluginExtension
import com.github.grishberg.tests.InstrumentalTestTask
import com.github.grishberg.tests.InstallApkCommandsProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by grishberg on 12.12.17.
 */
class RunTestPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        InstallApkCommandsProvider installApkCommandsProvider = new InstallApkCommandsProvider(project)
        InstrumentalTestTask instrumentalTestTask = project.tasks.findByName(InstrumentalTestTask.NAME)
        InstrumentalPluginExtension extension = project
                .getExtensions()
                .findByType(InstrumentalPluginExtension.class)

        /**
         * Setup custom instrumentation test runner.
         */
        def runTestTask = project.tasks.create("runTestTask") {
            dependsOn('assembleDebug', 'assembleDebugAndroidTest')
            finalizedBy instrumentalTestTask
            group 'android'
            doLast {
                File resultsDir = new File(project.getBuildDir(), "results")
                File coverageDir = new File(project.getBuildDir(), "coverage")
                File reportsDir = new File(project.getBuildDir(), "reports")
                instrumentalTestTask.setResultsDir(resultsDir)
                instrumentalTestTask.setCoverageDir(coverageDir)
                instrumentalTestTask.setReportsDir(reportsDir)
                instrumentalTestTask.commandProvider =
                        new CommandsForDeviceProvider(project, extension,
                                new DefaultInstrumentationArgsProvider(),
                                installApkCommandsProvider.provideInstallApkCommands("debug"))
            }
        }
    }
}