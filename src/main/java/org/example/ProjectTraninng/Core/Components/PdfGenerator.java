package org.example.ProjectTraninng.Core.Components;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.Data;
import org.example.ProjectTraninng.Common.Entities.*;
import org.example.ProjectTraninng.Common.Enums.Role;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Repsitories.FileDataRepository;
import org.example.ProjectTraninng.Core.Repsitories.TreatmentRepository;
import org.example.ProjectTraninng.Core.Servecies.SalaryPaymentService;
import org.example.ProjectTraninng.Core.Servecies.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
@Component
public class PdfGenerator {

    @Autowired
    private SalaryPaymentService salaryPaymentService;

    @Autowired
    private ChartGenerator chartGenerator;

    @Autowired
    private FileDataRepository fileDataRepository;

    @Autowired
            private TreatmentRepository treatmentRepository;

    String pdfFiles = "C:\\Users\\moham\\Desktop\\ProjectFinalTraninng\\projecttraning\\src\\main\\resources\\PDF\\";
    String chartFiles = "C:\\Users\\moham\\Desktop\\ProjectFinalTraninng\\projecttraning\\src\\main\\resources\\Chart\\";


    public GeneralResponse generatePdfForRoles(List<Role> roles, String headerBgColor, String headerTextColor, String tableRowColor1, String tableRowColor2) throws Exception {
        List<SalaryPayment> payments = salaryPaymentService.getSalaryPaymentsByRoles(roles);
        Map<String, Double> totalAmountByMonth = salaryPaymentService.getTotalAmountByMonth(payments);
        double totalAmount = salaryPaymentService.getTotalAmount(payments);
        Long time = System.currentTimeMillis();

        // Define file names
        String pdfFileName = "SalaryPaymentsReport" + time + ".pdf";
        String chartFileName = "chart" + time + ".png";

        // Firebase storage paths
        String firebasePdfPath = "medical/Report/" + pdfFileName;
        String firebaseChartPath = "medical/Chart/" + chartFileName;

        // Generate chart image locally
        String chartFilePath = chartFiles + chartFileName;
        chartGenerator.generateChart(totalAmountByMonth, chartFilePath);

        // Generate PDF
        Document document = new Document(PageSize.A4, 36, 36, 100, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new HeaderFooterPageEvent());

        document.open();

        Paragraph title = new Paragraph("Salary Payments Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph rolesParagraph = new Paragraph("Roles: " + roles);
        rolesParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(rolesParagraph);

        Paragraph totalAmountParagraph = new Paragraph("Total Amount: $" + totalAmount);
        totalAmountParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(totalAmountParagraph);

        List<User> users = salaryPaymentService.getUsersByRoles(roles);
        document.add(new Paragraph("Users Information:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(new Paragraph(" "));

        PdfPTable userTable = new PdfPTable(6);
        userTable.setWidthPercentage(100);
        userTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        userTable.setSpacingBefore(10);
        userTable.setSpacingAfter(10);

        // Add table headers
        addColoredHeaderCell(userTable, "ID", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(userTable, "First Name", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(userTable, "Last Name", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(userTable, "Email", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(userTable, "Role", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(userTable, "Total Salary", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));

        int rowIndex = 0;
        for (User user : users) {
            addColoredRow(userTable, user, rowIndex++, hexToBaseColor(tableRowColor1), hexToBaseColor(tableRowColor2));
        }
        document.add(userTable);

        // Total amount by month table
        document.add(new Paragraph("Total Amount by Month:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(new Paragraph(" "));

        PdfPTable monthTable = new PdfPTable(2);
        monthTable.setWidthPercentage(80);
        monthTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addColoredHeaderCell(monthTable, "Month", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(monthTable, "Total Amount", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));

        rowIndex = 0;
        for (Map.Entry<String, Double> entry : totalAmountByMonth.entrySet()) {
            addColoredRow(monthTable, entry, rowIndex++, hexToBaseColor(tableRowColor1), hexToBaseColor(tableRowColor2));
        }
        document.add(monthTable);

        // Add chart image
        Image chartImage = Image.getInstance(chartFilePath);
        chartImage.setAlignment(Image.ALIGN_CENTER);
        chartImage.scaleToFit(PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
        document.add(chartImage);

        document.close();

        // Upload files to Firebase
        Bucket bucket = StorageClient.getInstance().bucket();

        // Upload chart
        byte[] chartData = Files.readAllBytes(Paths.get(chartFilePath));
        Blob chartBlob = bucket.create(firebaseChartPath, chartData, "image/png");

        // Upload PDF
        byte[] pdfData = baos.toByteArray();
        Blob pdfBlob = bucket.create(firebasePdfPath, pdfData, "application/pdf");

        // Save file data to the database
        fileDataRepository.save(FileData.builder()
                .name(pdfFileName)
                .type("application/pdf")
                .filePath(firebasePdfPath)
                .build());

        fileDataRepository.save(FileData.builder()
                .name(chartFileName)
                .type("image/png")
                .filePath(firebaseChartPath)
                .build());

        // Generate download URLs
        String baseUrl = "https://firebasestorage.googleapis.com/v0/b/graduationproject-df4b7.appspot.com/o/";
        String pdfDownloadUrl = baseUrl + firebasePdfPath.replace("/", "%2F") + "?alt=media";
        String chartDownloadUrl = baseUrl + firebaseChartPath.replace("/", "%2F") + "?alt=media";

        return GeneralResponse.builder()
                .message(pdfDownloadUrl)
                .build();
    }


    private String getTotalSalary(User user) {
        List<SalaryPayment> payments = user.getSalaryId();
        double totalSalary = payments.stream()
                .mapToDouble(SalaryPayment::getAmount)
                .sum();
        return "$" + totalSalary;
    }

    private void addColoredHeaderCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, textColor);
        Phrase phrase = new Phrase(text, font);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(1.4f);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }

    private void addColoredRow(PdfPTable table, User user, int rowIndex, BaseColor color1, BaseColor color2) {
        BaseColor color = (rowIndex % 2 == 0) ? color1 : color2;
        addCell(table, user.getId().toString(), color);
        addCell(table, user.getFirstName(), color);
        addCell(table, user.getLastName(), color);
        addCell(table, user.getEmail(), color);
        addCell(table, user.getRole().toString(), color);
        addCell(table, getTotalSalary(user), color);
    }

    private void addColoredRow(PdfPTable table, Map.Entry<String, Double> entry, int rowIndex, BaseColor color1, BaseColor color2) {
        BaseColor color = (rowIndex % 2 == 0) ? color1 : color2;
        addCell(table, entry.getKey(), color);
        addCell(table, "$" + entry.getValue(), color);
    }

    private void addCell(PdfPTable table, String text, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(1.4f);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }

    private BaseColor hexToBaseColor(String hex) {
        return new BaseColor(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }
    class HeaderFooterPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable header = new PdfPTable(2);
            try {
                header.setWidths(new int[]{1, 3});
                header.setTotalWidth(527);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(50);
                header.getDefaultCell().setBorder(Rectangle.BOTTOM);
                header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

                LocalDate localDate = LocalDate.now();
                String formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                PdfPCell dateCell = new PdfPCell(new Phrase("Date: " + formattedDate, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                dateCell.setBorder(Rectangle.BOTTOM);
                dateCell.setBorderColor(BaseColor.LIGHT_GRAY);
                dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                header.addCell(dateCell);

                PdfPCell titleCell = new PdfPCell(new Phrase("Salary Payments Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                titleCell.setBorder(Rectangle.BOTTOM);
                titleCell.setBorderColor(BaseColor.LIGHT_GRAY);
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.addCell(titleCell);

                header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
    }

    class HeaderFooterPageProftsEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable header = new PdfPTable(2);
            try {
                header.setWidths(new int[]{1, 3});
                header.setTotalWidth(527);
                header.setLockedWidth(true);
                header.getDefaultCell().setFixedHeight(50);
                header.getDefaultCell().setBorder(Rectangle.BOTTOM);
                header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

                LocalDate localDate = LocalDate.now();
                String formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                PdfPCell dateCell = new PdfPCell(new Phrase("Date: " + formattedDate, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                dateCell.setBorder(Rectangle.BOTTOM);
                dateCell.setBorderColor(BaseColor.LIGHT_GRAY);
                dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                header.addCell(dateCell);

                PdfPCell titleCell = new PdfPCell(new Phrase("Treatments Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                titleCell.setBorder(Rectangle.BOTTOM);
                titleCell.setBorderColor(BaseColor.LIGHT_GRAY);
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.addCell(titleCell);

                header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
    }

    public GeneralResponse generatePdfForTreatments(String headerBgColor, String headerTextColor, String tableRowColor1, String tableRowColor2) throws Exception {
        List<Treatment> treatments = treatmentRepository.getAllTreatments();
        Long time = System.currentTimeMillis();
        String fileName = "Treatments_Report_" + time + ".pdf";
        String pdfFilePath = pdfFiles + fileName;

        // Step 1: Generate PDF locally
        Document document = new Document(PageSize.A4, 36, 36, 100, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        writer.setPageEvent(new HeaderFooterPageProftsEvent());

        document.open();

        Paragraph title = new Paragraph("Treatments Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        PdfPTable treatmentTable = new PdfPTable(5);
        treatmentTable.setWidthPercentage(100);
        treatmentTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        treatmentTable.setSpacingBefore(10);
        treatmentTable.setSpacingAfter(10);

        // Add table headers
        addColoredHeaderCell(treatmentTable, "ID", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(treatmentTable, "Treatment Date", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(treatmentTable, "Disease Description", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(treatmentTable, "Note", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));
        addColoredHeaderCell(treatmentTable, "Total Price", hexToBaseColor(headerBgColor), hexToBaseColor(headerTextColor));

        int rowIndex = 0;
        for (Treatment treatment : treatments) {
            double totalCost = treatment.getPrice();
            for (PatientMedicine pm : treatment.getPatientMedicines()) {
                totalCost += pm.getPrice() * pm.getQuantity();
            }

            addColoredRow(treatmentTable, treatment, totalCost, rowIndex++, hexToBaseColor(tableRowColor1), hexToBaseColor(tableRowColor2));
        }
        document.add(treatmentTable);

        document.add(new Paragraph(" "));
        double totalAmount = treatments.stream()
                .mapToDouble(treatment -> {
                    double totalCost = treatment.getPrice();
                    for (PatientMedicine pm : treatment.getPatientMedicines()) {
                        totalCost += pm.getPrice() * pm.getQuantity();
                    }
                    return totalCost;
                }).sum();

        Paragraph totalAmountParagraph = new Paragraph("Total Profits: $" + totalAmount, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        totalAmountParagraph.setAlignment(Element.ALIGN_LEFT);
        document.add(totalAmountParagraph);

        document.close();

        // Step 2: Upload to Firebase Storage
        String firebasePath = "medical/Report/" + fileName;
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(firebasePath, baos.toByteArray(), "application/pdf");

        // Generate a Firebase download URL
        final String baseUrl = "https://firebasestorage.googleapis.com/v0/b/graduationproject-df4b7.appspot.com/o/";
        String downloadUrl = baseUrl + "medical%2FReport%2F" + fileName + "?alt=media";

        // Step 3: Store file information in FileData repository
        fileDataRepository.save(FileData.builder()
                .name(fileName)
                .type("application/pdf")
                .filePath(downloadUrl)  // Store the Firebase URL
                .build());

        // Return success response with the download URL
        return GeneralResponse.builder()
                .message("File successfully uploaded to Firebase. Download URL: " + downloadUrl)
                .build();
    }

    private void addColoredRow(PdfPTable table, Treatment treatment, double totalCost, int rowIndex, BaseColor color1, BaseColor color2) {
        BaseColor bgColor = (rowIndex % 2 == 0) ? color1 : color2;
        addColoredCell(table, String.valueOf(treatment.getId()), bgColor);
        addColoredCell(table, treatment.getTreatmentDate().toString(), bgColor);
        addColoredCell(table, treatment.getDiseaseDescription(), bgColor);
        addColoredCell(table, treatment.getNote(), bgColor);
        addColoredCell(table, String.valueOf(totalCost), bgColor);
    }

    private void addColoredCell(PdfPTable table, String text, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }


}
