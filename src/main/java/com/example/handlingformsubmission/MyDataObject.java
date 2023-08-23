package com.example.handlingformsubmission;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MyDataObject {

	// Add getter and setter methods (or use Lombok annotations)

	// For Jackson, you can use the following annotations to map the fields to XML
	// elements
	// (if the XML element names differ from the Java field names):
	// @JacksonXmlProperty(localName = "name")
	// @JacksonXmlProperty(localName = "age")
	// @JacksonXmlProperty(localName = "city")

	@JacksonXmlProperty(localName = "name") // Replace "name" with your XML element names
	private String name;
	@JacksonXmlProperty(localName = "age")
	private int age;
	@JacksonXmlProperty(localName = "city")
	private int city;
}
