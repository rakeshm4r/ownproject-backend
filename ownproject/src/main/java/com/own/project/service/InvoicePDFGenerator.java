package com.own.project.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.own.project.dao.InvoiceDao;
import com.own.project.model.OrdersStatus;
import com.own.project.model.PaymentDetails;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class InvoicePDFGenerator  implements InvoiceDao {
  private static final Logger log = LoggerFactory.getLogger(InvoicePDFGenerator.class);   

  public byte[] generateInvoice(OrdersStatus orderStatusId){
    
    log.info("In InvoicePDFGenerator of generateInvoice()");
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

    
        PaymentDetails paymentDetails = orderStatusId.getPayment();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Invoice Details
            PdfPTable invoiceTable = new PdfPTable(2);
            invoiceTable.setWidthPercentage(100);
            invoiceTable.setSpacingBefore(10f);
            
            // First Row: Invoice No (Left) and Date (Right)
            invoiceTable.addCell(getCell("Invoice No: " + orderStatusId.getInvoiceDetails().getInvoiceNumber(), PdfPCell.ALIGN_LEFT));
            invoiceTable.addCell(getCell("Date: " + orderStatusId.getFormattedBookedOrderdDate(), PdfPCell.ALIGN_RIGHT));

            // Second Row: Order No (Left)
            invoiceTable.addCell(getCell("Order No: " + orderStatusId.getOrders().getOrderNumber(), PdfPCell.ALIGN_LEFT));
            invoiceTable.addCell(getCell("", PdfPCell.ALIGN_LEFT)); // Empty cell on the right 

            document.add(invoiceTable);

            // Customer Details
            document.add(new Paragraph("\nBILL TO:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            UserTypeDetails user = orderStatusId.getUser();
            
            document.add(new Paragraph(user.getUserName() + "\n" + user.getAddress() + "\n" + user.getCity() + ", " + user.getState() + "\n" + user.getCountry()));

            document.add(new Paragraph("\nSHIP TO:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph(user.getUserName() + "\n" + user.getAddress()));

            // Products Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setWidths(new float[]{3, 1, 1, 1});

            // Table Headers
            addTableHeader(table, "Description", "Qty", "Unit Price", "Total");
            
            // Fetch order items
            List<Product> products = List.of(orderStatusId.getProduct());
            double total = 0;
            
            for (Product product : products) {
                total = paymentDetails.getPaymentAmount() ; // multiply by the quantity for the total
                addTableRow(table, product.getProductName(), String.valueOf(orderStatusId.getItemsQuantity()), "₹" + product.getProductPrice(), "₹" + total);
            }

            document.add(table);

            // Summary
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setSpacingBefore(10f);

            double tax = total * 0.08; // 8% tax
            summaryTable.addCell(getCell("Tax (8%):", PdfPCell.ALIGN_LEFT));
            summaryTable.addCell(getCell("₹" + tax, PdfPCell.ALIGN_RIGHT));

            double shipping = 2.99;
            summaryTable.addCell(getCell("Shipping:", PdfPCell.ALIGN_LEFT));
            summaryTable.addCell(getCell("₹" + shipping, PdfPCell.ALIGN_RIGHT));

            double totalAmount = paymentDetails.getPaymentAmount();
            summaryTable.addCell(getCell("Total:", PdfPCell.ALIGN_LEFT));
            summaryTable.addCell(getCell("₹" + totalAmount, PdfPCell.ALIGN_RIGHT));

            document.add(summaryTable);

            // Footer
            document.add(new Paragraph("\nThank you for Order!", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC)));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String desc, String qty, String unitPrice, String total) {
        table.addCell(getCell(desc, PdfPCell.ALIGN_LEFT));
        table.addCell(getCell(qty, PdfPCell.ALIGN_CENTER));
        table.addCell(getCell(unitPrice, PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell(total, PdfPCell.ALIGN_RIGHT));
    }

    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
