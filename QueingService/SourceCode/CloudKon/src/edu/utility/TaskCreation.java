package edu.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class TaskCreation {

	public static void main(String[] args) {
		File file = null;
		FileWriter fileWriter = null;
		try {
			Properties property = new Properties();
			property.load(new FileInputStream(new File("./resources/config.properties")));
			file = new File("./resources/workloadFile");
			fileWriter = new FileWriter(file);
			//int noOfTasks = Integer.parseInt(property.getProperty("noOfTasks"));
			//int sleepTime = Integer.parseInt(property.getProperty("sleepTime"));
			int noOfTasks = Integer.parseInt(args[0]);
			int sleepTime = Integer.parseInt(args[1]);

			for (int i = 1; i <= noOfTasks; i++) {
				fileWriter.write("sleep_task" + i + " " + sleepTime + "\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
