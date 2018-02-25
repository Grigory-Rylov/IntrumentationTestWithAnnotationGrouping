package com.github.grishberg;

import com.github.grishberg.tests.DeviceWrapper;
import com.github.grishberg.tests.DirectoriesProvider;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.InstrumentationArgsProvider;
import com.github.grishberg.tests.commands.ClearCommand;
import com.github.grishberg.tests.commands.DeviceCommand;
import com.github.grishberg.tests.commands.DeviceCommandProvider;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.parser.TestPlan;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides test commands for executing on target device.
 */
public class CommandsForDeviceProvider implements DeviceCommandProvider {
    private static final String INSTRUMENTAL_ANNOTATION = "com.github.grishberg.instrumentaltestwithtestgroupsordering.InstrumentalTest";
    private static final String ESPRESSO_ANNOTAION = "com.github.grishberg.instrumentaltestwithtestgroupsordering.EspressoTest";
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;
    private List<DeviceCommand> prepareCommands;
    private final Logger logger;

    public CommandsForDeviceProvider(Project project,
                                     InstrumentalPluginExtension instrumentationInfo,
                                     InstrumentationArgsProvider argsProvider,
                                     List<DeviceCommand> prepareCommands) {
        this.project = project;
        logger = project.getLogger();
        this.instrumentationInfo = instrumentationInfo;
        this.argsProvider = argsProvider;
        this.prepareCommands = prepareCommands;
    }

    @Override
    public DeviceCommand[] provideDeviceCommands(DeviceWrapper deviceWrapper,
                                                 InstrumentalTestPlanProvider testPlanProvider,
                                                 DirectoriesProvider directoriesProvider) {
        ArrayList<DeviceCommand> espressoCommands = new ArrayList<>();
        ArrayList<DeviceCommand> instrumentalCommands = new ArrayList<>();

        espressoCommands.addAll(prepareCommands);

        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(deviceWrapper);
        logger.info("[CFDP] device={}, args={}",
                deviceWrapper.toString(), instrumentalArgs);
        Set<TestPlan> planSet = testPlanProvider.provideTestPlan(deviceWrapper, instrumentalArgs);
        logger.info("planSet.size = {}", planSet.size());
        for (TestPlan currentPlan : planSet) {
            logger.info("   provideDeviceCommands: current plan: {}", currentPlan.getName());
            logger.info("       Feature: {}", currentPlan.getFeatureParameter());
            for (String currentAnnotation : currentPlan.getAnnotations()) {
                logger.info("       Annotation: {}", currentAnnotation);

                if (INSTRUMENTAL_ANNOTATION.equals(currentAnnotation)) {
                    instrumentalCommands.add(new SingleInstrumentalTestCommand(project,
                            instrumentationInfo,
                            instrumentalArgs,
                            currentPlan));
                    break;
                }
                if (ESPRESSO_ANNOTAION.equals(currentAnnotation)) {
                    espressoCommands.add(new SingleInstrumentalTestCommand(project,
                            instrumentationInfo,
                            instrumentalArgs,
                            currentPlan));
                    break;
                }
            }
        }

        espressoCommands.add(new ClearCommand(logger, instrumentationInfo));
        espressoCommands.addAll(instrumentalCommands);
        logger.info("---------------------- espressoCommands size = {}-------------", espressoCommands.size());
        logger.info("---------------------- espressoCommands = {}-------------", espressoCommands);
        return espressoCommands.toArray(new DeviceCommand[espressoCommands.size()]);
    }
}
