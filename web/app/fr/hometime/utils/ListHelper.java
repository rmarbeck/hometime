package fr.hometime.utils;

import java.util.List;

public class ListHelper<T> {
	List<T> workingList;
	
	public ListHelper(List<T> list) {
		workingList = list;
	}
	
	public T getAnElementRandomly() {
		if (workingList == null || workingList.size()==0)
			return null;
		int sizeOfTheList = workingList.size();
		int upperBoundIncluded = sizeOfTheList - 1;
		T element = workingList.get(getRandomIndex(upperBoundIncluded)); 
		return element;
	}
	
	public static int getRandomIndex(int upperBoundIncluded) {
		return RandomHelper.getRandomInt(0, upperBoundIncluded);
	}

}
