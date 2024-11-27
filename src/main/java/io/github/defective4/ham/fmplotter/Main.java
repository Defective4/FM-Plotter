package io.github.defective4.ham.fmplotter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            String name = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getName();
            System.err.println("Usage: java -jar " + name + " <command> [args]");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "wavcalc" -> {
                if (args.length < 4) {
                    System.err.println("Usage: wavcalc <size in bytes> <samplerate> <bits per sample>");
                    return;
                }
                long size = Long.parseLong(args[1]);
                int sampleRate = Integer.parseInt(args[2]);
                int bits = Integer.parseInt(args[3]);

                long len = WavCalculator.calculateLength(size, sampleRate, bits);
                System.out.println("Wave file length " + new SimpleDateFormat("mm:ss").format(new Date(len)));
            }
            case "plot" -> {
                if (args.length < 4) {
                    System.err.println("Usage: plot <file> <min difference> <output>");
                    return;
                }

                int minDiff = Integer.parseInt(args[2]);

                BufferedReader input;
                OutputStream output;
                boolean stdin = false;
                boolean stdout = false;
                if ("-".equals(args[1])) {
                    input = new BufferedReader(new InputStreamReader(System.in));
                    stdin = true;
                } else {
                    File in = new File(args[1]);
                    if (!in.isFile()) {
                        System.err.println("File " + in + " does not exist!");
                        return;
                    }
                    input = new BufferedReader(new FileReader(in));
                }

                if ("-".equals(args[3])) {
                    output = System.out;
                } else {
                    output = Files.newOutputStream(Path.of(args[3]));
                }

                try {
                    Plotter.plot(input, output, minDiff);
                } finally {
                    if (!stdin) input.close();
                    if (!stdout) output.close();
                }
            }
            default -> { System.err.println("""
                                            Unknown command """ + args[0] + """

                                                                            Valid commands:
                                                                            - plot
                                                                            - wavcalc
                                                                            """); }
        }
    }
}
