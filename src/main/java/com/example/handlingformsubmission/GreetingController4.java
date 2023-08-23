package com.example.handlingformsubmission;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

@Controller
public class GreetingController4 {

	ArrayList<SchemaItem> schemaItems = new ArrayList<SchemaItem>();
	static JSONArray schemaArray = new JSONArray();
	static JSONObject schemaJsonObject = new JSONObject();
	static JSONObject mainSchemaJsonObject = new JSONObject();

	@GetMapping("/greeting4")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		return "greeting";
	}

	@PostMapping("/greeting4")
	public String greetingSubmit(@RequestParam("folderPath") String folderPath, Model model) {
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
		JSONObject jsonObject = prepareJsonObject();
		jsonObject.put("schema", schemaArray);
		String filePath = "/Users/santoshvadakattu/Downloads/company/ContentStack/cs-import-samples/output.json";
		try (FileWriter fileWriter = new FileWriter(filePath)) {
			JSONObject jsonObj1 = (JSONObject) jsonObj.get("jcr:root");
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = mapper.readValue(jsonObj1.toString(), ObjectNode.class);
			JsonNode rootNode = mapper.readTree(jsonObj1.toString());
			iterateJsonValues(rootNode);
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

	public static void iterateJsonValues(JsonNode node) {
		if (node.isValueNode()) {
			System.out.println("Value is :::: " + node.asText());
		} else if (node.isObject()) {
			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::");
			System.out.println("Its a json object ::: ");
			if (node.has("name")) {
				System.out.println("This could be a dialog item for schema");
				createSchemaItem(node);
			}
			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::");
			node.fields().forEachRemaining(entry -> {
				System.out.println("Key: " + entry.getKey());
				iterateJsonValues(entry.getValue());
			});
		} else if (node.isArray()) {
			System.out.println("Its a JSON ARRAY");
			node.elements().forEachRemaining(GreetingController4::iterateJsonValues);
		}
	}

	private static void createSchemaItem(JsonNode node) {
		JSONObject schemaJsonObject = new JSONObject();
		schemaJsonObject.put("mandatory", false);
		schemaJsonObject.put("unique", false);
		schemaJsonObject.put("multiple", false);
		schemaJsonObject.put("non_localizable", false);
		schemaJsonObject.put("mandatory", false);

		node.fields().forEachRemaining(entry -> {
			System.out.println("Key: " + entry.getKey());
			if (entry.getKey().toString().equalsIgnoreCase("fieldLabel")) {
				schemaJsonObject.put("display_name", entry.getValue().asText());
			} else if (entry.getKey().toString().equalsIgnoreCase("name")) {
				schemaJsonObject.put("uid", entry.getValue().asText().replace("./", "").toLowerCase().concat("_1"));
			} else if (entry.getKey().toString().equalsIgnoreCase("sling:resourceType")) {
				String resourceTypeVal = entry.getValue().asText(); // entry.getValue().toString();
				System.out.println("inside the resource Type validation:::::::::::::::entry.getValue() is :::: "
						+ resourceTypeVal);
				if (resourceTypeVal.equalsIgnoreCase("granite/ui/components/coral/foundation/form/textfield")) {
					System.out.println("setting the value data_type as text");
					schemaJsonObject.put("data_type", "text");
				} else if (resourceTypeVal.equalsIgnoreCase("granite/ui/components/coral/foundation/form/pathfield")) {
					schemaJsonObject.put("data_type", "link");
				}
				System.out.println("completeing here..");

			}

		});
		schemaArray.put(schemaJsonObject);
		mainSchemaJsonObject.put("schema", schemaJsonObject);

	}

	private JSONObject prepareJsonObject() {
		JSONObject jsonObject = new JSONObject();
		JSONObject emptyJsonObject = new JSONObject();
		JSONArray emptyJsonArray = new JSONArray();

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

}