package com.filenames.manager;

public class TestFileNamesManager {

	public static void main(String[] args) {
		try {
			FileNamesManager fileNamesManager = new FileNamesManager(15);
			fileNamesManager.run();
		} catch (FilesNameMatcherException e) {
			System.out.println("FileNamesManager failed: " + e.getStackTrace());
		}
	}

}