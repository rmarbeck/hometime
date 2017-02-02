package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of a mail template
 */
@Entity
public class MailTemplate extends Model {
	private static final long serialVersionUID = 3301115479489050285L;

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
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Template is called : " + this.templateName);
    	content.append(", MailTemplateType is : " + this.type);
    	content.append("]");
    	return content.toString();
    }
}


