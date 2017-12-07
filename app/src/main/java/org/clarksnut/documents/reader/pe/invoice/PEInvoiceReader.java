package org.clarksnut.documents.reader.pe.invoice;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.files.XmlUBLFileModel;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.math.BigDecimal;

@Stateless
@SupportedType(value = "Invoice")
public class PEInvoiceReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEInvoiceReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "Invoice";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        InvoiceType invoiceType;
        try {
            invoiceType = ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(invoiceType.getID().getValue());
        documentEntity.setSupplierAssignedId(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(invoiceType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(invoiceType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(invoiceType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(invoiceType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID().value());
        documentEntity.setAmount(invoiceType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Tax
        documentEntity.setTax(invoiceType.getTaxTotal().stream()
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType supplierPostalAddressType = invoiceType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(supplierPostalAddressType.getStreetName().getValue());
        documentEntity.setSupplierCity(supplierPostalAddressType.getCitySubdivisionName().getValue() + ", " + supplierPostalAddressType.getCityName().getValue() + ", " + supplierPostalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = invoiceType.getAccountingCustomerParty().getParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            documentEntity.setCustomerStreetAddress(customerPostalAddressType.getStreetName().getValue());
            documentEntity.setCustomerCity(customerPostalAddressType.getCitySubdivisionName().getValue() + ", " + customerPostalAddressType.getCityName().getValue() + ", " + customerPostalAddressType.getCitySubdivisionName().getValue());
            documentEntity.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCode().getValue());
        }

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return invoiceType;
            }
        };
    }

}