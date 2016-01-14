package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

/**
 * Definition of an Accounting document
 */
@Entity
public class AccountingDocument extends Model {
	private static final long serialVersionUID = -7480911363546156415L;
	
	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@ManyToOne
	public Customer customer;
	
	@OneToMany(mappedBy="document", cascade = CascadeType.ALL)
	public List<AccountingLine> lines;
	
	@Lob
	public byte[] documentData;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public AccountingDocument() {
		this.creationDate = new Date();
	}
	
	
	public AccountingDocument(Customer customer) {
		this();
		this.customer = customer;
	}
	
}

