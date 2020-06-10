package fr.hometime.utils;

public class EmailProviderFactory {
	public static EmailProvider of() {
		return new SendInBlueEmailProvider();
	}
}
