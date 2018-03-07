package com.github.grishberg;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.InstrumentationArgsProvider;
import com.github.grishberg.tests.commands.ClearCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.parser.TestPlan;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides test commands for executing on target device.
 */
public class CommandsForDeviceProvider implements DeviceRunnerCommandProvider {
    private static final String INSTRUMENTAL_ANNOTATION = "com.github.grishberg.instrumentaltestwithtestgroupsordering.InstrumentalTest";
    private static final String ESPRESSO_ANNOTATION = "com.github.grishberg.instrumentaltestwithtestgroupsordering.EspressoTest";
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;
    private List<DeviceRunnerCommand> prepareCommands;
    private final Logger logger;

    public CommandsForDeviceProvider(Project project,
                                     InstrumentalPluginExtension instrumentationInfo,
                                     InstrumentationArgsProvider argsProvider,
                                     List<DeviceRunnerCommand> prepareCommands) {
        this.project = project;
        logger = project.getLogger();
        this.instrumentationInfo = instrumentationInfo;
        this.argsProvider = argsProvider;
        this.prepareCommands = prepareCommands;
    }

    @Override
    public DeviceRunnerCommand[] provideCommandsForDevice(ConnectedDeviceWrapper deviceWrapper,
                                                          InstrumentalTestPlanProvider testPlanProvider,
                                                          Environment directoriesProvider) {
        ArrayList<DeviceRunnerCommand> commands = new ArrayList<>();
        ArrayList<TestPlan> espressoPlan = new ArrayList<>();
        ArrayList<TestPlan> instrumentalPlan = new ArrayList<>();
        ArrayList<TestPlan> simplePlan = new ArrayList<>();

        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(deviceWrapper);
        logger.info("[CFDP] device={}, args={}", deviceWrapper, instrumentalArgs);
        List<TestPlan> planSet = testPlanProvider.provideTestPlan(deviceWrapper, instrumentalArgs);
        logger.info("planSet.size = {}", planSet.size());
        for (TestPlan currentPlan : planSet) {
            logger.info("   provideDeviceCommands: current plan: {}", currentPlan.toString());
            logger.info("       Feature: {}", currentPlan.getFeatureParameter());
            for (String currentAnnotation : currentPlan.getAnnotations()) {
                logger.info("       Annotation: {}", currentAnnotation);

                if (INSTRUMENTAL_ANNOTATION.equals(currentAnnotation)) {
                    espressoPlan.add(currentPlan);
                    break;
                }
                if (ESPRESSO_ANNOTATION.equals(currentAnnotation)) {
                    instrumentalPlan.add(currentPlan);
                    break;
                }
                simplePlan.add(currentPlan);
            }
        }
        commands.add(new SingleInstrumentalTestCommand(project,
                "espresso_tests",
                instrumentationInfo,
                instrumentalArgs,
                espressoPlan,
                directoriesProvider.getCoverageDir(),
                directoriesProvider.getResultsDir()));
        commands.add(new ClearCommand(logger, instrumentationInfo));
        commands.add(new SingleInstrumentalTestCommand(project,
                "instrumental_tests",
                instrumentationInfo,
                instrumentalArgs,
                instrumentalPlan,
                directoriesProvider.getCoverageDir(),
                directoriesProvider.getResultsDir()));
        commands.add(new ClearCommand(logger, instrumentationInfo));
        commands.add(new SingleInstrumentalTestCommand(project,
                "simple_tests",
                instrumentationInfo,
                instrumentalArgs,
                simplePlan,
                directoriesProvider.getCoverageDir(),
                directoriesProvider.getResultsDir()));
        logger.info("---------------------- espressoCommands size = {}-------------", commands.size());
        logger.info("---------------------- espressoCommands = {}-------------", commands);
        return commands.toArray(new DeviceRunnerCommand[commands.size()]);
    }
}
