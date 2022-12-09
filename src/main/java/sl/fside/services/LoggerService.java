package sl.fside.services;

import com.google.inject.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.builder.api.*;

public class LoggerService {

    // TODO Determine if debug
    private final boolean isDebug = true;

    private final String loggerName = "MainLogger";

    @Inject
    LoggerService() {
        configureLoggers();
    }

    private void configureLoggers() {
        final var stdoutAppender = "stdout";
        final var fileAppender = "logFile";

        var configurationBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

        // Create appenders
        var console = configurationBuilder.newAppender(stdoutAppender, "Console");

        var file = configurationBuilder.newAppender(fileAppender, "File");
        file.addAttribute("fileName", "target/logs.log");

        // Create mainLayout
        var mainLayout = configurationBuilder.newLayout("PatternLayout");
        mainLayout.addAttribute("pattern", "%d %-5level: %msg%n%throwable");

        // Add layouts to appenders
        console.add(mainLayout);
        file.add(mainLayout);

        // Add appenders
        configurationBuilder.add(console);
        configurationBuilder.add(file);

        // Configure root logger
        var rootLoggerBuilder = configurationBuilder.newRootLogger();
        rootLoggerBuilder.add(configurationBuilder.newAppenderRef(stdoutAppender));
        rootLoggerBuilder.add(configurationBuilder.newAppenderRef(fileAppender));
        configurationBuilder.add(rootLoggerBuilder);

        // Configure logger
        var loggerBuilder = configurationBuilder.newLogger(loggerName, Level.ALL);
        loggerBuilder.addAttribute("additivity", true);
        configurationBuilder.add(loggerBuilder);

        // Configure policies
        var triggeringPolicies = configurationBuilder.newComponent("Policies")
                .addComponent(configurationBuilder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));
        file.addComponent(triggeringPolicies);

        Configurator.initialize(configurationBuilder.build());
    }

    public void logDebug(String message) {
        if (isDebug)
            getLogger().debug(message);
    }

    public void logInfo(String message) {
        getLogger().info(message);
    }

    public void logWarning(String message) {
        getLogger().warn(message);
    }

    public void logError(String message) {
        getLogger().error(message);
    }

    private Logger getLogger() {
        return LogManager.getLogger(loggerName);
    }
}
