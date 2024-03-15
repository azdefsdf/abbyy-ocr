package com.abbyysdk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import exception.ImageProcessingException;
import jakarta.annotation.Resource;
import service.ImageProcessingService;

@JsonIgnoreProperties(ignoreUnknown = true)
@Resource
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

	
}
