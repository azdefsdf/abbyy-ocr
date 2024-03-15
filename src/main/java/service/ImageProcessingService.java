package service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.abbyy.FCEngine.Engine;
import com.abbyy.FCEngine.ExportDestinationTypeEnum;
import com.abbyy.FCEngine.FileExportFormatEnum;
import com.abbyy.FCEngine.IBatch;
import com.abbyy.FCEngine.IEngine;
import com.abbyy.FCEngine.IEngineLoader;
import com.abbyy.FCEngine.IExportParams;
import com.abbyy.FCEngine.IFileExportParams;
import com.abbyy.FCEngine.IProject;
import com.abbyy.FCEngine.MessagesLanguageEnum;
import com.abbyy.FCEngine.RecognitionModeEnum;
import config.SamplesConfig;
import exception.ImageProcessingException;
import com.aspose.words.*;




@Service
public class ImageProcessingService {

	public String processImagesAndGetJsonData(MultipartFile[] images, String projectPath)
			throws ImageProcessingException {
		IEngine engine = null;
		String jsonData = null;
		String jsonData1 = null;


		try {

			// Getting engine loader
			trace("Getting engine loader...");
			IEngineLoader engineLoader = Engine.GetEngineLoader(SamplesConfig.GetDllPath());

			// Setting parameters to load the engine
			trace("Setting parameters to load the engine...");
			engineLoader.setCustomerProjectId(SamplesConfig.GetCustomerProjectId());
			engineLoader.setLicensePath(SamplesConfig.GetLicensePath());
			engineLoader.setLicensePassword(SamplesConfig.GetLicensePassword());

			// Load the engine
			trace("Load the engine...");
			engine = engineLoader.GetEngine();
			// Set English language for any messages (e.g., in logs)
			engine.setMessagesLanguage(MessagesLanguageEnum.ML_English);

			// Opening the project
			String samplesFolder = SamplesConfig.GetSamplesFolder();
			trace("Opening the project...");
			String projectFilePath = combinePaths(samplesFolder, projectPath);
			trace("Opening the project: " + projectFilePath);
			IProject project = engine.OpenProject(projectFilePath);

			// Remove any batches that could remain from previous runs of FC SDK samples
			project.getBatches().DeleteAll();

			// Creating a batch
			trace("Creating a batch...");
			IBatch batch = project.getBatches().AddNew();
			// Opening the batch
			trace("Opening the batch...");
			batch.Open();

					
			
			try {
				// Adding images to the batch
				trace("Adding images...");
				for (MultipartFile image : images) {
					File imageFile = convertMultipartFileToFile(image);
					System.out.println(imageFile.getPath());

					 String namefile = imageFile.getName() ;
					 System.out.println(namefile);
					
					batch.AddImage(imageFile.getPath());

					// Path to the input image file
			        File image_File = new File(imageFile.getPath());

			        // Extract file name and parent folder
			        String fileName = image_File.getName();
			        String parentFolderPath = image_File.getParent();

			        // Create a new folder for PDF conversion
			        String pdfConvertFolderPath = parentFolderPath + File.separator + "PdfConvert";
			        File pdfConvertFolder = new File(pdfConvertFolderPath);
			        if (!pdfConvertFolder.exists()) {
			            pdfConvertFolder.mkdirs(); // Create the folder if it doesn't exist
			        }

			        // Create a new PDF document
			        Document doc = new Document();
			        DocumentBuilder builder = new DocumentBuilder(doc);

			        // Insert the image into the document
			        builder.insertImage(image_File.getPath());

			        try {
			            // Save the output PDF file inside the "PdfConvert" folder
			            String outputFileName = fileName.replaceFirst("[.][^.]+$", "") + ".pdf";
			            String outputPath = pdfConvertFolderPath + File.separator + outputFileName;
			            doc.save(outputPath);

			            System.out.println("PDF created successfully at: " + outputPath);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }

			    
					
				  

				

				// Recognizing the images
				trace("Recognizing the images...");
				batch.Recognize(null, RecognitionModeEnum.RM_ReApplyDocumentDefinitions, null);

			
				trace("Exporting... format Json");
				IExportParams exportParamsJSON = engine.CreateExportParams(ExportDestinationTypeEnum.EDT_File);
				IFileExportParams fileExportParamsJSON = exportParamsJSON.getFileExportParams();
				fileExportParamsJSON
						.setRootPath(combinePaths(samplesFolder, "SampleProjects\\Hello\\SampleProject\\Export"));
				fileExportParamsJSON.setFileFormat(FileExportFormatEnum.FEF_JSON); // Adjust as needed
				project.Export(null, exportParamsJSON);
				
				 
				jsonData1 = renameFile();			
				jsonData = readFileAsStringAndDelete(jsonData1);

				trace("Export done successfully.");
			} catch (Exception ex) {
				trace("Export error :." + ex.getMessage());
			} finally {
				// Closing the batch and the project
				trace("Closing the batch and the project...");
				batch.Close();
				project.Close();
			}
		} catch (Exception ex) {
			throw new ImageProcessingException("Error processing images", ex);
		} finally {
			// Deinitializing Engine
			trace("Deinitializing Engine...");
			if (engine != null) {
				Engine.Unload();
			}
		}

		return jsonData;
	}

	private static void trace(String txt) {
		System.out.println(txt);
	}

	private static String combinePaths(String var0, String var1) {
		File var2 = new File(var0);
		File var3 = new File(var2, var1);
		return var3.getPath();
	}

	public static File convertMultipartFileToFile(MultipartFile multipartFile) throws Exception {
		String originalFilename = multipartFile.getOriginalFilename();
		String filePath = "C:\\ProgramData\\ABBYY\\FCSDK\\12\\FlexiCapture SDK\\Samples\\SampleProjects\\Hello\\SampleProject\\saved\\"
				+ originalFilename;
		File file = new File(filePath);
		multipartFile.transferTo(file);
		return file;
	}

	
	private static String renameFile() throws Exception {
		
	    String samplesFolder = SamplesConfig.GetSamplesFolder();
		 String customFileName = UUID.randomUUID().toString() + ".json";

	    // Construct the path to the exported JSON file
	    String exportedFilePath = combinePaths(samplesFolder, "SampleProjects\\Hello\\SampleProject\\Export\\Batch.json");

	    // Construct the path to the file with the custom name
	    String customFilePath = combinePaths(samplesFolder, "SampleProjects\\Hello\\SampleProject\\Export\\" + customFileName);

	    // Rename the exported JSON file to the custom name
	    File exportedFile = new File(exportedFilePath);
	    File customFile = new File(customFilePath);
	    boolean renamed = exportedFile.renameTo(customFile);
	    if (renamed) {
	        System.out.println("Renamed the file from 'Batch.json' to '" + customFileName + "'");
	        return customFilePath; // Return the new file path with the custom name
	    } else {
	        System.out.println("Failed to rename the file");
	        return null; // Return null if renaming failed
	    }
	}


	
	private static String readFileAsStringAndDelete(String filePath) throws Exception {

		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
		String fileContent = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);

		// Delete the file after reading
	

		return fileContent;
	}
	
 

}