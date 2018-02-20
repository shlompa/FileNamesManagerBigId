package com.filenames.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;

/**
 * This class is responsible to read a file, executes threads that will 
 * return the file index locations of the most common American names mentioned in the file 
 *
 */
public class FileNamesManager {
	private Integer linesOffsetSoFar = 0;
	private ExecutorService exec;
	private static final String FILE_NAME = "http://norvig.com/big.txt";
	private static final Integer CHUNK_SIZE = 1000;
	private Phaser phaser;

	public FileNamesManager(Integer numOfThreads) {
		this.exec = Executors.newFixedThreadPool(numOfThreads);
		phaser = new Phaser();
	}

	public void run() throws FilesNameMatcherException  {		
		List<Map<String, List<TextLocation>>> maps = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		
		phaser.register();
		
		try {
			List<Future<Map<String, List<TextLocation>>>> futureResults = getFileNameMatcheresults();
	
			// wait for the tasks to finish and collect results
			for (Future<Map<String, List<TextLocation>>> future : futureResults) {
				maps.add(future.get());
			}
			exec.shutdown();

			System.out.println("number of : "+ 			phaser.getUnarrivedParties());

			// the aggregator will merge all the maps and print them
			Aggregator.run(maps);
		
			long duration = System.currentTimeMillis() - startTime;
			System.out.println("duration: " + duration);

		} catch(Exception e) {
			throw new FilesNameMatcherException(e);
		}
	}

	/**
	 * 
	 * @return future list which is the results of all the running name matcher
	 *         threads
	 * @throws IOException 
	 */
	private List<Future<Map<String, List<TextLocation>>>> getFileNameMatcheresults() throws IOException {
		String inputLine;
		
		// the future list to be returned
		List<Future<Map<String, List<TextLocation>>>> futureResults = new ArrayList<>();

		URL url = new URL(FILE_NAME);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		// each chunk will hold in the first line the number of lines
		StringBuilder lines = new StringBuilder("0\n");

		// indicates if we finished reading a whole block
		boolean isEndBlock = true;

		while ((inputLine = in.readLine()) != null) {
			isEndBlock = false;
			++linesOffsetSoFar;

			// adding the input line length plus the line character
			lines.append(inputLine).append("\n");

			// put in the queue the text every 1000 lines
			if (linesOffsetSoFar % CHUNK_SIZE == 0) {
				// submitting the name matcher callable
				Future<Map<String, List<TextLocation>>> future = exec.submit(new NamesMatcher(lines.toString(), phaser));
				futureResults.add(future);
				lines = new StringBuilder(linesOffsetSoFar + "\n");
				isEndBlock = true;
			}
		}

		// if finished reading the file
		if (!isEndBlock) {
			Future<Map<String, List<TextLocation>>> future = exec.submit(new NamesMatcher(lines.toString(), phaser));
			futureResults.add(future);
		}
		
		in.close();		
		return futureResults;
	}
}
