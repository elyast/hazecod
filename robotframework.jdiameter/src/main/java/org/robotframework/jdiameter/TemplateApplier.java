package org.robotframework.jdiameter;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies full qualified user parameters into xml template
 * 
 * @author Sasnal.net
 * 
 */
public class TemplateApplier {

    private static final String VALUE_ATTRIBUTE_NAME = "value";
    static Logger logger = LoggerFactory.getLogger(TemplateApplier.class);

    /**
     * Do the applying
     * 
     * @param qualifiedUserParameters user parameters after mapping
     * @param doc
     *            xml template
     */
    public void apply(List<Entry<String, String>> qualifiedUserParameters,
	    Document doc) {
	List<Entry<Element, String>> taskList = 
	    createFinalTreeStructureAndGenerateTaskList(
		doc, qualifiedUserParameters);
	processTaskList(doc, taskList);
	removeEmptyNodes(doc);
    }

    private List<Entry<Element, String>> 
    	createFinalTreeStructureAndGenerateTaskList(
	    Document doc, List<Entry<String, String>> qualifiedUserParameters) {
	assert (doc != null);
	assert (qualifiedUserParameters != null);

	List<Entry<Element, String>> delayedTaskList = 
	    new LinkedList<Entry<Element, String>>();
	logger.debug("Document: " + doc.toXML());
	for (Entry<String, String> userParameter : qualifiedUserParameters) {
	    Element currentElement = doc.getRootElement();
	    logger.debug("Current element: " + currentElement);
	    currentElement = getReferenceToFinalElementInTree(userParameter,
		    currentElement);

	    // add to delayed list of task - overwrite attribute with user value
	    delayedTaskList.add(new AbstractMap.SimpleEntry<Element, String>(
		    currentElement, userParameter.getValue()));
	}

	return delayedTaskList;
    }

    private Element getReferenceToFinalElementInTree(
	    Entry<String, String> userParameter, Element currentElement) {
	String[] parsedParameter = userParameter.getKey().split("\\.");
	for (String nodeName : parsedParameter) {

	    if (nodeName.lastIndexOf("[") != -1) {
		// indexed node
		logger.debug("Indexed Node name: " + nodeName);
		currentElement = getElementFromIndexedNode(currentElement,
			nodeName);
	    } else {
		// non-indexed node
		logger.debug("Non Indexed Node name: " + nodeName);
		currentElement = currentElement.getFirstChildElement(nodeName);
	    }

	    if (currentElement == null) {
		throw new IllegalArgumentException("node " + nodeName
			+ " does not exists");
	    }
	}
	return currentElement;
    }

    private Element getElementFromIndexedNode(Element currentElement,
	    String nodeName) {
	int separatorPosition = nodeName.lastIndexOf("[");
	String indexedNodeName = nodeName.substring(0, separatorPosition);
	int index = Integer.parseInt(nodeName.substring(separatorPosition + 1,
		nodeName.length() - 1));

	Elements children = currentElement.getChildElements(indexedNodeName);
	if (children.size() > index) {
	    currentElement = currentElement.getChildElements(indexedNodeName)
		    .get(index);
	} else if (children.size() == index && children.size() > 0) {
	    // needs to create new node (from copying last sibling
	    // with the same name)
	    int lastChildIndex = currentElement.getChildElements(
		    indexedNodeName).size() - 1;
	    Element child = currentElement.getChildElements(indexedNodeName)
		    .get(lastChildIndex);
	    if (!child.getAttributeValue("cardinality").equals("0..n")) {
		throw new IllegalArgumentException("Cardinality 0..n for "
			+ indexedNodeName + " not exists, but indexed");
	    }
	    currentElement.appendChild(child.copy());
	    currentElement = currentElement.getChildElements(indexedNodeName)
		    .get(lastChildIndex + 1);
	} else {
	    throw new IndexOutOfBoundsException(indexedNodeName
		    + " node number out of bounds");
	}

	return currentElement;
    }

    private void processTaskList(Document doc,
	    List<Entry<Element, String>> delayedTaskList) {
	assert (doc != null);
	assert (delayedTaskList != null);

	for (Entry<Element, String> task : delayedTaskList) {
	    Element currentElement = task.getKey();
	    String userValue = task.getValue();
	    Attribute attrib = currentElement
		    .getAttribute(VALUE_ATTRIBUTE_NAME);
	    if (attrib != null) {
		// overwrite with qualified user value
		attrib.setValue(userValue);
	    } else {
		// create new attribute with qualified user value
		attrib = new Attribute(VALUE_ATTRIBUTE_NAME, userValue);
		currentElement.addAttribute(attrib);
	    }
	}
    }

    private void removeEmptyNodes(Document doc) {
	assert (doc != null);

	removeEmptyChildren(doc.getRootElement());
    }

    private void removeEmptyChildren(Element element) {
	assert (element != null);

	Elements children = element.getChildElements();
	int i = 0;
	while (i < children.size()) {
	    if (isEmpty(children.get(i))) {
		element.removeChild(i);
	    } else {
		removeEmptyChildren(children.get(i));
		i++;
	    }
	    children = element.getChildElements();
	}
    }

    private boolean isEmpty(Element element) {
	assert (element != null);

	if (element.getAttribute(VALUE_ATTRIBUTE_NAME) != null) {
	    return false;
	}

	return !isAnyChildNotEmpty(element);
    }

    private boolean isAnyChildNotEmpty(Element element) {
	Elements children = element.getChildElements();
	for (int i = 0; i < children.size(); i++) {
	    Element child = children.get(i);
	    if (!isEmpty(child)) {
		return true;
	    }
	}
	return false;
    }
}
