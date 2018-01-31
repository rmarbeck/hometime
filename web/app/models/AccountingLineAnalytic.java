package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

/**
 * Definition of an analytic information about an accounting line
 */
@Entity 
public class AccountingLineAnalytic extends Model {
	private static final long serialVersionUID = 160414590466772458L;

	@Id
	public Long id;
	
	public Long analyticCode = 0L;

	public Float marginRate = -1f;
	
	@Column(length = 10000)
	public String remark;
	
	@Column(length = 10000)
	public String reserved;
	
	@OneToOne
	public AccountingLine documentLine;
	
	public AccountingLineAnalytic() {
		
	}
	
	public AccountingLineAnalytic(AccountingLine documentLine) {
		this();
		this.documentLine = documentLine;
	}
	
	public AccountingLineAnalytic(AccountingLine documentLine, Long analyticCode) {
		this(documentLine, analyticCode, -1F);
	}
	
	public AccountingLineAnalytic(AccountingLine documentLine, Long analyticCode, Float marginRate) {
		this(documentLine);
		this.analyticCode = analyticCode;
		this.marginRate = marginRate;
	}
	
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AccountingLineAnalytic> find = new Model.Finder(String.class, AccountingLineAnalytic.class);
    
    public static List<AccountingLineAnalytic> findAll() {
        return find.all();
    }
    
    public static AccountingLineAnalytic findById(Long id) {
        return find.byId(id.toString());
    }

    public static Page<AccountingLineAnalytic> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("model", "%" + filter + "%"), Expr.ilike("brand", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
}

