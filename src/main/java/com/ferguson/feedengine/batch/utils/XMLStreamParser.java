package com.ferguson.feedengine.batch.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * TODO define POJO and replace map as POJO
 * 
 * @author samli
 *
 */
@Service
public class XMLStreamParser {

	public static final String ELEMENT_NAME = "_elementName_";
	public static final String VALUE = "_value_";

//	@Value("${parser.xml.catalog.filepath}")
//	public String filePath = "/Lucidworks/156032797-156032811.xml";

	private static List<String> atrributedStartElements;

	private static List<String> textOnlyStartElements;

	private static List<String> atrributedAndTextStartElements;

	private static List<String> normalEndElements;

	private static List<String> listEndElements;

	private static List<String> rootElements;

	public static Stack<Map> parseContext = new Stack<>();

	public static Map parse(XMLEventReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();
			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();
				String elementName = startElement.getName().getLocalPart();
				if (atrributedStartElements.contains(elementName)) {
					parseElementWithAttribute(startElement, elementName);
				} else if (textOnlyStartElements.contains(elementName)) {
					nextEvent = parseElementWithTextValueOnly(reader, elementName);
				} else if (atrributedAndTextStartElements.contains(elementName)) {
					nextEvent = parseElementWithAttributeAndText(reader, startElement, elementName);
				}
			}

			if (nextEvent.isEndElement()) {
				boolean execute = false;
				EndElement endElement = nextEvent.asEndElement();
				String elementName = endElement.getName().getLocalPart();
				if (normalEndElements.contains(elementName)) {
					execute = maintainNormalEndElement(elementName);
				}
				if (listEndElements.contains(elementName)) {
					execute = maintainListEndElement(elementName);
				}
				if (!execute && rootElements.contains(elementName)) {
					Map entity = null;
					entity = parseContext.pop();
					if (parseContext.isEmpty()) {
						// System.out.println(JSONObject.toJSONString(entity));
						return entity;
					} else {
						parseContext.push(entity);
					}
				}
			}

		}
		return null;
	}

	static {
		String[] elementArray = new String[] {
				// Attribute elements
				"Attribute", "Validation", "UserTypeLink", "AttributeGroupLink", "ListOfValueLink", "LinkType",
				"MetaData", "MultiValue",
				// Classification elements
				"Classification", "AttributeLink",
				// Asset element
				"Asset", "Values", "ClassificationReference", "AssetContent", "AssetContentSpecification",
				// Product element
				"Product", "AssetCrossReference", "CurrentTasks", "Task" };
		atrributedStartElements = Arrays.asList(elementArray);

		elementArray = new String[] { "Name" };
		textOnlyStartElements = Arrays.asList(elementArray);

		elementArray = new String[] { "Value", "AssetPushLocation" };
		atrributedAndTextStartElements = Arrays.asList(elementArray);

		elementArray = new String[] {
				// Attribute elements
				"Validation", "ListOfValueLink", "LinkType", "MetaData",
				// Classification elements
				// Asset element
				"Values", "AssetContent", "AssetContentSpecification",
				// Product element
				"Product", "AssetCrossReference", "CurrentTasks" };
		normalEndElements = Arrays.asList(elementArray);

		elementArray = new String[] {
				// Attribute elements
				"UserTypeLink", "AttributeGroupLink", "MultiValue", "Value",
				// Classification elements
				"Classification", "AttributeLink",
				// Asset elments
				"AssetPushLocation", "ClassificationReference", "Task" };
		listEndElements = Arrays.asList(elementArray);

		elementArray = new String[] { "Attribute", "Classification", "Asset", "Product" };
		rootElements = Arrays.asList(elementArray);
	}

	private static XMLEvent parseElementWithTextValueOnly(XMLEventReader reader, String elementName)
			throws XMLStreamException {
		// <Name>Default Sku</Name>
		Map entity = parseContext.peek();
		XMLEvent nextEvent = reader.nextEvent();
		if (!nextEvent.isEndElement()) {
			entity.put(elementName, nextEvent.asCharacters().getData());
		}
		return nextEvent;
	}

	private static void parseElementWithAttribute(StartElement startElement, String elementName) {
		// <Validation BaseType="text" MinValue="" MaxValue="" MaxLength="100"
		// InputMask=""/>
		Map entity;
		entity = new HashMap();
		parseContext.push(entity);
		entity.put(ELEMENT_NAME, elementName);
		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			entity.put(attribute.getName().getLocalPart(), attribute.getValue());
		}
	}

	private static XMLEvent parseElementWithAttributeAndText(XMLEventReader reader, StartElement startElement,
			String elementName) throws XMLStreamException {
		// <Validation BaseType="text" MinValue="" MaxValue="" MaxLength="100"
		// InputMask=""/>
		Map entity = new HashMap();
		parseContext.push(entity);
		entity.put(ELEMENT_NAME, elementName);
		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			entity.put(attribute.getName().getLocalPart(), attribute.getValue());
		}
		XMLEvent nextEvent = reader.nextEvent();
		if (!nextEvent.isEndElement()) {
			entity.put(VALUE, nextEvent.asCharacters().getData());
		}
		return nextEvent;
	}

	private static boolean maintainNormalEndElement(String elementName) {
		Map currentElement = null;
		Map parentElement = null;
		currentElement = parseContext.pop();
		if (parseContext.isEmpty()) {
			parseContext.push(currentElement);
			return false;
		}
		parentElement = parseContext.peek();
		parentElement.put(elementName, currentElement);
		return true;
	}

	private static boolean maintainListEndElement(String subListName) {
		Map currentElement = null;
		Map parentElement = null;
		currentElement = parseContext.pop();
		if (parseContext.isEmpty()) {
			parseContext.push(currentElement);
			return false;
		}
		parentElement = parseContext.peek();
		List usbList = null;
		String listName = subListName + "s";
		if (null == parentElement.get(listName)) {
			usbList = new ArrayList();
			parentElement.put(listName, usbList);
		}
		usbList = (List) parentElement.get(listName);
		usbList.add(currentElement);
		return true;
	}

}
