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
		listener.fire(this, action);
	}

	@Override
	public void delete() {
		fire("deleting");
		super.delete();
	}

	@Override
	public void delete(String arg0) {
		fire("deleting");
		super.delete(arg0);
	}

	@Override
	public void save() {
		fire("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$   saving  $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		super.save();
	}

	@Override
	public void save(String arg0) {
		fire("saving");
		super.save(arg0);
	}

	@Override
	public void update() {
		fire("updating");
		super.update();
	}

	@Override
	public void update(Object arg0, String arg1) {
		fire("updating");
		super.update(arg0, arg1);
	}

	@Override
	public void update(Object arg0) {
		fire("updating");
		super.update(arg0);
	}

	@Override
	public void update(String arg0) {
		fire("updating");
		super.update(arg0);
	}

}
