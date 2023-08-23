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
public class GreetingController {

	ArrayList<SchemaItem> schemaItems = new ArrayList<SchemaItem>();
	static JSONArray schemaArray = new JSONArray();
	static JSONObject schemaJsonObject = new JSONObject();
	static JSONObject mainSchemaJsonObject = new JSONObject();

	@GetMapping("/greeting")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		return "greeting";
	}

	@PostMapping("/greeting")
	public String greetingSubmit(@RequestParam("folderPath") String folderPath, Model model) {
		String pageHITURLResponsJSON = "";
		if (folderPath.equalsIgnoreCase("1")) {
			folderPath = "/Users/santoshvadakattu/Downloads/company/ContentStack/aem-export-samples/weretail-button/jcr_root/apps/weretail/components/content/button";
		}
		if (folderPath.equalsIgnoreCase("2")) {
			folderPath = "/Users/santoshvadakattu/Downloads/company/ContentStack/aem-export-samples/cm-pkg/jcr_root/content/service-oklahoma/us/en/test-migration/aem-content-for-migration/.content.xml";
		}

		if (folderPath.equalsIgnoreCase("3")) {
			HttpUrlConnectionWithCredentials urlConn = new HttpUrlConnectionWithCredentials();
			pageHITURLResponsJSON = urlConn.hitURL(null);
			folderPath = "/Users/santoshvadakattu/Downloads/company/ContentStack/aem-export-samples/cm-pkg/jcr_root/content/service-oklahoma/us/en/test-migration/aem-content-for-migration/.content.xml";
		}
		String listOfFiles = convertXmlToJson(folderPath);
		listOfFiles = pageHITURLResponsJSON + "</br></br></br></br>" + listOfFiles;
		//String outPutJson = createCSJson(listOfFiles);
		// createJsonFile(listOfFiles);
		model.addAttribute("reqHTML", listOfFiles);
		return "result";
	}

	// To create the required CS Json to import to content stack
	private String createCSJson(String listOfFiles) {
		// System.out.println("=====inside createCS-Json File method ::: +
		// "+listOfFiles);
		JSONObject jsonObj = new JSONObject(listOfFiles);
		String filePath = "/Users/santoshvadakattu/Downloads/company/ContentStack/cs-import-samples/cs-entry1.json";
		try (FileWriter fileWriter = new FileWriter(filePath)) {
			JSONObject jsonObj1 = (JSONObject) jsonObj.get("jcr:root");
			ObjectMapper mapper = new ObjectMapper();
			String pageTitle = ((JSONObject) jsonObj1.get("jcr:content")).get("jcr:title").toString();
			ObjectNode node = mapper.readValue(jsonObj1.toString(), ObjectNode.class);
			JsonNode rootNode = mapper.readTree(jsonObj1.toString());
			// System.out.println("rootNode is =====: "+rootNode.toPrettyString());

			JSONObject reqJson = findObjectByNameRecursive(jsonObj1, "migration_poc");
			System.out.println("required JSON is : " + reqJson.toString());
			JSONObject mainJson = prepareContentEntryJsonObject();

			mainJson.put("ccm_property_1", reqJson.get("prop1"));
			mainJson.put("ccm_property_2", reqJson.get("prop2"));
			mainJson.put("ccm_property_3", reqJson.get("prop3"));
			mainJson.put("title", pageTitle);

			String formattedData = new GsonBuilder().setPrettyPrinting().create()
					.toJson(JsonParser.parseString(mainJson.toString()));
			fileWriter.write(formattedData);
			System.out.println("JSON file created successfully!");
		} catch (IOException e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
		return null;

	}

	private static JSONObject findObjectByNameRecursive(JSONObject jsonObject, String targetName) {
		if (jsonObject.has(targetName)) {
			return jsonObject.getJSONObject(targetName);
		}

		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				JSONObject subObject = (JSONObject) value;
				JSONObject foundObject = findObjectByNameRecursive(subObject, targetName);
				if (foundObject != null) {
					return foundObject;
				}
			} else if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				for (int i = 0; i < array.length(); i++) {
					Object arrayItem = array.get(i);
					if (arrayItem instanceof JSONObject) {
						JSONObject subObject = (JSONObject) arrayItem;
						JSONObject foundObject = findObjectByNameRecursive(subObject, targetName);
						if (foundObject != null) {
							return foundObject;
						}
					}
				}
			}
		}
		return null;
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

	// Recursive function to search for a JSON node by name
	private static JsonNode findJsonNodeByName(JsonNode node, String targetName) {

		if (node.isObject()) {
			if (node.has("name") && node.get("name").asText().equals(targetName)) {
				return node;
			}
			for (JsonNode child : node) {
				JsonNode result = findJsonNodeByName(child, targetName);
				if (result != null) {
					return result;
				}
			}
		} else if (node.isArray()) {
			for (JsonNode child : node) {
				JsonNode result = findJsonNodeByName(child, targetName);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
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
			node.elements().forEachRemaining(GreetingController::iterateJsonValues);
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

	private JSONObject prepareContentEntryJsonObject() {

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("created_at", "2023-08-21T12:27:36.949Z");
		jsonObject.put("created_by", "blt2fdc74fb79172b4c");

		jsonObject.put("updated_at", "2023-08-22T07:53:54.299Z");
		jsonObject.put("updated_by", "blt2fdc74fb79172b4c");
		// jsonObject.put("title", "CCM Page 1 Migrated from AEM");
		jsonObject.put("uid", "ccm-page1-mig-from-aem");
		jsonObject.put("_version", 1);
		jsonObject.put("locale", "en-us");
		jsonObject.put("_in_progress", false);
		return jsonObject;

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
		jsonObject.put("title", "CT1 Migrated From AEM");
		jsonObject.put("uid", "ct1-mfa1");
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
		// filePath = filePath + "/_cq_dialog/content.xml";
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