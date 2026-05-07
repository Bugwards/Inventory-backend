package com.example.inventoryAuth.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import com.example.inventoryAuth.Service.ReportService;
import com.example.inventoryAuth.Service.PdfService;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.DTO.ReortDto.ReOrderResponse;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    @Autowired
    private ReportService service;

    @Autowired
    private PdfService pdfService;



    //--for select Item group--------------------------------------------------------------------------------------//
    @GetMapping
    public List<String> itemGroupNamesGet() {
        return service.itemGroupNamesGetting();
    }


    //--for select reorder items report----------------------------------------------------------------------------//
    @GetMapping("/reports/reorder")
    public List<ReOrderResponse> reorderReportsGeneration(
            @RequestParam Location location,
            @RequestParam String groupName) {
        System.out.println("locationnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"+location);

        return service.reorderBalanceReport(location, groupName);
    }

    //--for select Item balance report--//
    @GetMapping("/reports/balance")
    public Object balanceReportsGeneration(
            @RequestParam Location location,
            @RequestParam String groupName,
            @RequestParam Boolean isGrnWise) {

        if (isGrnWise) {
            return service.grnBalanceReport(location, groupName);
        } else {
            return service.itemBalanceReport(location, groupName);
        }
    }

    //--for reorder report's pdf--//
    @GetMapping("/reports/reorder/pdf")
    public ResponseEntity<byte[]> reorderPdf(
            @RequestParam Location location,
            @RequestParam String groupName) {

        byte[] pdf = pdfService.generateReorderPdf(
                service.reorderBalanceReport(location, groupName));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=reorder.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    //--for balance report's pdf--//
    @GetMapping("/reports/balance/pdf")
    public ResponseEntity<byte[]> balancePdf(
            @RequestParam Location location,
            @RequestParam String groupName,
            @RequestParam Boolean isGrnWise) {

        byte[] pdf;

        if (isGrnWise) {
            pdf = pdfService.generateGrnPdf(
                    service.grnBalanceReport(location, groupName));
        } else {
            pdf = pdfService.generateBalancePdf(
                    service.itemBalanceReport(location, groupName));
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=balance.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}