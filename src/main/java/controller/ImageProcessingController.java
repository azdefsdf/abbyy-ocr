package controller;

import exception.ImageProcessingException;
import service.ImageProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/process-images")
    public ResponseEntity<String> processImages(@RequestParam("images") MultipartFile[] images,
                                                @RequestParam("projectPath") String projectPath) {
        try {
            imageProcessingService.processImages(images, projectPath);
            
       
            
            return ResponseEntity.ok("Images processed successfully.");
        } catch (ImageProcessingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error processing images: " + ex.getMessage());
        }
    }
}
