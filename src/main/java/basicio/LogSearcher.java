package basicio;

import java.io.*;
import java.util.regex.*;

public class LogSearcher {

    private final String logDirectory = "logs/";

    public void searchLogByPattern(String pattern) throws IOException {
        File dir = new File(logDirectory);
        File[] files = dir.listFiles();
        Pattern regex = Pattern.compile(pattern);

        for (File file : files) {
            Matcher matcher = regex.matcher(file.getName());
            if (matcher.find()) {
                System.out.println("Found log: " + file.getName());
                displayLog(file);
            }
        }
    }

    private void displayLog(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
