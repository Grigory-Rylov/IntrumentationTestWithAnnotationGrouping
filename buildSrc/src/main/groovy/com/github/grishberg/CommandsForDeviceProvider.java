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
import com.github.grishberg.tests.common.RunnerLogger;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides test commands for executing on target device.
 */
public class CommandsForDeviceProvider implements DeviceRunnerCommandProvider {
    private static final String TAG = "CFDP";
    private static final String INSTRUMENTAL_ANNOTATION = "com.github.grishberg.instrumentaltestwithtestgroupsordering.InstrumentalTest";
    private static final String ESPRESSO_ANNOTATION = "com.github.grishberg.instrumentaltestwithtestgroupsordering.EspressoTest";
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;
    private List<DeviceRunnerCommand> prepareCommands;
    private final RunnerLogger logger;

    public CommandsForDeviceProvider(Project project,
                                     InstrumentalPluginExtension instrumentationInfo,
                                     InstrumentationArgsProvider argsProvider,
                                     List<DeviceRunnerCommand> prepareCommands,
                                     RunnerLogger logger) {
        this.project = project;
        this.instrumentationInfo = instrumentationInfo;
        this.argsProvider = argsProvider;
        this.prepareCommands = prepareCommands;
        this.logger = logger;
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
        logger.i(TAG, "device = %s, args = %s", deviceWrapper, instrumentalArgs);
        List<TestPlan> planSet = testPlanProvider.provideTestPlan(deviceWrapper, instrumentalArgs);
        logger.i(TAG, "planSet.size = %s", planSet.size());
        for (TestPlan currentPlan : planSet) {
            logger.i(TAG, "   provideDeviceCommands: current plan: %s", currentPlan.toString());
            logger.i(TAG, "       Feature: %s", currentPlan.getFeatureParameter());
            for (String currentAnnotation : currentPlan.getAnnotations()) {
                logger.i(TAG, "       Annotation: %s", currentAnnotation);

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
                directoriesProvider.getResultsDir(),
                logger));
        commands.add(new ClearCommand(project.getLogger(), instrumentationInfo));
        commands.add(new SingleInstrumentalTestCommand(project,
                "instrumental_tests",
                instrumentationInfo,
                instrumentalArgs,
                instrumentalPlan,
                directoriesProvider.getCoverageDir(),
                directoriesProvider.getResultsDir(),
                logger));
        commands.add(new SingleInstrumentalTestCommand(project,
                "simple_tests",
                instrumentationInfo,
                instrumentalArgs,
                simplePlan,
                directoriesProvider.getCoverageDir(),
                directoriesProvider.getResultsDir(),
                logger));
        logger.i(TAG, "---------------------- espressoCommands size = %s-------------", commands.size());
        logger.i(TAG, "---------------------- espressoCommands = %s -------------", commands);
        return commands.toArray(new DeviceRunnerCommand[commands.size()]);
    }
}
