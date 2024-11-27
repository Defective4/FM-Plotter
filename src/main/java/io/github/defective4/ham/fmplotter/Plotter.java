package io.github.defective4.ham.fmplotter;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Plotter {

    private static class ModificationRow {
        private final long time, difference, size;

        private ModificationRow(long time, long difference, long size) {
            this.time = time;
            this.difference = difference;
            this.size = size;
        }

        @Override
        public String toString() {
            return "ModificationRow [time=" + new Date(time) + ", difference=" + difference + "B, size=" + size + "B]";
        }

    }

    private static final DateFormat TIME = new SimpleDateFormat("HH:mm:ss");

    private Plotter() {}

    public static void plot(BufferedReader input, OutputStream output, int minDiff) throws Exception {
        long last = 0;
        List<ModificationRow> rows = new ArrayList<>();
        while (true) {
            String line = input.readLine();
            if (line == null) break;
            String[] split = line.split(" ");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(TIME.parse(split[3]));
            int min = calendar.get(Calendar.MINUTE);
            min -= min % 10;
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);

            long time = calendar.getTimeInMillis();
            long size = Long.parseLong(split[10]);
            long diff = size - last;
            if (diff < minDiff) diff = 0;
            last = size;

            rows.add(new ModificationRow(time, diff, size));
        }

        rows.sort((a, b) -> (int) (a.time - b.time));

        PrintWriter pw = new PrintWriter(output);
        pw.println("Time;Difference;Total");
        for (ModificationRow row : rows) {
            String time = TIME.format(new Date(row.time));
            time = time.substring(0, time.length() - 3);
            pw.println(String.format("%s;%s;%s", time, row.difference / 1024, row.size / 1024));
        }
        pw.flush();
    }
}
