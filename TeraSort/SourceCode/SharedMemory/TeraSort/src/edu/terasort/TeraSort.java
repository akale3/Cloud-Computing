package edu.terasort;
	
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.util.Properties;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
	import java.util.logging.FileHandler;
	import java.util.logging.Logger;
	import java.util.logging.SimpleFormatter;
	
	public class TeraSort {
	
		public static Properties property;
		public static Logger logger = Logger.getLogger(TeraSort.class.getName());
		private static FileHandler filehandler;
		
		public static void main(String[] args) {
	
			property = new Properties();
			try {
				//load an configuration file
				property.load(new FileInputStream(new File("./resources/config.properties")));
				//create log file
				filehandler = new FileHandler("./resources/TeraSort.log");  
				SimpleFormatter formatter = new SimpleFormatter();  
				filehandler.setFormatter(formatter);
		        logger.addHandler(filehandler);
				// Sort Big file into chunks using multiThreading.
		        divideInChunks();
		        
		        // Merge chunks of file into a single file using multiThreading.
				mergeFromChunks();
			} catch (FileNotFoundException e) {
				logger.info(e.getMessage());
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}
	
		/**
		 * This method pass the starting position and ending position of a file to a thread.
		 * That thread creates a file chunk from that starting to ending position.
		 */
		private static void divideInChunks() {
			logger.info("Chunks creation started.");
			double startTime = System.currentTimeMillis();
			int threadPoolThread = Integer.parseInt(property.getProperty("threadPoolThread"));
			// Create a ThreadPool
			ExecutorService executor = Executors.newFixedThreadPool(threadPoolThread);
			double numberOfLines = Integer.parseInt(property.getProperty("noOfLinesInSingleRead"));
			double noOfThreads = Integer.parseInt(property.getProperty("noOfThreads"));
			double startPos;
			double endPos;
			String filePath = property.getProperty("sourceFile");
			String destinationFolder = property.getProperty("DestinationFolder");
			for (int i = 0; i < noOfThreads; i++) {
				startPos = i * numberOfLines;
				endPos = ((i + 1) * numberOfLines) - 1;
				// call a Thread for creation of chunks
				Runnable createFileThread = new Thread(new CreateFile(filePath, destinationFolder, startPos, endPos));
				executor.execute(createFileThread);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
			}
			long endTime = System.currentTimeMillis();
			logger.info("Total time taken divide and sort a file is : " + (endTime - startTime) / 1000 + " Seconds");
			logger.info("Finished all creation of chunks");
		}
	
		/**
		 * This method merge the two chunks of file into 1 file.
		 * This process is repeated till there remains single file in output folder 
		 */
		private static void mergeFromChunks() {
			logger.info("Merging started.");
			double startTime = System.currentTimeMillis();
			ExecutorService executor;
			int noOfThreads = 0;
			String destinationPath = property.getProperty("DestinationFolder");
			File folder = new File(destinationPath);
			int threadPoolThread = Integer.parseInt(property.getProperty("threadPoolThread"));
			while (true) {
				// Take all files of a folder
				File[] listOfFiles = folder.listFiles();
				int noOfFiles = listOfFiles.length;
				if (noOfFiles == 1) {
					break;
				}
				noOfThreads = noOfFiles / 2;
				if(noOfFiles <= threadPoolThread){
					threadPoolThread = noOfThreads;
				}
				// create a thread pool
				executor = Executors.newFixedThreadPool(threadPoolThread);
				int j = 0;
				for (int i = 0; i < noOfThreads; i++) {
					Runnable mergeFileThread = new Thread(
							new MergeFiles(destinationPath,listOfFiles[j].getName(), listOfFiles[j + 1].getName()));
					// execute a thread for merging two files
					executor.execute(mergeFileThread);
					j += 2;
				}
				executor.shutdown();
				// process is waited till all files are merged.
				while (!executor.isTerminated()) {
				}
			}
			double endTime = System.currentTimeMillis();
			logger.info("Total time taken to sort a "+ property.getProperty("sourceFile") + " file is : " + (endTime - startTime) / 1000 + " Seconds");
		}
	}