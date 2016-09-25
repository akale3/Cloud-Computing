package edu.sort;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TeraSort {

	private static Logger logger = Logger.getLogger(TeraSort.class.getName());
	private static FileHandler filehandler;
	
	public TeraSort() {
		super();
	}

	public static void main(String[] args) throws Exception {
		
		filehandler = new FileHandler("./MapReduceTeraSort.log");  
		SimpleFormatter formatter = new SimpleFormatter();  
		filehandler.setFormatter(formatter);
        logger.addHandler(filehandler);
		
        long startTime = System.currentTimeMillis();
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Tera Sort");
		job.setJarByClass(TeraSort.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		long endTime = System.currentTimeMillis();
		logger.info("Total Time for Execution of MapReduce is :" + (endTime-startTime)/1000 + " Seconds");
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

	private Text word = new Text();
	private Text word1 = new Text();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();
		String key1 = line.substring(0, 10);
		String val = line.substring(10);
		word.set(key1);
		word1.set(val);
		context.write(word, word1);

	}
}

class IntSumReducer extends Reducer<Text, Text, Text, Text> {

	private Text word = new Text();
	private Text word1 = new Text();

	public void reduce(Text key, Text value, Context context) throws IOException, InterruptedException {
		word.set(key.toString() + value.toString());
		word1.set("");
		context.write(word, word1);
	}
}
