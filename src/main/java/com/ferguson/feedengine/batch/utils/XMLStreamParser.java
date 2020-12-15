package com.ferguson.feedengine.batch.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.annotation.PostConstruct;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 * @author samli
 *
 */
@Component
@PropertySource(value="classpath:stibo_parser.properties") 
public class XMLStreamParser {

	public static final String ELEMENT_NAME = "_elementName_";
	public static final String VALUE = "_value_";

	@Value("#{'${start.elements.attribute}'.split(',')}")
	private Set<String> startElementsAttribute;
	@Value("#{'${text.only.start.elements.attribute}'.split(',')}")
	private Set<String> textOnlyStartElementsAttribute;
	@Value("#{'${start.elements.with.text.attribute}'.split(',')}")
	private Set<String> startElementsWithTextAttribute;
	@Value("#{'${normal.end.elements.attribute}'.split(',')}")
	private Set<String> normalEndElementsAttribute;
	@Value("#{'${list.end.elements.attribute}'.split(',')}")
	private Set<String> listEndElementsAttribute;
	@Value("#{'${root.elements.attribute}'.split(',')}")
	private Set<String> rootElementsAttribute;
	
	
	@Value("#{'${start.elements.classification}'.split(',')}")
	private Set<String> startElementsClassification;
	@Value("#{'${text.only.start.elements.classification}'.split(',')}")
	private Set<String> textOnlyStartElementsClassification;
	@Value("#{'${start.elements.with.text.classification}'.split(',')}")
	private Set<String> startElementsWithTextClassification;
	@Value("#{'${normal.end.elements.classification}'.split(',')}")
	private Set<String> normalEndElementsClassification;
	@Value("#{'${list.end.elements.classification}'.split(',')}")
	private Set<String> listEndElementsClassification;
	@Value("#{'${root.elements.classification}'.split(',')}")
	private Set<String> rootElementsClassification;
	
	
	@Value("#{'${start.elements.asset}'.split(',')}")
	private Set<String> startElementsAsset;
	@Value("#{'${text.only.start.elements.asset}'.split(',')}")
	private Set<String> textOnlyStartElementsAsset;
	@Value("#{'${start.elements.with.text.asset}'.split(',')}")
	private Set<String> startElementsWithTextAsset;
	@Value("#{'${normal.end.elements.asset}'.split(',')}")
	private Set<String> normalEndElementsAsset;
	@Value("#{'${list.end.elements.asset}'.split(',')}")
	private Set<String> listEndElementsAsset;
	@Value("#{'${root.elements.asset}'.split(',')}")
	private Set<String> rootElementsAsset;
	
	@Value("#{'${start.elements.product}'.split(',')}")
	private Set<String> startElementsProduct;
	@Value("#{'${text.only.start.elements.product}'.split(',')}")
	private Set<String> textOnlyStartElementsProduct;
	@Value("#{'${start.elements.with.text.product}'.split(',')}")
	private Set<String> startElementsWithTextProduct;
	@Value("#{'${normal.end.elements.product}'.split(',')}")
	private Set<String> normalEndElementsProduct;
	@Value("#{'${list.end.elements.product}'.split(',')}")
	private Set<String> listEndElementsProduct;
	@Value("#{'${root.elements.product}'.split(',')}")
	private Set<String> rootElementsProduct;
	
	
	private Set<String> startElements;

	private Set<String> textOnlyStartElements;

	private Set<String> startElementsWithText;

	private Set<String> normalEndElements;

	private Set<String> listEndElements;

	private Set<String> rootElements;

	public Stack<Map> parseContext = new Stack<>();

	public Map parse(XMLEventReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();
			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();
				String elementName = startElement.getName().getLocalPart();
				if (startElements.contains(elementName)) {
					parseElementWithAttribute(startElement, elementName);
				} else if (textOnlyStartElements.contains(elementName)) {
					nextEvent = parseElementWithTextValueOnly(reader, elementName);
				} else if (startElementsWithText.contains(elementName)) {
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

	@PostConstruct
	public void init() {
		startElements = new HashSet<>();
		startElements.addAll(startElementsAttribute);
		startElements.addAll(startElementsClassification);
		startElements.addAll(startElementsAsset);
		startElements.addAll(startElementsProduct);
		
		textOnlyStartElements = new HashSet<>();
		textOnlyStartElements.addAll(textOnlyStartElementsAttribute);
		textOnlyStartElements.addAll(textOnlyStartElementsClassification);
		textOnlyStartElements.addAll(textOnlyStartElementsAsset);
		textOnlyStartElements.addAll(textOnlyStartElementsProduct);
		
		startElementsWithText = new HashSet<>();
		startElementsWithText.addAll(startElementsWithTextAttribute);
		startElementsWithText.addAll(startElementsWithTextClassification);
		startElementsWithText.addAll(startElementsWithTextAsset);
		startElementsWithText.addAll(startElementsWithTextProduct);
		
		normalEndElements = new HashSet<>();
		normalEndElements.addAll(normalEndElementsAttribute);
		normalEndElements.addAll(normalEndElementsClassification);
		normalEndElements.addAll(normalEndElementsAsset);
		normalEndElements.addAll(normalEndElementsProduct);
		
		listEndElements = new HashSet<>();
		listEndElements.addAll(listEndElementsAttribute);
		listEndElements.addAll(listEndElementsClassification);
		listEndElements.addAll(listEndElementsAsset);
		listEndElements.addAll(listEndElementsProduct);
		
		rootElements = new HashSet<>();
		rootElements.addAll(rootElementsAttribute);
		rootElements.addAll(rootElementsClassification);
		rootElements.addAll(rootElementsAsset);
		rootElements.addAll(rootElementsProduct);
	}

	private XMLEvent parseElementWithTextValueOnly(XMLEventReader reader, String elementName)
			throws XMLStreamException {
		// <Name>Default Sku</Name>
		Map entity = parseContext.peek();
		XMLEvent nextEvent = reader.nextEvent();
		if (!nextEvent.isEndElement()) {
			entity.put(elementName, nextEvent.asCharacters().getData());
		}
		return nextEvent;
	}

	private void parseElementWithAttribute(StartElement startElement, String elementName) {
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

	private XMLEvent parseElementWithAttributeAndText(XMLEventReader reader, StartElement startElement,
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

	private boolean maintainNormalEndElement(String elementName) {
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

	private boolean maintainListEndElement(String subListName) {
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
