package com.zqh.mr;

import java.util.List;
import java.util.Map;

public interface MapReduce {

	Map<Object, Integer> sortMap(Map<Object, Integer> mapToSort);

	List<Object> readFile(String pathToFile);

	void displayMap(Map<Object, Integer> map, int values);

	List<Object> generateData();

	Map<String, Integer> simpleWordCounting(List<Object> words);

	Map<Object, Integer> mapReduce(List<Object> data);

}
