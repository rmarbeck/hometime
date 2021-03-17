package fr.hometime.utils;

import javax.persistence.MappedSuperclass;

import play.db.ebean.Model;

@MappedSuperclass
public class ListenableModel extends Model {
	private static transient ModelUpdateListener listener = ModelUpdateListener.of();
	
	public ListenableModel() {
		super();
	}
	
	public static ModelUpdateListener getListener() {
		return listener;
	}
	
	private void fire(String action) {
		listener.fireAsync(this, action);
	}

	@Override
	public void delete() {
		super.delete();
		fire("deleting");
	}

	@Override
	public void delete(String arg0) {
		super.delete(arg0);
		fire("deleting");
	}

	@Override
	public void save() {
		super.save();
		fire("saving");
	}

	@Override
	public void save(String arg0) {
		super.save(arg0);
		fire("saving");
	}

	@Override
	public void update() {
		super.update();
		fire("updating");
	}

	@Override
	public void update(Object arg0, String arg1) {
		super.update(arg0, arg1);
		fire("updating");
	}

	@Override
	public void update(Object arg0) {
		super.update(arg0);
		fire("updating");
	}

	@Override
	public void update(String arg0) {
		super.update(arg0);
		fire("updating");
	}

}
