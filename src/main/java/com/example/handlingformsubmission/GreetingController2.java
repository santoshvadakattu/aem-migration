package com.example.handlingformsubmission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Controller
public class GreetingController2 {

	@GetMapping("/greeting2")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		return "greeting";
	}

	@PostMapping("/greeting2")
	public String greetingSubmit(@RequestParam("folderPath") String folderPath, Model model) {
		// String listOfFiles = listFiles(folderPath);
		// String xmlString = "<root><name>John</name><age>30</age></root>";
		String listOfFiles = convertXmlToJson(folderPath);
		model.addAttribute("reqHTML", listOfFiles);
		return "result";
	}

	

	public static String convertXmlToJson(String filePath) {
		String totFileStr = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			// Declaring a string variable
			String st;
			while ((st = br.readLine()) != null) {
				// Print the string
				// System.out.println(st);
				totFileStr = totFileStr + st;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int PRETTY_PRINT_INDENT_FACTOR = 4;
		JSONObject xmlJSONObj = XML.toJSONObject(totFileStr);
		return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	}

	private String convertXmlToJson3(String filePath) {

		String jsonString = null;
		try {
			// Step 1: Read the XML file
			File inputFile = new File(filePath);
			// Step 2: Create XmlMapper to read XML
			XmlMapper xmlMapper = new XmlMapper();
			// Step 3: Convert XML to Java object
			MyDataObject dataObject = xmlMapper.readValue(inputFile, MyDataObject.class);
			// Step 4: Create ObjectMapper to write JSON
			ObjectMapper jsonMapper = new ObjectMapper();
			// Step 5: Convert Java object to JSON
			jsonString = jsonMapper.writeValueAsString(dataObject);
			// Step 6: Print the JSON string
			System.out.println(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;

	}

	private static String convertXmlToJson2(String filePath) {

		String jsonString = null;
		try {
			File file = new File(filePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			String xmlString = doc.toString();

			System.out.println("xmlString is 1111 ==== :: " + xmlString);
			// Create the XML mapper
			XmlMapper xmlMapper = new XmlMapper();
			System.out.println("xmlString is 2222 :: " + xmlString);
			// Parse the XML string into a JsonNode
			JsonNode jsonNode = xmlMapper.readTree(xmlString);
			// Create the JSON mapper
			ObjectMapper jsonMapper = new ObjectMapper();
			// Convert the JsonNode to JSON string
			jsonString = jsonMapper.writeValueAsString(jsonNode);
			// Print the JSON output
			System.out.println(jsonString);
		} catch (Exception e) {
			System.out.println("Exception is : " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	private String listFiles(String filePath) {
		File directoryPath = new File(filePath);
		List<File> componentsFolders = findDirectoriesWithSameName("components", directoryPath);
		List<File> templatesFolders = findDirectoriesWithSameName("templates", directoryPath);
		String reqList = ""; // "</br>";
		reqList = reqList + "<h2> The components are </h2> ";
		reqList = reqList + getRequiredList(componentsFolders, true);
		reqList = reqList + "<h2> The templates are </h2>";
		reqList = reqList + getRequiredList(templatesFolders, false);
		return reqList;
	}

	private String getRequiredList(List<File> componentsFolders, boolean isComponent) {
		File reqFolder = null;
		for (Iterator iterator = componentsFolders.iterator(); iterator.hasNext();) {
			reqFolder = (File) iterator.next();
			System.out.println("req Folder is : " + reqFolder.getPath());
			if (!reqFolder.getPath().contains("target"))
				break;
		}
		String listOfFiles = "";
		// List only the directories not all the files if exists
		String contents[] = reqFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});

		for (int i = 0; i < contents.length; i++) {
			String filePathTemp = reqFolder.getAbsolutePath() + "/" + contents[i] + "/.content.xml";
			System.out.println();
			listOfFiles = listOfFiles + readTitleFromXML(filePathTemp, isComponent) + "</br>";
		}
		return listOfFiles;

	}

	private String readTitleFromXML(String filePath, boolean isComponent) {

		String itemName = null;
		try {
			File file = new File(filePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
			NodeList nodeList = null;
			if (isComponent)
				nodeList = doc.getElementsByTagName("jcr:root");
			else
				nodeList = doc.getElementsByTagName("jcr:content");
			System.out.println("nodeList.getLength() is : " + nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element elementAttribute = null;
				elementAttribute = (Element) nodeList.item(i);
				itemName = elementAttribute.getAttribute("jcr:title");
				System.out.println(elementAttribute.getAttribute("jcr:title"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemName;

	}

	public static List<File> findDirectoriesWithSameName(String name, File root) {
		List<File> result = new ArrayList<>();
		for (File file : root.listFiles()) {
			if (file.isDirectory()) {
				if (file.getName().equals(name)) {
					result.add(file);
				}
				result.addAll(findDirectoriesWithSameName(name, file));
			}
		}
		return result;
	}
}