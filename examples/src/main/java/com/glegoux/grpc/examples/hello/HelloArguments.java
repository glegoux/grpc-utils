package com.glegoux.grpc.examples.hello;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class HelloArguments {

  private static final short DEFAULT_PORT = 8000;
  private static final short DEFAULT_MONITORING_PORT = 8001;

  private short port = DEFAULT_PORT;
  private short monitoringPort = DEFAULT_MONITORING_PORT;

  HelloArguments(String[] args) {
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
    } catch (ParseException exception) {
      System.err.println(exception.getMessage());
      System.exit(1);
    }
  }

  short getPort() {
    return port;
  }

  short getMonitoringPort() {
    return monitoringPort;
  }

  private static void printUsage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    String programName = "hello";
    String description = String.format("Run a gRPC server%n%n");
    formatter.printHelp(programName, description, options, null, true);
  }

  private static Options buildOptions() {
    Options options = new Options();
    Option help = new Option("h", "help", false, "print this message");
    Option port = Option.builder("p").longOpt("port")
        .hasArg()
        .required(false)
        .type(Short.class)
        .argName("value")
        .desc(String.format("gRPC server port (default: %d)", DEFAULT_PORT))
        .build();
    Option monitoringPort = Option.builder().longOpt("monitoring-port")
        .hasArg()
        .required(false)
        .type(Short.class)
        .argName("value")
        .desc(
            String.format(
                "monitoring server port for prometheus exporter of gRPC metrics (default: %d)",
                DEFAULT_MONITORING_PORT
            )
        )
        .build();
    options.addOption(help);
    options.addOption(port);
    options.addOption(monitoringPort);
    return options;
  }

  private static short checkAndGetPort(CommandLine cmd, String longOpt) throws ParseException {
    Object port = cmd.getParsedOptionValue(longOpt);
    if (port == null || !(1024 <= (Short) port && (Short) port <= 65535)) {
      throw new ParseException(
          String.format(
              "Failed to get the value `%s` of the option --%s must be a integer between 1024 and 65535",
              cmd.getOptionValue(longOpt),
              longOpt
          )
      );
    }
    return (Short) port;
  }

}
