package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.AccountingLine.LineType;
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
	public List<AccountingLine> lines = null;
	
	@Lob
	public byte[] documentData;
	
	@Transient
	public int linesIndex = 0;

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
	
	public void deleteLines() {
		if (id != null)
			for(AccountingLine line : AccountingLine.findByAccountingDocumentId(id))
				line.delete();
	}
	
	public void reorderLines() {
		if (id != null)
			lines = AccountingLine.findByAccountingDocumentId(id);
	}
	
	public List<AccountingLine> retrieveLines() {
		return AccountingLine.findByAccountingDocumentId(id);
	}
	
	public void addLine(LineType type, String description, Long unit, Float unitPrice) {
		if (lines == null)
			lines = new ArrayList<AccountingLine>();
		lines.add(new AccountingLine(this, type, description, unit, unitPrice, linesIndex++));
	}
}

