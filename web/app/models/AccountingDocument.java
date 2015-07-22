package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

/**
 * Definition of an Accounting document
 */
@MappedSuperclass
@Entity
public abstract class AccountingDocument extends Model {
	private static final long serialVersionUID = -7480911363546156415L;
	
	@Id
	public Long id;
	
	@OneToMany(mappedBy="document", cascade = CascadeType.ALL)
	public List<AccountingLine> lines;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}

