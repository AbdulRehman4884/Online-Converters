package com.example.demo.Controllers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.FileUploadModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class ImageController {

    @GetMapping("/TiffConverter")
    public String showConversionForm(Model model) {
        model.addAttribute("fileUploadModel", new FileUploadModel());
        return "convert";
    }

    @PostMapping("/tifToPdf")
    public ResponseEntity<byte[]> convertTiffToPdf(@ModelAttribute("fileUploadModel") FileUploadModel fileUploadModel, Model model) {
        try {
            MultipartFile tiffFile = fileUploadModel.getTiffFile();
            if (tiffFile.isEmpty()) {
                // Handle empty file
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Convert TIFF to BufferedImage using ImageIO
            BufferedImage bufferedImage = ImageIO.read(tiffFile.getInputStream());

            // Create a PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Convert BufferedImage to PDF
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                ImageIO.write(bufferedImage, "png", baos);
                contentStream.drawImage(org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromByteArray(document, baos.toByteArray(), "Image"), 0, 0);
            }

            // Save the PDF file in memory
            ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
            document.save(pdfOutput);
            document.close();

            // Prepare the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "output.pdf");

            // Return ResponseEntity with the PDF content and headers
            return new ResponseEntity<>(pdfOutput.toByteArray(), headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            // You might want to return an error response here if needed
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
