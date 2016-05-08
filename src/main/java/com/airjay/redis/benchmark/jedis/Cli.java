package com.airjay.redis.benchmark.jedis;

import org.apache.commons.cli.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();
    public static String host;
    public static String port;
    public static int threadCount;
    public static int repeatCount;
    public static int bytes;
    public static int connCount;
    public static String operation;
    public static int opTimeout;
    public static boolean enableCluster;

    public Cli(String[] args) {

        this.args = args;

        options.addOption("help", "help", false, "show help.");
        options.addOption("h", "host", true,
                "Specifies a host to contact over the network.");
        options.addOption("p", "port", true,
                "Specifies a port number to contact.");
        options.addOption("t", "thread", true, "Specifies thread count to run.");
        options.addOption("r", "repeat", true,
                "Specifies repeat count per thread.");
        options.addOption("b", "byte", true,
                "Specifies bytes for value object.");
        options.addOption("c", "connection", true,
                "Specifies connection pool size.");
        options.addOption("op", "operation", true,
                "Specifies set or get operation.");
        options.addOption("opTimeout", "operation timeout", true,
                "Specifies set or get operation timeout.");
        options.addOption("enableCluster", "enable Cluster", true,
                "Specifies true or false.");
    }

    public void parse() {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("help"))
                help();

            if (cmd.hasOption("h")) {
                log.log(Level.INFO,
                        "Using cli argument -h=" + cmd.getOptionValue("h"));
                host = cmd.getOptionValue("h");
            } else {
                log.log(Level.SEVERE, "Missing h option");
                help();
            }

            if (cmd.hasOption("p")) {
                log.log(Level.INFO,
                        "Using cli argument -p=" + cmd.getOptionValue("p"));
                port = cmd.getOptionValue("p");
            } else {
                log.log(Level.SEVERE, "Missing p option");
                help();
            }

            if (cmd.hasOption("t")) {
                log.log(Level.INFO,
                        "Using cli argument -t=" + cmd.getOptionValue("t"));
                threadCount = Integer.valueOf(cmd.getOptionValue("t"));
            } else {
                log.log(Level.SEVERE, "Missing t option");
                help();
            }

            if (cmd.hasOption("r")) {
                log.log(Level.INFO,
                        "Using cli argument -r=" + cmd.getOptionValue("r"));
                repeatCount = Integer.valueOf(cmd.getOptionValue("r"));
            } else {
                log.log(Level.SEVERE, "Missing r option");
                help();
            }

            if (cmd.hasOption("b")) {
                log.log(Level.INFO,
                        "Using cli argument -b=" + cmd.getOptionValue("b"));
                bytes = Integer.valueOf(cmd.getOptionValue("b"));
            } else {
                log.log(Level.SEVERE, "Missing b option");
                help();
            }

            if (cmd.hasOption("c")) {
                log.log(Level.INFO,
                        "Using cli argument -c=" + cmd.getOptionValue("c"));
                connCount = Integer.valueOf(cmd.getOptionValue("c"));
            } else {
                log.log(Level.SEVERE, "Missing c option");
                help();
            }

            if (cmd.hasOption("op")) {
                log.log(Level.INFO,
                        "Using cli argument -op=" + cmd.getOptionValue("op"));
                operation = cmd.getOptionValue("op");
            } else {
                log.log(Level.SEVERE, "Missing op option");
                help();
            }

            if (cmd.hasOption("opTimeout")) {
                log.log(Level.INFO,
                        "Using cli argument -opTimeout="
                                + cmd.getOptionValue("opTimeout"));
                opTimeout = Integer.valueOf(cmd.getOptionValue("opTimeout"));
            } else {
                log.log(Level.SEVERE, "Missing opTimeout option");
                help();
            }

            if (cmd.hasOption("enableCluster")) {
                log.log(Level.INFO,
                        "Using cli argument -enableCluster="
                                + cmd.getOptionValue("enableCluster"));
                enableCluster = Boolean.valueOf(cmd.getOptionValue("enableCluster"));
            } else {
                log.log(Level.SEVERE, "Missing enableCluster option");
                help();
            }

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        }
    }

    private void help() {
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }
}