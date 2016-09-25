package edu.terasort;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author aditya
 * This is a runnable class. Run method of this class merge a chunks of files.
 */
public class MergeFiles implements Runnable {

	private String destinationPath;
	private String fName1;
	private String fName2;

	public MergeFiles(String destinationPath, String name1, String name2) {
		this.destinationPath = destinationPath;
		this.fName1 = name1;
		this.fName2 = name2;
	}

	@Override
	public void run() {
		Scanner reader1 = null;
		Scanner reader2 = null;
		PrintWriter output = null;
		try {
			File file1 = new File(this.destinationPath + fName1);
			File file2 = new File(this.destinationPath + fName2);
			File file3 = new File(this.destinationPath + fName1 + "1");

			reader1 = new Scanner(file1);
			reader2 = new Scanner(file2);
			output = new PrintWriter(file3);

			String string1 = null;
			String string2 = null;
			String s1 = null;
			String s2 = null;
			int result;

			string1 = reader1.nextLine();
			string2 = reader2.nextLine();

			// Reading line and writing to a file in sorted order.
			while (true) {
				if (string1 != null && string1 != "" && string2 != null && string2 != "") {
					s1 = string1.substring(0, 10);
					s2 = string2.substring(0, 10);
				} else {
					break;
				}

				result = s1.compareTo(s2);

				if (result < 0) {
					output.write(string1 + "\r\n");
					string1 = "";
					if (reader1.hasNext()) {
						string1 = reader1.nextLine();
					} else {
						break;
					}
				} else {
					output.write(string2 + "\r\n");
					string2 = "";
					if (reader2.hasNext()) {
						string2 = reader2.nextLine();
					} else {
						break;
					}
				}

			}
			if (string1 != null && string1 != "") {
				output.write(string1 + "\r\n");
			}
			if (string2 != null && string2 != "") {
				output.write(string2 + "\r\n");
			}
			while (reader1 != null) {
				if (reader1.hasNext()) {
					output.write(reader1.nextLine() + "\r\n");
				} else {
					break;
				}
			}
			while (reader2 != null) {
				if (reader2.hasNext()) {
					output.write(reader2.nextLine() + "\r\n");
				} else {
					break;
				}
			}
			output.flush();

		} catch (IOException e) {
			System.out.println("Error in merge()\n" + e.getMessage());
		}

		finally {
			if (reader1 != null) {
				reader1.close();
			}
			if (reader2 != null) {
				reader2.close();
			}
			if (output != null) {
				output.close();
			}
			try {
				Path path = Paths.get(this.destinationPath + fName1);
				Files.deleteIfExists(path);
				path = Paths.get(this.destinationPath + fName2);
				Files.deleteIfExists(path);

			} catch (IOException e) {
				TeraSort.logger.info(e.getMessage());
			}
		}
	}

}
