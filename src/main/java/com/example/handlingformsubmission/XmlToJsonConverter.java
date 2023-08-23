package com.example.handlingformsubmission;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlToJsonConverter {
	public static void main(String[] args) {
		String xmlString = "<root><name>John</name><age>30</age></root>";
		convertXmlToJson(xmlString);

	}

	private static void convertXmlToJson(String xmlString) {
		try {
			// Create the XML mapper
			XmlMapper xmlMapper = new XmlMapper();
			// Parse the XML string into a JsonNode
			JsonNode jsonNode = xmlMapper.readTree(xmlString);
			// Create the JSON mapper
			ObjectMapper jsonMapper = new ObjectMapper();
			// Convert the JsonNode to JSON string
			String jsonString = jsonMapper.writeValueAsString(jsonNode);
			// Print the JSON output
			System.out.println(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
