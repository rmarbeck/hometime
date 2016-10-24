package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ListHelper<T> {
	List<T> workingList;
	
	public ListHelper(List<T> list) {
		workingList = list;
	}
	
	public static <U> ListHelper<U> of(List<U> list) {
		return new ListHelper<U>(list);
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
	
	public List<T> tryToGet() {
		return Optional.ofNullable(workingList).orElse(new ArrayList<T>());
	}
	
	public Stream<T> stream() {
		if (workingList == null)
			return Stream.empty();
		return workingList.stream();
	}
	
	public static <U> Stream<U> streamFromNullableList(List<U> list) {
		if (list == null)
			return Stream.empty();
		return list.stream();
	}

}
