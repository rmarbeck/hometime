package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ModelUpdateListener {
	private static Optional<List<BiConsumer<ListenableModel, String>>> consumers = Optional.empty();
	
	private ModelUpdateListener() {
		synchronized (consumers) {
		if (!consumers.isPresent())
			consumers = Optional.of(new ArrayList<BiConsumer<ListenableModel, String>>());
		}
	}
	
	public void addConsumer(BiConsumer<ListenableModel, String> newConsumer) {
		consumers.ifPresent(value ->  value.add(newConsumer));
	}
	
	public void removeConsumer(BiConsumer<ListenableModel, String> consumerToRemove) {
		consumers.ifPresent(value -> value.remove(consumerToRemove));
	}
	
	public static ModelUpdateListener of() {
		return new ModelUpdateListener();
	}
	
	public void fire(ListenableModel model, String action) {
		consumers.ifPresent(consumers -> System.err.println("--------LISTENER HAS "+consumers.parallelStream().count()+" consumers----------"));
		consumers.ifPresent(consumers -> consumers.parallelStream().forEach((current) -> current.accept(model, action)));
	}
	
	public void defaultActions() {
		this.addConsumer((model, action) -> System.err.println(".............consuming["+action+"] "+model.getClass().getName()+"............"));
		this.addConsumer((model, action) -> System.err.println("--------OTHER ACTION----------"));
		this.removeConsumer((model, action) -> System.err.println("--------OTHER ACTION----------"));
	}
}
