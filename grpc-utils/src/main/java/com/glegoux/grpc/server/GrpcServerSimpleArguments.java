package com.glegoux.grpc.server;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.logging.Logger;

public class GrpcServerSimpleArguments {
    private static final Logger LOGGER = Logger.getLogger(GrpcServerSimple.class.getName());
    static final int DEFAULT_PORT = 8000;
    static final int DEFAULT_MONITORING_PORT = 8001;
    static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();


    private final String programName;
    private int port = DEFAULT_PORT;
    private int monitoringPort = DEFAULT_MONITORING_PORT;
    private int numberOfThreads = DEFAULT_NUMBER_OF_THREADS;

    public GrpcServerSimpleArguments(String programName, String[] args) {
        this.programName = programName;
        CommandLineParser parser = new DefaultParser();
        Options options = buildOptions();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                printUsage(options);
                System.exit(0);
            }
            if (cmd.hasOption("port")) {
                this.port = checkAndGetPort(cmd, "port");
            }
            if (cmd.hasOption("monitoring-port")) {
                this.monitoringPort = checkAndGetPort(cmd, "monitoring-port");
            }
            if (cmd.hasOption("number-threads")) {
                this.numberOfThreads = ((Long) cmd.getParsedOptionValue("number-threads")).intValue();
            }
        } catch (ParseException exception) {
            LOGGER.warning(String.format("Incorrect server arguments %s", exception));
            System.exit(1);
        }
    }

    public String getProgramName() {
        return programName;
    }

    public int getPort() {
        return port;
    }

    public int getMonitoringPort() {
        return monitoringPort;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    private void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String description = String.format("Run a gRPC server%n%n");
        formatter.printHelp(getProgramName(), description, options, null, true);
    }

    private Options buildOptions() {
        Options options = new Options();
        Option help = new Option("h", "help", false, "print this message");
        Option port = buildOptionWithNumberValue("port", String.format("gRPC server port (default: %d)", DEFAULT_PORT));
        Option monitoringPort = buildOptionWithNumberValue("monitoring-port", String.format("monitoring server port for prometheus exporter of gRPC metrics (default: %d)", DEFAULT_MONITORING_PORT));
        Option numberOfThread = buildOptionWithNumberValue("number-threads", String.format("number of threads to process gRPC incoming requests (default: %d)", DEFAULT_NUMBER_OF_THREADS));
        options.addOption(help);
        options.addOption(port);
        options.addOption(monitoringPort);
        options.addOption(numberOfThread);
        return options;
    }

    private Option buildOptionWithNumberValue(String name, String desc) {
        return Option.builder().longOpt(name)
            .hasArg()
            .required(false)
            .type(Number.class)
            .argName("value")
            .desc(desc)
            .build();
    }

    private int checkAndGetPort(CommandLine cmd, String longOpt) throws ParseException {
        Long port;
        try {
            port = (Long) cmd.getParsedOptionValue(longOpt);
            if (port == null || !(1024 <= port && port <= 65535)) {
                throw new ParseException(getExceptionMsg(cmd, longOpt));
            }
        } catch (ClassCastException e) {
            throw new ParseException(getExceptionMsg(cmd, longOpt));
        }
        return port.intValue();
    }

    private String getExceptionMsg(CommandLine cmd, String longOpt) {
        return String.format(
            "Failed to get the value `%s` of the option --%s must be an integer between 1024 and 65535",
            cmd.getOptionValue(longOpt), longOpt);
    }
}
