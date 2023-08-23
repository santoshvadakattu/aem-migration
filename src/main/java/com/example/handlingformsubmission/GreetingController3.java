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

import org.json.JSONArray;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

// This is the latest copy before updating which contains almost all the previous code which had been used for other purposes as well

@Controller
public class GreetingController3 {

	ArrayList<SchemaItem> schemaItems = new ArrayList<SchemaItem>();

	@GetMapping("/greeting3")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		return "greeting";
	}

	@PostMapping("/greeting3")
	public String greetingSubmit(@RequestParam("folderPath") String folderPath, Model model) {
		// uncomment this for the actual listing of the components templates etc
		// String listOfFiles = listFiles(folderPath);
		if (folderPath.equalsIgnoreCase("1")) {
			System.out.println("started here!!");
			folderPath = "/Users/santoshvadakattu/Downloads/company/ContentStack/aem-export-samples/weretail-button/jcr_root/apps/weretail/components/content/button";
		}
		String listOfFiles = convertXmlToJson(folderPath);
		createJsonFile(listOfFiles);
		model.addAttribute("reqHTML", listOfFiles);
		return "result";
	}

	private void createJsonFile(String listOfFiles) {

		System.out.println("inside createJson File method");
		JSONObject jsonObj = new JSONObject(listOfFiles);
		// Create a JSON object
		JSONObject jsonObject = prepareJsonObject();
		// jsonObject.put("age", 30);
		JSONArray jsonArray = new JSONArray();
		// Define the file path
		String filePath = "/Users/santoshvadakattu/Downloads/company/ContentStack/cs-import-samples/output.json";
		// Write JSON object to a file
		try (FileWriter fileWriter = new FileWriter(filePath)) {
			JSONObject jsonObj1 = (JSONObject) jsonObj.get("jcr:root");
			JSONObject schemaJson = getRequiredSchema(jsonObj1);
			// System.out.println("the parsed json is : \n" + jsonObj1.toString());
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = mapper.readValue(jsonObj1.toString(), ObjectNode.class);
			JsonNode rootNode = mapper.readTree(jsonObj1.toString());

			iterateJsonValues(rootNode);
			// System.out.println("node is : "+node.toPrettyString());
			if (node.has("name")) {
				System.out.println("NAME: " + node.get("name"));
			}

			String formattedData = new GsonBuilder().setPrettyPrinting().create()
					.toJson(JsonParser.parseString(jsonObject.toString()));

			fileWriter.write(formattedData);
			System.out.println("JSON file created successfully!");
		} catch (IOException e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	private JSONObject getRequiredSchema(JSONObject jsonObj1) {
		JSONObject itemsJson = jsonObj1.getJSONObject("content").getJSONObject("items").getJSONObject("column")
				.getJSONObject("items");

		System.out.println("1111 : " + itemsJson.getJSONObject("linkTo").getString("sling:resourceType"));

		return null;
	}

	public static void iterateJsonValues(JsonNode node) {
		if (node.isValueNode()) {
			System.out.println("Value is :::: " + node.asText());
		} else if (node.isObject()) {
			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::");
			System.out.println("Its a json object ::: " + node.asText());
			if (node.has("name")) {
				System.out.println("This could be a dialog item for schema");
			}
			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::");
			node.fields().forEachRemaining(entry -> {
				System.out.println("Key: " + entry.getKey());
				iterateJsonValues(entry.getValue());
			});
		} else if (node.isArray()) {
			System.out.println("Its a JSON ARRAY");
			node.elements().forEachRemaining(GreetingController3::iterateJsonValues);
		}
	}

	private JSONObject prepareJsonObject() {
		JSONObject jsonObject = new JSONObject();
		JSONObject emptyJsonObject = new JSONObject();
		JSONArray emptyJsonArray = new JSONArray();
		jsonObject.put("created_at", "2023-08-06T15:49:16.881Z");
		jsonObject.put("updated_at", "2023-08-06T15:51:34.887Z");
		jsonObject.put("title", "tbu1");
		jsonObject.put("uid", "tbu2");
		jsonObject.put("_version", "4");
		jsonObject.put("inbuilt_class", false);

		// to be added after adding the JSON Array
		jsonObject.put("last_activity", emptyJsonObject);
		jsonObject.put("maintain_revisions", true);
		jsonObject.put("description", "This is a migrated Content Type");

		JSONObject optJson = new JSONObject();

		optJson.put("is_page", false);
		optJson.put("singleton", false);
		optJson.put("sub_title", emptyJsonArray);
		optJson.put("title", "tbu3");

		JSONObject ablilitiesJson = new JSONObject();
		ablilitiesJson.put("get_one_object", true);
		ablilitiesJson.put("get_all_objects", true);
		ablilitiesJson.put("create_object", true);
		ablilitiesJson.put("update_object", true);
		ablilitiesJson.put("delete_object", true);
		ablilitiesJson.put("delete_all_objects", true);

		jsonObject.put("options", optJson);
		jsonObject.put("abilities", ablilitiesJson);

		return jsonObject;
	}

	public static String convertXmlToJson(String filePath) {
		System.out.println("in convert xml to json");
		filePath = filePath + "/_cq_dialog/content.xml";
		String totFileStr = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String st;
			while ((st = br.readLine()) != null) {
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