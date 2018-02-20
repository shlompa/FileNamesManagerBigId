package com.filenames.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamesMatcher implements Callable<Map<String, List<TextLocation>>> {

	private static final String NAMES_REGEXP = "James|John|Robert|Michael|William|David|Richard|Charles|Joseph|Thomas|Christopher|Daniel|Paul|Mark|Donald|George|Kenneth|Steven|Edward|Brian|Ronald|Anthony|Kevin|Jason|Matthew|Gary|Timothy|Jose|Larry|Jeffrey|Frank|Scott|Eric|Stephen|Andrew|Raymond|Gregory|Joshua|Jerry|Dennis|Walter|Patrick|Peter|Harold|Douglas|Henry|Carl|Arthur|Ryan|Roger";
	private static Pattern pattern = Pattern.compile(NAMES_REGEXP);
	
	//text input
	private String text;
	
	//used in order make sure all the threads are waiting for each other
	private Phaser phaser;

	public NamesMatcher(String text) {
		this.text = text;
	}

	public NamesMatcher(String text, Phaser phaser) {
		this(text);
		this.phaser = phaser;

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public Map<String, List<TextLocation>> call() throws Exception {

		Map<String, List<TextLocation>> result = new HashMap<>();
		if (text != null) {
			String[] textLines = text.split("\n");

			//getting the number of lines from the first line
			long numberOfLines = Long.valueOf(textLines[0]);
			long numberOfCharacters = 0;

			for (int i = 1; i < textLines.length; i++) {
				Matcher matcher = pattern.matcher(textLines[i]);
				while (matcher.find()) {
					String name = matcher.group();
					List<TextLocation> locations = result.get(name);
					if (locations == null) {
						locations = new ArrayList<>();
					}

					TextLocation newLoc = new TextLocation(numberOfLines, numberOfCharacters + matcher.start());
					locations.add(newLoc);
					result.put(name, locations);

				}
				// updating the num of characters with the current
				// number of characters
				numberOfCharacters += textLines[i].length();
			}
		}
		phaser.arriveAndAwaitAdvance();
		return result;

	}

}