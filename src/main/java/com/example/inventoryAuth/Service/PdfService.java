package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ReortDto.*;
import com.example.inventoryAuth.DTO.ReortDto.BalanceResponse;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PdfService {

    // ---------- COMMON CELL--------------------------------------------------------------------------------------//
    private Cell cell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(9))
                .setBorder(Border.NO_BORDER)
                .setPadding(4);
    }

    //---------HEADER: NO BORDER-----------------------------------------------------------------------------------//
    private Cell header(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Paragraph title(String text) {
        return new Paragraph(text)
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Paragraph generated() {
        return new Paragraph("Generated: " + LocalDateTime.now())
                .setFontSize(9)
                .setTextAlignment(TextAlignment.LEFT);
    }

    // ---------- BALANCE REPORT -----------------------------------------------------------------------------------//
    public byte[] generateBalancePdf(List<BalanceResponse> list) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.add(title("Balance Summary Report"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2,2,2,3,2,2,2,2}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1f));


    //---------HEADER------------------------------------------------------------------------------------------------------//
        table.addHeaderCell(header("Item"));
        table.addHeaderCell(header("Code"));
        table.addHeaderCell(header("Group"));
        table.addHeaderCell(header("Description"));
        table.addHeaderCell(header("Unit"));
        table.addHeaderCell(header("Current"));
        table.addHeaderCell(header("Actual"));
        table.addHeaderCell(header("Value"));

        for (BalanceResponse dto : list) {
            table.addCell(cell(dto.getItemName()));
            table.addCell(cell(dto.getItemCode()));
            table.addCell(cell(dto.getItemGroupName()));
            table.addCell(cell(dto.getDescription()));
            table.addCell(cell(dto.getUnit()));
            table.addCell(cell(String.valueOf(dto.getCurrentBalance())));
            table.addCell(cell(String.valueOf(dto.getActualBalance())));
            table.addCell(cell(String.valueOf(dto.getValue())));
        }

        document.add(table);
        document.add(generated());

        document.close();
        return out.toByteArray();
    }

    // ---------- REORDER REPORT ------------------------------------------------------------------------------------//
    public byte[] generateReorderPdf(List<ReOrderResponse> list) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.add(title("Reorder Report"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2,2,3,2,2,2,2,2}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1f));

        table.addHeaderCell(header("Item"));
        table.addHeaderCell(header("Code"));
        table.addHeaderCell(header("Description"));
        table.addHeaderCell(header("Unit"));
        table.addHeaderCell(header("Current"));
        table.addHeaderCell(header("Actual"));
        table.addHeaderCell(header("Min Level"));
        table.addHeaderCell(header("Reorder Qty"));

        for (ReOrderResponse dto : list) {
            table.addCell(cell(dto.getItemName()));
            table.addCell(cell(dto.getItemCode()));
            table.addCell(cell(dto.getDescription()));
            table.addCell(cell(dto.getUnit()));
            table.addCell(cell(String.valueOf(dto.getCurrentBalance())));
            table.addCell(cell(String.valueOf(dto.getActualBalance())));
            table.addCell(cell(String.valueOf(dto.getMinLevelQuantity())));
            table.addCell(cell(String.valueOf(dto.getReorderQuantity())));
        }

        document.add(table);
        document.add(generated());

        document.close();

        System.out.println("pppddff"+document);
        return out.toByteArray();
    }

    // ---------- GRN REPORT-----------------------------------------------------------------------------------------//
    public byte[] generateGrnPdf(List<GrnWiseReportResponse> list) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.add(title("GRN Wise Balance Report"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2,2,2,3,2,2,2,2,3}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1f));

        table.addHeaderCell(header("Item"));
        table.addHeaderCell(header("Code"));
        table.addHeaderCell(header("Group"));
        table.addHeaderCell(header("Description"));
        table.addHeaderCell(header("Unit"));
        table.addHeaderCell(header("Current"));
        table.addHeaderCell(header("Actual"));
        table.addHeaderCell(header("Value"));
        table.addHeaderCell(header("GRN Details"));

        for (GrnWiseReportResponse dto : list) {

            StringBuilder grn = new StringBuilder();

            if (dto.getGrnNumber() != null && dto.getGrnDate() != null) {
                for (int i = 0; i < dto.getGrnNumber().size(); i++) {
                    grn.append(dto.getGrnNumber().get(i))
                            .append(" (")
                            .append(dto.getGrnDate().get(i))
                            .append(")\n");
                }
            }

            table.addCell(cell(dto.getItemName()));
            table.addCell(cell(dto.getItemCode()));
            table.addCell(cell(dto.getItemGroupName()));
            table.addCell(cell(dto.getDescription()));
            table.addCell(cell(dto.getUnit()));
            table.addCell(cell(String.valueOf(dto.getCurrentBalance())));
            table.addCell(cell(String.valueOf(dto.getActualBalance())));
            table.addCell(cell(String.valueOf(dto.getValue())));
            table.addCell(cell(grn.toString()));
        }

        document.add(table);
        document.add(generated());

        document.close();
        return out.toByteArray();
    }
}