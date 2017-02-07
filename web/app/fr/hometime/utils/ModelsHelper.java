package fr.hometime.utils;

import java.util.List;
import java.util.stream.Collectors;

import models.MailTemplateType;

public class ModelsHelper {
    public static List<Long> getIdsByDisplayNameAsc() {
    	return MailTemplateType.findByDisplayNameAsc().stream().map(template -> template.id).collect(Collectors.toList());
    }
    
    public static List<String> getDisplayNamesByDisplayNameAsc() {
    	return MailTemplateType.findByDisplayNameAsc().stream().map(template -> template.displayName).collect(Collectors.toList());
    }

}
