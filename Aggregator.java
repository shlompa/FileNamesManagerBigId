package com.filenames.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggregator {
	private static Map<String, List<TextLocation>> nameLocations = new HashMap<>();

	public Aggregator() {
	}

	public Map<String, List<TextLocation>> getNameLocations() {
		return nameLocations;
	}

	/**
	 * 
	 * @param maps
	 */
	public static void run(List<Map<String, List<TextLocation>>> maps) {
		for (Map<String, List<TextLocation>> map : maps) {
			map.entrySet().stream()
					.forEach(entry -> 
						nameLocations.merge(entry.getKey(), entry.getValue(), (l1, l2) -> {
						List<TextLocation> l = new ArrayList<>(l1);
						l.addAll(l2);
						return l;
					}));
		}
		
		printMap();

	}

	private static void printMap() {
		for (String nameLoc : nameLocations.keySet()) {
			System.out.println("Name: " +nameLoc + "-> "+ nameLocations.get(nameLoc));
		}
	}
}
