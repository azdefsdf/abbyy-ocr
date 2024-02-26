package service;

import java.io.File;
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

@Service
public class ImageProcessingService {


     
    public void processImages(MultipartFile[] images, String projectPath) throws ImageProcessingException {
        IEngine engine = null;
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

                    batch.AddImage(imageFile.getPath());
                }

                // Recognizing the images
                trace("Recognizing the images...");
                batch.Recognize(null, RecognitionModeEnum.RM_ReApplyDocumentDefinitions, null);

                
                
                trace("Exporting... format Json");
                // Exporting to JSON format
                IExportParams exportParamsJSON = engine.CreateExportParams(ExportDestinationTypeEnum.EDT_File);
                IFileExportParams fileExportParamsJSON = exportParamsJSON.getFileExportParams();
                fileExportParamsJSON.setRootPath(combinePaths(samplesFolder, "SampleProjects\\Hello\\SampleProject\\Export"));
                fileExportParamsJSON.setFileFormat(FileExportFormatEnum.FEF_JSON); // Adjust as needed
                project.Export(null, exportParamsJSON);
                
                
          
                // Exporting to XLS format
                trace("Exporting...");
                IExportParams exportParamsXLS = engine.CreateExportParams(ExportDestinationTypeEnum.EDT_File);
                IFileExportParams fileExportParamsXLS = exportParamsXLS.getFileExportParams();
                fileExportParamsXLS.setRootPath(combinePaths(samplesFolder, "SampleProjects\\Hello\\SampleProject\\Export"));
                fileExportParamsXLS.setFileFormat(FileExportFormatEnum.FEF_XLS);
                project.Export(null, exportParamsXLS);

           

                trace("Export done successfully.");
            } catch (Exception ex) {
            	 trace("Export error :."+ ex.getMessage());
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
    }


    private static void trace(String txt) {
        System.out.println(txt);
    }

    private static String combinePaths(String var0, String var1) {
        File var2 = new File(var0);
        File var3 = new File(var2, var1);
        return var3.getPath();
    }


    private static File convertMultipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFilename = multipartFile.getOriginalFilename();
        String filePath = "C:\\ProgramData\\ABBYY\\FCSDK\\12\\FlexiCapture SDK\\Samples\\SampleProjects\\Hello\\SampleProject\\saved\\" + originalFilename; // Provide the full path here
        File file = new File(filePath);
        multipartFile.transferTo(file);
        return file;
    }
}
