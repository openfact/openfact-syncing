package org.clarksnut.mapper.document.basic.invoice;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.basic.BasicUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class BasicInvoiceBeanAdapter implements DocumentBean {

    private final InvoiceType type;

    public BasicInvoiceBeanAdapter(InvoiceType type) {
        this.type = type;
    }

    private Optional<PayableAmountType> getPayableAmount() {
        MonetaryTotalType legalMonetaryTotal = type.getLegalMonetaryTotal();
        if (legalMonetaryTotal != null) {
            return Optional.ofNullable(legalMonetaryTotal.getPayableAmount());
        }
        return Optional.empty();
    }

    private Optional<AddressType> getSupplierPostalAddress() {
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            PartyType party = accountingSupplierParty.getParty();
            if (party != null) {
                return Optional.ofNullable(party.getPostalAddress());
            }
        }
        return Optional.empty();
    }

    private Optional<AddressType> getCustomerPostalAddress() {
        CustomerPartyType accountingCustomerParty = type.getAccountingCustomerParty();
        if (accountingCustomerParty != null) {
            PartyType party = accountingCustomerParty.getParty();
            if (party != null) {
                return Optional.ofNullable(party.getPostalAddress());
            }
        }
        return Optional.empty();
    }

    @Override
    public String getAssignedId() {
        return type.getIDValue();
    }

    @Override
    public Double getAmount() {
        Optional<PayableAmountType> payableAmount = getPayableAmount();
        if (payableAmount.isPresent()) {
            PayableAmountType payableAmountType = payableAmount.get();
            BigDecimal value = payableAmountType.getValue();
            if (value != null) {
                return value.doubleValue();
            }
        }
        return null;
    }

    @Override
    public Double getTax() {
        return type.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    @Override
    public String getCurrency() {
        Optional<PayableAmountType> payableAmount = getPayableAmount();
        if (payableAmount.isPresent()) {
            PayableAmountType payableAmountType = payableAmount.get();
            return payableAmountType.getCurrencyID();
        }
        return null;
    }

    @Override
    public Date getIssueDate() {
        return BasicUtils.toDate(type.getIssueDate(), Optional.ofNullable(type.getIssueTime()));
    }

    @Override
    public String getSupplierName() {
        return BasicUtils.getSupplierName(type.getAccountingSupplierParty());
    }

    @Override
    public String getSupplierAssignedId() {
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            return accountingSupplierParty.getCustomerAssignedAccountIDValue();
        }
        return null;
    }

    @Override
    public String getSupplierStreetAddress() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            return addressType.getStreetNameValue();
        }
        return null;
    }

    @Override
    public String getSupplierCity() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            if (addressType.getCountry() != null) {
                return BasicUtils.toCityString(addressType);
            }
        }
        return null;
    }

    @Override
    public String getSupplierCountry() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            CountryType country = addressType.getCountry();
            if (country != null) {
                return country.getIdentificationCodeValue();
            }
        }
        return null;
    }

    @Override
    public String getCustomerName() {
        return BasicUtils.getCustomerName(type.getAccountingCustomerParty());
    }

    @Override
    public String getCustomerAssignedId() {
        CustomerPartyType accountingCustomerParty = type.getAccountingCustomerParty();
        if (accountingCustomerParty != null) {
            return accountingCustomerParty.getCustomerAssignedAccountIDValue();
        }
        return null;
    }

    @Override
    public String getCustomerStreetAddress() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            return addressType.getStreetNameValue();
        }
        return null;
    }

    @Override
    public String getCustomerCity() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            if (addressType.getCountry() != null) {
                return BasicUtils.toCityString(addressType);
            }
        }
        return null;
    }

    @Override
    public String getCustomerCountry() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            CountryType country = addressType.getCountry();
            if (country != null) {
                return country.getIdentificationCodeValue();
            }
        }
        return null;
    }
}
