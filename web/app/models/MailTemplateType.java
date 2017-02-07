package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import fr.hometime.utils.ModelsHelper;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of a mail template type
 */
@Entity
public class MailTemplateType extends Model {
	private static final long serialVersionUID = -8741954708299310630L;

	@Id
	public Long id;
	
	@Constraints.Required
    @Formats.NonEmpty
	public String templateTypeName;
	
	public String displayName;
	
	public Long sortingIndex;
	
	public Boolean active = true;
	
	public MailTemplateType() {
		super();
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,MailTemplateType> find = new Model.Finder(String.class, MailTemplateType.class);
    
    public static List<MailTemplateType> findAll() {
        return find.all();
    }
    
    public static MailTemplateType findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<MailTemplateType> findByDisplayNameAsc() {
    	return find.where().orderBy("displayName ASC").findList();
    }
    
    public static List<Long> getIdsByDisplayNameAsc() {
    	return ModelsHelper.getIdsByDisplayNameAsc();
    }
    
    public static List<String> getDisplayNamesByDisplayNameAsc() {
    	return ModelsHelper.getDisplayNamesByDisplayNameAsc();
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Template type is called : " + this.templateTypeName);
    	content.append("]");
    	return content.toString();
    }
}


