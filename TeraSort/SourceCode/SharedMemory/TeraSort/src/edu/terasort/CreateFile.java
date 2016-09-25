package edu.terasort;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author aditya
 * This is a runnable class. Run method of this class creates a chunk of files.
 */
public class CreateFile implements Runnable {

	private String filePath;
	private String destinationPath;
	private double startPos;
	private double endPos;

	public CreateFile(String filePath, String destinationPath, double startPos, double endPos) {
		this.filePath = filePath;
		this.destinationPath = destinationPath;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	@Override
	public void run() {

		File file = null;
		RandomAccessFile randomFileAccess = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		FileOutputStream fileOutputStream = null;
		try {
			file = new File(this.filePath);
			randomFileAccess = new RandomAccessFile(file, "r");
			double seekPosition = startPos * 100; 
			// Pointing to a specified line of an large input file
			randomFileAccess.seek((long) seekPosition);

			int size = (int) ((endPos - startPos) + 1);
			byte[] bytesData = new byte[100 * size];
			randomFileAccess.read(bytesData, 0, bytesData.length);
			inputStream = new ByteArrayInputStream(bytesData);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String lineData = null;
			// Storing values in a TreeMap as key value pairs
			TreeMap<String, String> dataMap = new TreeMap<String, String>();
			while ((lineData = bufferedReader.readLine()) != null) {
				dataMap.put(lineData.substring(0, 10), lineData.substring(10));
			}

			String data;
			byte[] lineByte = new byte[100];
			int j = 0;
			// Appending sorted lines output and converting to an byte array
			for (Map.Entry<String, String> entry : dataMap.entrySet()) {
				data = entry.getKey() + "" + entry.getValue();
				lineByte = data.getBytes();
				for (int i = 0; i < lineByte.length; i++) {
					bytesData[j] = lineByte[i];
					j++;
				}
				j += 2;
			}

			//Writing a byte array to a file.
			File outputFile = new File(this.destinationPath + "File_" + this.startPos);
			fileOutputStream = new FileOutputStream(outputFile);
			fileOutputStream.write(bytesData);
			
		} catch (IOException e) {
			TeraSort.logger.info(e.getMessage());
		} finally {
			try {
				randomFileAccess.close();
				fileOutputStream.close();
			} catch (IOException e) {
				TeraSort.logger.info(e.getMessage());
			}
		}

	}
}
