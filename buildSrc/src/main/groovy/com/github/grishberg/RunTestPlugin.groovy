package com.github.grishberg

import com.github.grishberg.tests.DefaultInstrumentationArgsProvider
import com.github.grishberg.tests.InstrumentalPluginExtension
import com.github.grishberg.tests.InstrumentationTestTask
import com.github.grishberg.tests.InstallApkCommandsProvider
import com.github.grishberg.tests.common.FileLogger
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by grishberg on 12.12.17.
 */
class RunTestPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        InstallApkCommandsProvider installApkCommandsProvider = new InstallApkCommandsProvider(project)
        InstrumentationTestTask instrumentalTestTask = project.tasks.findByName(InstrumentationTestTask.NAME)
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
                FileLogger logger = new FileLogger(project, "instrumental_runner.log")
                File resultsDir = new File(project.getBuildDir(), "results")
                File coverageDir = new File(project.getBuildDir(), "coverage")
                File reportsDir = new File(project.getBuildDir(), "reports")
                instrumentalTestTask.setResultsDir(resultsDir)
                instrumentalTestTask.setCoverageDir(coverageDir)
                instrumentalTestTask.setReportsDir(reportsDir)
                instrumentalTestTask.setRunnerLogger(logger)
                instrumentalTestTask.commandProvider =
                        new CommandsForDeviceProvider(project, extension,
                                new DefaultInstrumentationArgsProvider(), [], logger)
            }
        }
    }
}