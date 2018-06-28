package fr.hometime.utils;

import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;

public class MailAdapterException extends Exception {
	private static final long serialVersionUID = 2606617263043686601L;
	private String rootExceptionClass = "unknown";
	private Exception rootException = null;
	private MailjetResponse response = null;
		
	public MailAdapterException(Exception e) {
		if (e.getClass().equals(MailjetException.class))
			setRootExceptionClass("MailjetException");
		if (e.getClass().equals(MailjetSocketTimeoutException.class))
			setRootExceptionClass("MailjetSocketTimeoutException");
		setRootException(e);
	}

	public MailAdapterException(MailjetException me) {
		this((Exception) me);
	}
	
	public MailAdapterException(MailjetSocketTimeoutException mtoe) {
		this((Exception) mtoe);
	}
	
	public MailAdapterException(MailjetResponse response) {
		this.response = response;
		setRootExceptionClass("Functionnal");
	}

	public String getRootExceptionClass() {
		return rootExceptionClass;
	}

	public void setRootExceptionClass(String rootExceptionClass) {
		this.rootExceptionClass = rootExceptionClass;
	}

	public Exception getRootException() {
		return rootException;
	}

	public void setRootException(Exception rootException) {
		this.rootException = rootException;
	}
	
	public String toString() {
		if (response != null)
			return "---------------------> "+response.getData();
		return "!!!!!!!!!!!!!!!!!!!!!> "+rootExceptionClass;
	}
}
