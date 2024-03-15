package dto;


//Custom response object to hold JSON data and PDF file bytes
public class ResponseData {
 private String jsonData;
 private byte[] pdfBytes;
 private String errorMessage;

 // Constructors

 public ResponseData(String jsonData, byte[] pdfBytes) {
     this.jsonData = jsonData;
     this.pdfBytes = pdfBytes;
 }

 public ResponseData(String jsonData, byte[] pdfBytes, String errorMessage) {
     this.jsonData = jsonData;
     this.pdfBytes = pdfBytes;
     this.errorMessage = errorMessage;
 }

public String getJsonData() {
	return jsonData;
}

public void setJsonData(String jsonData) {
	this.jsonData = jsonData;
}

public byte[] getPdfBytes() {
	return pdfBytes;
}

public void setPdfBytes(byte[] pdfBytes) {
	this.pdfBytes = pdfBytes;
}

public String getErrorMessage() {
	return errorMessage;
}

public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
}
 
 
 
 
 
}
 // Getters and setters