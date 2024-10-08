package basicio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataExchange {
	public void byteExchange(String source, String target) throws IOException {
		try(FileInputStream in = new FileInputStream(source);
				FileOutputStream out = new FileOutputStream(target)) {
			
			int byteData;
			
			while((byteData = in.read()) != -1) {
				out.write(byteData);
			}
		}
	}
	
	public void characterExchange(String source, String target) throws IOException {
		try(BufferedReader in = Files.newBufferedReader(Paths.get(source));
				BufferedWriter out = Files.newBufferedWriter( Paths.get(target))) {
			
			String line;
			while((line = in.readLine()) != null) {
				out.write(line);
			}
		}
	}
}
