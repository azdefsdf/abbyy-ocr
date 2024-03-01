package com.abbyysdk.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import exception.ImageProcessingException;
import jakarta.annotation.Resource;
import model.Invoice;
import service.ImageProcessingService;

@JsonIgnoreProperties(ignoreUnknown = true)
@Resource
//@RestController
@CrossOrigin("http://localhost:4200")
@RestController
public class ImageProcessingController {

	private final ImageProcessingService imageProcessingService;

	public ImageProcessingController() {
		super();

		this.imageProcessingService = new ImageProcessingService();
	}

	public ImageProcessingController(ImageProcessingService imageProcessingService) {
		this.imageProcessingService = imageProcessingService;
	}

	/////

	//@GetMapping("/data")
	@PostMapping("/data")
	public ResponseEntity<String> fetchAbbyyData(@RequestParam("images") MultipartFile[] images,
			@RequestParam("projectPath") String projectPath) {
		try {

			String jsonData = imageProcessingService.processImagesAndGetJsonData(images, projectPath);
			return ResponseEntity.ok(jsonData);
		} catch (ImageProcessingException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing images: " + ex.getMessage());
		}
	}

	////
	@PostMapping("/process-images")
	public ResponseEntity<String> processImages(@RequestParam("images") MultipartFile[] images,
			@RequestParam("projectPath") String projectPath) {
		try {
			// imageProcessingService.processImages(images, projectPath);
			// imageProcessingService.readJSON(images, projectPath)
			return ResponseEntity.ok("Images processed successfully.");
		} catch (ImageProcessingException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing images: " + ex.getMessage());
		}

	}

	////
	@GetMapping("/process-images")
	public ResponseEntity<?> readJSON() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			File file = ResourceUtils.getFile("src/main/resources/Batch.json");
			Invoice obj = mapper.readValue(file, Invoice.class);
			return ResponseEntity.ok(obj);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error reading JSON data: " + e.getMessage());
		}
	}

}
