package fr.hometime.utils;

import java.util.List;
import java.util.Optional;

import models.Invoice;

public class InvoiceHelper {
    public static Optional<List<Invoice>> tryTofindAllByDescendingDate() {
        return Optional.ofNullable(Invoice.findAllByDescendingDate());
    }

}
