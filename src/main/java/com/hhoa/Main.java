package com.hhoa;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
    name = "md2mermaid",
    mixinStandardHelpOptions = true,
    version = "md2mermaid 0.1.0",
    description = "Converts a Markdown list file to Mermaid syntax."
)
public class Main implements Callable<Integer> {

    @Option(
        names = {"-i", "--input"},
        description = "Path to the input Markdown file.",
        required = true,
        paramLabel = "<inputFile>"
    )
    private Path inputFile;

    @Option(
        names = {"-o", "--output"},
        description = "Path to write the generated Mermaid file.",
        required = true,
        paramLabel = "<outputFile>"
    )
    private Path outputFile;

    @Option(
        names = {"--headers"},
        description = "Add headers to the generated Mermaid file.",
        defaultValue = "false"
    )
    private boolean addHeaders;

    @Override
    public Integer call() {
        try {
            var markdown = Files.readString(inputFile);
            var mermaid  = new MermaidConverter(addHeaders, markdown).convert();
            Files.writeString(outputFile, mermaid);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
