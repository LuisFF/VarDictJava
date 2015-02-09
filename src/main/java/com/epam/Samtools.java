package com.epam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Samtools implements AutoCloseable {

        private Process proc;
        private BufferedReader reader;
        private final List<String> list;

        public Samtools(String... args) throws IOException {
            list = new ArrayList<String>(1 + args.length);
            list.add("samtools");
            for (String arg : args) {
                list.add(arg);
            }

            ProcessBuilder builder = new ProcessBuilder(list);
            builder.redirectErrorStream(false);
            proc = builder.start();
            reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        }

        public String read() throws IOException {
            return reader.readLine();
        }

        @Override
        public void close() throws IOException {
            reader.close();
            proc.getInputStream().close();
            proc.getOutputStream().close();
            proc.getErrorStream().close();
            try {
                int exitValue = proc.waitFor();
                if (exitValue != 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String string : list) {
                        if (sb.length() > 0)
                            sb.append(" ");
                        sb.append(string);
                    }
                    System.err.println("Process: '" + sb + "' exit with error code(" + exitValue + ").");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }