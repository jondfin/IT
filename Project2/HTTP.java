/**
 * @author Jon Cobi Delfin
 */

import java.io.*;
public class HTTP {
	
	public File file = null;
	public FileInputStream fileInputStream = null; //send response to client
	public boolean fileExists = false;
	private final String CRLF = "\r\n";
	
	public String parse(String message) {
		String fileName = "";
		fileName = message.split("[\\s\\/]")[2];
		try {
			System.out.println("Client requested: " + fileName);
			file = new File(HTTP.class.getResource(fileName).getFile());
			fileInputStream = new FileInputStream(file);
		}catch(FileNotFoundException fe) {
			System.out.println("FileNotFoundExceptoin: Could not find " + fileName);
			return null;
		}catch(NullPointerException n) {
			System.out.println("NullPointerException: Could not find " + fileName);
			return null;
		}catch(Exception e) {
			System.out.println("Error: " + e);
			return null;
		}
		fileExists = true;
		return fileName;
	}
	
	public String compose(int statusCode, String fileName) {
		String phrase = "";
		String message = "";
		if(statusCode == 200) phrase = "OK";
		else if(statusCode == 404) phrase = "Not Found";
		message = "HTTP/1.0 " + statusCode + " " + phrase + CRLF + "Content-type: " + getContentType(fileName) + CRLF;
		return message;
	}
	
	private String getContentType(String fileName) {
		if(fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.equals("")) {
			return "text/html" + CRLF;
		}else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
			return "image/jpeg" + CRLF;
		}else if(fileName.endsWith(".bmp")) {
			return "image/bmp" + CRLF;
		}else if(fileName.endsWith(".gif")) {
			return "image/gif" + CRLF;
		}
		return "application/octet-stream" + CRLF;
	}
	
}
