package fr.hometime.utils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;


import models.AccountingDocument;
import models.AccountingLine;

/**
 * Helper for managing Accounting Documents
 * 
 * @author Raphael
 *
 */

public class AccountingDocumentHelper {
	public static Optional<List<AccountingLine>> retrieveInvoiceLines(int nbOfMonthBack) {
		return retrieveInvoiceLines(() -> AccountingDocument.find.where().gt("creation_date", DateHelper.toDate(ZonedDateTime.now().minusMonths(nbOfMonthBack).toInstant())).findList());
	}
	
	public static Optional<List<AccountingLine>> retrieveInvoiceLines() {
		return retrieveInvoiceLines(() -> AccountingDocument.find.all()); 
	}
	
	private static Optional<List<AccountingLine>> retrieveInvoiceLines(Supplier<List<AccountingDocument>> supplier) {
		List<AccountingDocument> documents = supplier.get();
		if (documents != null) {
			List<AccountingLine> lines = documents.stream().flatMap(document -> document.retrieveLines().stream()).collect(Collectors.toList());
			if (lines != null)
				return Optional.of(lines);
		}
		return Optional.empty(); 
	}
}