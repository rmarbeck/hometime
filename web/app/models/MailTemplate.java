package models;

import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of a mail template
 */
@Entity
public class MailTemplate extends Model implements CrudReady<MailTemplate, MailTemplate> {
	private static final long serialVersionUID = 3301115479489050285L;
	private static MailTemplate singleton = null;

	@Id
	public Long id;
	
	@Constraints.Required
    @Formats.NonEmpty
	public String templateName;
	
	@ManyToOne
	public MailTemplateType type;
	
	public String displayName;
	
	public String title;
	
	@Column(length = 20000)
	public String body;
	
	public Long sortingIndex;
	
	public Boolean active = true;
	
	public MailTemplate() {
		super();
	}
	
	public static MailTemplate of() {
    	if (singleton == null)
    		singleton = new MailTemplate();
    	return singleton;
    }
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,MailTemplate> find = new Model.Finder(String.class, MailTemplate.class);
    
    public static List<MailTemplate> findAll() {
        return find.all();
    }
    
    public static MailTemplate findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<MailTemplate> findByType(MailTemplateType type) {
        return find.where().eq("type", type).findList();
    }
    
    public static Page<MailTemplate> page(int page, int pageSize, String sortBy, String order, String filter) {
    	if (sortBy == null || sortBy.equals("")) {
    		sortBy = "title";
    		order = "ASC";
    	}
        return 
            find.fetch("type").where().disjunction().add(Expr.ilike("title", "%" + filter + "%"))
            										 .add(Expr.contains("body", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
	public static MailTemplate duplicate(Long id) {
		Optional<MailTemplate> foundTemplate = Optional.ofNullable(findById(id));
    	if (foundTemplate.isPresent())
    		return duplicateTemplate(foundTemplate.get());
    	return new MailTemplate();
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Template is called : " + this.templateName);
    	content.append(", MailTemplateType is : " + this.type);
    	content.append("]");
    	return content.toString();
    }

	@Override
	public Finder<String, MailTemplate> getFinder() {
		return find;
	}

	@Override
	public Page<MailTemplate> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private static MailTemplate duplicateTemplate(MailTemplate templateToDuplicate) {
		MailTemplate duplicatedTemplate = new MailTemplate();
		duplicatedTemplate.type = templateToDuplicate.type;
		duplicatedTemplate.title = templateToDuplicate.title;
		duplicatedTemplate.body = templateToDuplicate.body;
		duplicatedTemplate.templateName = templateToDuplicate.templateName+"_copy";
		duplicatedTemplate.displayName = templateToDuplicate.displayName;
		return duplicatedTemplate;
	}
	
	public String getTypeDisplayName() {
		return this.type.displayName;
	}
}


