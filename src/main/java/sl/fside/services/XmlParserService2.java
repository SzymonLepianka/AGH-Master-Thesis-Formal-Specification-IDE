package sl.fside.services;

import com.google.inject.*;
import javafx.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import sl.fside.factories.*;
import sl.fside.model.*;

import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;

public class XmlParserService2 {

    private final IModelFactory modelFactory;
    private final LoggerService loggerService;

    @Inject
    XmlParserService2(IModelFactory modelFactory, LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
    }

    public Map<String, Map<String, List<String>>> parseXml2(UseCaseDiagram useCaseDiagram, File xmlFile) {
        try {
            // Instantiate the Factory
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "//student/subject";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                System.out.println("Subject: " + nodeList.item(i).getTextContent());
            }

            var useCases = findUseCases(doc);
            createUseCaseObjects(useCases, useCaseDiagram);
            loggerService.logInfo("XML parsed");
            return useCases;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createUseCaseObjects(Map<String, Map<String, List<String>>> useCases, UseCaseDiagram useCaseDiagram) {
        useCaseDiagram.setUseCasesRaw(useCases);
        var useCasesList = new ArrayList<UseCase>();
        for (var entry : useCases.entrySet()) {
            var useCase = modelFactory.createUseCase(useCaseDiagram, UUID.randomUUID(), entry.getKey(), true);
            useCase.setPrettyName(entry.getValue().get("NAME").get(0));
            useCasesList.add(useCase);
        }
        for (var entry : useCases.entrySet()) {
            var useCase = useCasesList.stream().filter(x -> x.getUseCaseName().equals(entry.getKey())).findFirst().orElseThrow();
            for (var inc : entry.getValue().get("INCLUDE")) {
                var useCaseInInclude = useCasesList.stream().filter(x -> x.getUseCaseName().equals(inc)).findFirst().orElseThrow();
                useCase.addRelations(useCaseInInclude.getId(), UseCase.RelationEnum.INCLUDE);
                modelFactory.createRelation(useCaseDiagram, UUID.randomUUID(), useCase.getId(), useCaseInInclude.getId(), Relation.RelationType.INCLUDE);
            }
            for (var ext : entry.getValue().get("EXTEND")) {
                var useCaseInExtend = useCasesList.stream().filter(x -> x.getUseCaseName().equals(ext)).findFirst().orElseThrow();
                useCase.addRelations(useCaseInExtend.getId(), UseCase.RelationEnum.EXTEND);
                modelFactory.createRelation(useCaseDiagram, UUID.randomUUID(), useCase.getId(), useCaseInExtend.getId(), Relation.RelationType.EXTEND);
            }
        }
    }

    private Map<String, Map<String, List<String>>> findUseCases(Document doc) {
        var root = doc.getDocumentElement();
        var rootTag = root.getTagName(); // root.tag (xmi:XMI)
        var rootAttrib = getAttributesFromNamedNodeMap(root.getAttributes()); // root.attrib (xmi:version) oraz root.nsmap (xmlns:xsi, xmlns:uml, xmlns:xmi)
        var namespaces = rootAttrib; // TODO możliwe że usunąć (xmi:version)
        var ucMatches = gatherRulesUseCases(namespaces);
        var extMatches = gatherRulesExtends(namespaces);
        var incMatches = gatherRulesIncludes(namespaces);

        var ucXmlElems = gatherUcXmlElems(root);
        var useCases = getUseCasesFromUcXmlElems(ucXmlElems);
        var useCasesList = new ArrayList<>(useCases.values());

        var extXmlElems = gatherExtXmlElems(root);
        var extend = getExtendFromExtXmlElems(extXmlElems, useCases);

        var incXmlElems = gatherIncXmlElems(root);
        var include = getIncludeFromIncXmlElems(incXmlElems, useCases);

        var matchedUseCases = matchIncludeExtend(useCasesList, include, extend);
        return matchedUseCases;
    }

    private Map<String, Map<String, List<String>>> matchIncludeExtend(List<String> useCasesList, Map<String, Pair<String, String>> include, Map<String, Pair<String, String>> extend) {
        var matchedUseCases = new HashMap<String, Map<String, List<String>>>();
        for (String ucName : useCasesList) {
            var id_ = ucName.replace(" ", "_").toLowerCase();
            var useCaseDescription = new HashMap<String, List<String>>();
            useCaseDescription.put("NAME", new ArrayList<>(List.of(ucName)));
            useCaseDescription.put("INCLUDE", new ArrayList<>());
            useCaseDescription.put("EXTEND", new ArrayList<>());
            matchedUseCases.put(id_, useCaseDescription);
        }
        for (var entryMatchedUseCases : matchedUseCases.entrySet()) {
            for (var entryInclude : include.entrySet()) {
                if (entryInclude.getValue().getKey().equals(entryMatchedUseCases.getValue().get("NAME").get(0))) {
                    var to = entryInclude.getValue().getValue();
                    to = to.replace(" ", "_").toLowerCase();
                    entryMatchedUseCases.getValue().get("INCLUDE").add(to);
                }
            }
            for (var entryExtend : extend.entrySet()) {
                if (entryExtend.getValue().getKey().equals(entryMatchedUseCases.getValue().get("NAME").get(0))) {
                    var to = entryExtend.getValue().getValue();
                    to = to.replace(" ", "_").toLowerCase();
                    entryMatchedUseCases.getValue().get("EXTEND").add(to);
                }
            }
        }
        return matchedUseCases;
    }

    private Map<String, Pair<String, String>> getExtendFromExtXmlElems(List<Element> extXmlElems, Map<String, String> useCases) {
        var extend = new HashMap<String, Pair<String, String>>();
        var cnt = 1;
        for (var elem : extXmlElems) {
            var elementAttributes = getAttributesFromNamedNodeMap(elem.getAttributes());
            var key = "Ext" + cnt;
            if (elementAttributes.containsKey("extension")) {
                var from = useCases.get(elementAttributes.get("extension"));
                var to = useCases.get(elementAttributes.get("extendedCase"));
                extend.put(key, new Pair<>(from, to));
            } else if (elementAttributes.containsKey("extendedCase")) {
                var parentElementAttributes = getAttributesFromNamedNodeMap(elem.getParentNode().getAttributes());
                var from = parentElementAttributes.get("name");
                var to = useCases.get(elementAttributes.get("extendedCase"));
                extend.put(key, new Pair<>(from, to));
            } else if (elementAttributes.containsKey("From")) {
                var from = useCases.get(elementAttributes.get("To"));
                var to = useCases.get(elementAttributes.get("From"));
                extend.put(key, new Pair<>(from, to));
            }
            cnt++;
        }
        return extend;
    }

    private Map<String, Pair<String, String>> getIncludeFromIncXmlElems(List<Element> incXmlElems, Map<String, String> useCases) {
        var include = new HashMap<String, Pair<String, String>>();
        var cnt = 1;
        for (var elem : incXmlElems) {
            var elementAttributes = getAttributesFromNamedNodeMap(elem.getAttributes());
            var key = "Inc" + cnt;
            if (elementAttributes.containsKey("includingCase")) {
                var from = useCases.get(elementAttributes.get("includingCase"));
                var to = useCases.get(elementAttributes.get("addition"));
                include.put(key, new Pair<>(from, to));
            } else if (elementAttributes.containsKey("addition")) {
                var parentElementAttributes = getAttributesFromNamedNodeMap(elem.getParentNode().getAttributes());
                var from = parentElementAttributes.get("name");
                var to = useCases.get(elementAttributes.get("addition"));
                include.put(key, new Pair<>(from, to));
            } else if (elementAttributes.containsKey("From")) {
                var from = useCases.get(elementAttributes.get("From"));
                var to = useCases.get(elementAttributes.get("To"));
                include.put(key, new Pair<>(from, to));
            }
            cnt++;
        }
        return include;
    }

    private List<Element> gatherExtXmlElems(Element root) {
        var rootTag = root.getTagName(); // root.tag (xmi:XMI)
        var extXmlElems = new ArrayList<Element>();
        if (rootTag.equals("xmi:XMI")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("uml:Model")) {
                    var modelNode = (Element) childOfRootNodes.item(i);
                    extXmlElems = new ArrayList<>(getExtXmlElemsFromPackagedElementInsideModel(modelNode));
                }
            }
        } else if (rootTag.equals("uml:Model")) {
            extXmlElems = new ArrayList<>(getExtXmlElemsFromPackagedElementInsideModel(root)); // model element jest rootem w tym przypadku
        } else if (rootTag.equals("Project")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("Models")) {
                    var modelsNode = (Element) childOfRootNodes.item(i);
                    var childOfModelsNode = modelsNode.getChildNodes();
                    for (int j = 0; j < childOfModelsNode.getLength(); j++) {
                        if (childOfModelsNode.item(j).getNodeType() == Node.ELEMENT_NODE && childOfModelsNode.item(j).getNodeName().equals("ModelRelationshipContainer")) {
                            var modelRelationshipContainerNode = (Element) childOfModelsNode.item(j);
                            var childOfModelRelationshipContainerNode = modelRelationshipContainerNode.getChildNodes();
                            for (int k = 0; k < childOfModelRelationshipContainerNode.getLength(); k++) {
                                if (childOfModelRelationshipContainerNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfModelRelationshipContainerNode.item(k).getNodeName().equals("ModelChildren")) {
                                    var modelChildrenNode = (Element) childOfModelRelationshipContainerNode.item(k);
                                    var childOfModelChildrenNode = modelChildrenNode.getChildNodes();
                                    for (int l = 0; l < childOfModelChildrenNode.getLength(); l++) {
                                        if (childOfModelChildrenNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfModelChildrenNode.item(l).getNodeName().equals("ModelRelationshipContainer")) {
                                            var innerModelRelationshipContainer = (Element) childOfModelChildrenNode.item(l);
                                            var childOfInnerModelRelationshipContainer = innerModelRelationshipContainer.getChildNodes();
                                            for (int m = 0; m < childOfInnerModelRelationshipContainer.getLength(); m++) {
                                                if (childOfInnerModelRelationshipContainer.item(m).getNodeType() == Node.ELEMENT_NODE && childOfInnerModelRelationshipContainer.item(m).getNodeName().equals("ModelChildren")) {
                                                    var innerModelChildrenNode = (Element) childOfInnerModelRelationshipContainer.item(m);
                                                    var childOfInnerModelChildrenNode = innerModelChildrenNode.getChildNodes();
                                                    for (int n = 0; n < childOfInnerModelChildrenNode.getLength(); n++) {
                                                        if (childOfInnerModelChildrenNode.item(n).getNodeType() == Node.ELEMENT_NODE && childOfInnerModelChildrenNode.item(n).getNodeName().equals("Extend")) {
                                                            extXmlElems.add((Element) childOfInnerModelChildrenNode.item(n));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            //TODO zmienić na logger
            System.out.println("Inny root tag name");
        }
        return extXmlElems;
    }

    private List<Element> getExtXmlElemsFromPackagedElementInsideModel(Element modelNode) {
        var extXmlElems = new ArrayList<Element>();
        var childOfModelNodes = modelNode.getChildNodes();
        for (int j = 0; j < childOfModelNodes.getLength(); j++) {
            if (childOfModelNodes.item(j).getNodeType() == Node.ELEMENT_NODE && childOfModelNodes.item(j).getNodeName().equals("packagedElement")) {
                var packagedElementNode = childOfModelNodes.item(j);
                var childOfPackagedElementNode = packagedElementNode.getChildNodes();
                for (int k = 0; k < childOfPackagedElementNode.getLength(); k++) {
                    if (childOfPackagedElementNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfPackagedElementNode.item(k).getNodeName().equals("extend")) {
                        var extendNode = (Element) childOfPackagedElementNode.item(k);
                        extXmlElems.add(extendNode);
                    }
                }
            }
        }
        return extXmlElems;
    }

    private List<Element> gatherIncXmlElems(Element root) {
        var rootTag = root.getTagName(); // root.tag (xmi:XMI)
        var incXmlElems = new ArrayList<Element>();
        if (rootTag.equals("xmi:XMI")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("uml:Model")) {
                    var modelNode = (Element) childOfRootNodes.item(i);
                    incXmlElems = new ArrayList<>(getIncXmlElemsFromPackagedElementInsideModel(modelNode));
                }
            }
        } else if (rootTag.equals("uml:Model")) {
            incXmlElems = new ArrayList<>(getIncXmlElemsFromPackagedElementInsideModel(root)); // model element jest rootem w tym przypadku
        } else if (rootTag.equals("Project")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("Models")) {
                    var modelsNode = (Element) childOfRootNodes.item(i);
                    var childOfModelsNode = modelsNode.getChildNodes();
                    for (int j = 0; j < childOfModelsNode.getLength(); j++) {
                        if (childOfModelsNode.item(j).getNodeType() == Node.ELEMENT_NODE && childOfModelsNode.item(j).getNodeName().equals("ModelRelationshipContainer")) {
                            var modelRelationshipContainerNode = (Element) childOfModelsNode.item(j);
                            var childOfModelRelationshipContainerNode = modelRelationshipContainerNode.getChildNodes();
                            for (int k = 0; k < childOfModelRelationshipContainerNode.getLength(); k++) {
                                if (childOfModelRelationshipContainerNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfModelRelationshipContainerNode.item(k).getNodeName().equals("ModelChildren")) {
                                    var modelChildrenNode = (Element) childOfModelRelationshipContainerNode.item(k);
                                    var childOfModelChildrenNode = modelChildrenNode.getChildNodes();
                                    for (int l = 0; l < childOfModelChildrenNode.getLength(); l++) {
                                        if (childOfModelChildrenNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfModelChildrenNode.item(l).getNodeName().equals("ModelRelationshipContainer")) {
                                            var innerModelRelationshipContainer = (Element) childOfModelChildrenNode.item(l);
                                            var childOfInnerModelRelationshipContainer = innerModelRelationshipContainer.getChildNodes();
                                            for (int m = 0; m < childOfInnerModelRelationshipContainer.getLength(); m++) {
                                                if (childOfInnerModelRelationshipContainer.item(m).getNodeType() == Node.ELEMENT_NODE && childOfInnerModelRelationshipContainer.item(m).getNodeName().equals("ModelChildren")) {
                                                    var innerModelChildrenNode = (Element) childOfInnerModelRelationshipContainer.item(m);
                                                    var childOfInnerModelChildrenNode = innerModelChildrenNode.getChildNodes();
                                                    for (int n = 0; n < childOfInnerModelChildrenNode.getLength(); n++) {
                                                        if (childOfInnerModelChildrenNode.item(n).getNodeType() == Node.ELEMENT_NODE && childOfInnerModelChildrenNode.item(n).getNodeName().equals("Include")) {
                                                            incXmlElems.add((Element) childOfInnerModelChildrenNode.item(n));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            //TODO zmienić na logger
            System.out.println("Inny root tag name");
        }
        return incXmlElems;
    }

    private List<Element> getIncXmlElemsFromPackagedElementInsideModel(Element modelNode) {
        var incXmlElems = new ArrayList<Element>();
        var childOfModelNodes = modelNode.getChildNodes();
        for (int j = 0; j < childOfModelNodes.getLength(); j++) {
            if (childOfModelNodes.item(j).getNodeType() == Node.ELEMENT_NODE && childOfModelNodes.item(j).getNodeName().equals("packagedElement")) {
                var packagedElementNode = childOfModelNodes.item(j);
                var childOfPackagedElementNode = packagedElementNode.getChildNodes();
                for (int k = 0; k < childOfPackagedElementNode.getLength(); k++) {
                    if (childOfPackagedElementNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfPackagedElementNode.item(k).getNodeName().equals("include")) {
                        var includeNode = (Element) childOfPackagedElementNode.item(k);
                        incXmlElems.add(includeNode);
                    }
                }
            }
        }
        return incXmlElems;
    }

    private Map<String, String> getUseCasesFromUcXmlElems(List<Element> ucXmlElems) {
        var useCases = new HashMap<String, String>();
        for (var elem : ucXmlElems) {
            var elementAttributes = getAttributesFromNamedNodeMap(elem.getAttributes());
            var id = "";
            if (elementAttributes.containsKey("id")) {
                id = elementAttributes.get("id");
            } else if (elementAttributes.containsKey("Id")) {
                id = elementAttributes.get("Id");
            } else {
                id = elementAttributes.get("xmi.id");
            }
            if (elementAttributes.containsKey("Name")) {
                useCases.put(id, elementAttributes.get("Name"));
            } else if (elementAttributes.containsKey("name")) {
                useCases.put(id, elementAttributes.get("name"));
            } else if (elem.getTagName().equals("Foundation.Core.ModelElement.name")) {
                useCases.put(id, elem.getTextContent());
            } else if (elementAttributes.containsKey("xmi.id")) {
                var childNodes = elem.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childNodes.item(i).getNodeName().equals("Foundation.Core.ModelElement.name")) {
                        useCases.put(id, childNodes.item(i).getTextContent());
                    }
                }
            }
        }
        return useCases;
    }

    private List<Element> gatherUcXmlElems(Element root) {
        var rootTag = root.getTagName(); // root.tag (xmi:XMI)
        var ucXmlElems = new ArrayList<Element>();
        if (rootTag.equals("xmi:XMI")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("uml:Model")) {
                    var modelNode = (Element) childOfRootNodes.item(i);
                    ucXmlElems = new ArrayList<>(getUcXmlElemsFromPackagedElementInsideModel(modelNode));
                } else if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("UMLProject")) {
                    var umlProjectNode = (Element) childOfRootNodes.item(i);
                    var childOfUmlProjectNode = umlProjectNode.getChildNodes();
                    for (int j = 0; j < childOfUmlProjectNode.getLength(); j++) {
                        if (childOfUmlProjectNode.item(j).getNodeType() == Node.ELEMENT_NODE && childOfUmlProjectNode.item(j).getNodeName().equals("UMLModel")) {
                            var umlModelNode = (Element) childOfUmlProjectNode.item(j);
                            var childOUmlModelNode = umlModelNode.getChildNodes();
                            for (int k = 0; k < childOUmlModelNode.getLength(); k++) {
                                if (childOUmlModelNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOUmlModelNode.item(k).getNodeName().equals("UMLUseCase")) {
                                    ucXmlElems.add((Element) childOUmlModelNode.item(k));
                                }
                            }
                        }
                    }
                }
            }
        } else if (rootTag.equals("uml:Model")) {
            ucXmlElems = new ArrayList<>(getUcXmlElemsFromPackagedElementInsideModel(root)); // model element jest rootem w tym przypadku
        } else if (rootTag.equals("Project")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("Models")) {
                    var modelsNode = (Element) childOfRootNodes.item(i);
                    var childOfModelsNode = modelsNode.getChildNodes();
                    for (int j = 0; j < childOfModelsNode.getLength(); j++) {
                        if (childOfModelsNode.item(j).getNodeType() == Node.ELEMENT_NODE && childOfModelsNode.item(j).getNodeName().equals("Model")) {
                            var modelNode = (Element) childOfModelsNode.item(j);
                            if (modelNode.hasAttribute("Abstract") && modelNode.getAttribute("Abstract").equals("false")) {
                                var childOfModelNode = modelNode.getChildNodes();
                                for (int k = 0; k < childOfModelNode.getLength(); k++) {
                                    if (childOfModelNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfModelNode.item(k).getNodeName().equals("ModelChildren")) {
                                        var modelChildrenNode = (Element) childOfModelNode.item(k);
                                        var childOfModelChildrenNode = modelChildrenNode.getChildNodes();
                                        for (int l = 0; l < childOfModelChildrenNode.getLength(); l++) {
                                            if (childOfModelChildrenNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfModelChildrenNode.item(l).getNodeName().equals("UseCase")) {
                                                ucXmlElems.add((Element) childOfModelChildrenNode.item(l));
                                            } else if (childOfModelChildrenNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfModelChildrenNode.item(l).getNodeName().equals("System")) {
                                                var systemNode = (Element) childOfModelChildrenNode.item(l);
                                                var childOfSystemNode = systemNode.getChildNodes();
                                                for (int m = 0; m < childOfSystemNode.getLength(); m++) {
                                                    if (childOfSystemNode.item(m).getNodeType() == Node.ELEMENT_NODE && childOfSystemNode.item(m).getNodeName().equals("ModelChildren")) {
                                                        var innerModelChildrenNode = (Element) childOfSystemNode.item(m);
                                                        var childOfInnerModelChildrenNode = innerModelChildrenNode.getChildNodes();
                                                        for (int n = 0; n < childOfInnerModelChildrenNode.getLength(); n++) {
                                                            if (childOfInnerModelChildrenNode.item(n).getNodeType() == Node.ELEMENT_NODE && childOfInnerModelChildrenNode.item(n).getNodeName().equals("UseCase")) {
                                                                ucXmlElems.add((Element) childOfInnerModelChildrenNode.item(n));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (modelNode.hasAttribute("composite") && modelNode.getAttribute("composite").equals("false")) {
                                var childOfModelNode = modelNode.getChildNodes();
                                for (int k = 0; k < childOfModelNode.getLength(); k++) {
                                    if (childOfModelNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfModelNode.item(k).getNodeName().equals("ChildModels")) {
                                        var childModelsNode = (Element) childOfModelNode.item(k);
                                        var childOfChildModelsNode = childModelsNode.getChildNodes();
                                        for (int l = 0; l < childOfChildModelsNode.getLength(); l++) {
                                            if (childOfChildModelsNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfChildModelsNode.item(l).getNodeName().equals("Model")) {
                                                var innerModelNode = (Element) childOfChildModelsNode.item(l);
                                                var childOfInnerModelNode = innerModelNode.getChildNodes();
                                                for (int m = 0; m < childOfInnerModelNode.getLength(); m++) {
                                                    if (childOfInnerModelNode.item(m).getNodeType() == Node.ELEMENT_NODE && childOfInnerModelNode.item(m).getNodeName().equals("ChildModels")) {
                                                        var innerChildModelsNode = (Element) childOfInnerModelNode.item(m);
                                                        var childOfInnerChildModelsNode = innerChildModelsNode.getChildNodes();
                                                        for (int n = 0; n < childOfInnerChildModelsNode.getLength(); n++) {
                                                            if (childOfInnerChildModelsNode.item(n).getNodeType() == Node.ELEMENT_NODE && childOfInnerChildModelsNode.item(n).getNodeName().equals("Model")) {
                                                                var innerInnerModelNode = (Element) childOfInnerChildModelsNode.item(n);
                                                                if (innerInnerModelNode.hasAttribute("modelType") && innerInnerModelNode.getAttribute("modelType").equals("UseCase")) {
                                                                    ucXmlElems.add(innerInnerModelNode);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (rootTag.equals("XMI") && !root.hasAttribute("xmlns:UML")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("XMI.content")) {
                    var xmiContentNode = (Element) childOfRootNodes.item(i);
                    var childOfXmiContentNode = xmiContentNode.getChildNodes();
                    for (int j = 0; j < childOfXmiContentNode.getLength(); j++) {
                        if (childOfXmiContentNode.item(j).getNodeType() == Node.ELEMENT_NODE && childOfXmiContentNode.item(j).getNodeName().equals("Model_Management.Model")) {
                            var modelManagementModelNode = (Element) childOfXmiContentNode.item(j);
                            var childOfModelManagementModelNode = modelManagementModelNode.getChildNodes();
                            for (int k = 0; k < childOfModelManagementModelNode.getLength(); k++) {
                                if (childOfModelManagementModelNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfModelManagementModelNode.item(k).getNodeName().equals("Foundation.Core.Namespace.ownedElement")) {
                                    var FCNOE_Node = (Element) childOfModelManagementModelNode.item(k);
                                    var childOfFCNOE_Node = FCNOE_Node.getChildNodes();
                                    for (int l = 0; l < childOfFCNOE_Node.getLength(); l++) {
                                        if (childOfFCNOE_Node.item(l).getNodeType() == Node.ELEMENT_NODE && childOfFCNOE_Node.item(l).getNodeName().equals("Model_Management.Package")) {
                                            var modelManagementPackageNode = (Element) childOfFCNOE_Node.item(l);
                                            var childOfModelManagementPackageNode = modelManagementPackageNode.getChildNodes();
                                            for (int m = 0; m < childOfModelManagementPackageNode.getLength(); m++) {
                                                if (childOfModelManagementPackageNode.item(m).getNodeType() == Node.ELEMENT_NODE && childOfModelManagementPackageNode.item(m).getNodeName().equals("Foundation.Core.Namespace.ownedElement")) {
                                                    var innerFCNOE_Node = (Element) childOfModelManagementPackageNode.item(m);
                                                    var childOfInnerFCNOE_Node = innerFCNOE_Node.getChildNodes();
                                                    for (int n = 0; n < childOfInnerFCNOE_Node.getLength(); n++) {
                                                        if (childOfInnerFCNOE_Node.item(n).getNodeType() == Node.ELEMENT_NODE && childOfInnerFCNOE_Node.item(n).getNodeName().equals("Behavioral_Elements.Use_Cases.UseCase")) {
                                                            ucXmlElems.add((Element) childOfInnerFCNOE_Node.item(n));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (rootTag.equals("XMI") && root.hasAttribute("xmlns:UML")) {
            var childOfRootNodes = root.getChildNodes();
            for (int i = 0; i < childOfRootNodes.getLength(); i++) {
                if (childOfRootNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childOfRootNodes.item(i).getNodeName().equals("XMI.content")) {
                    var xmiContentNode = (Element) childOfRootNodes.item(i);
                    var childOfXmiContentNode = xmiContentNode.getChildNodes();
                    for (int j = 0; j < childOfXmiContentNode.getLength(); j++) {
                        if (childOfXmiContentNode.item(j).getNodeType() == Node.ELEMENT_NODE && childOfXmiContentNode.item(j).getNodeName().equals("UML:Model")) {
                            var umlModelNode = (Element) childOfXmiContentNode.item(j);
                            var childOfUmlModelNode = umlModelNode.getChildNodes();
                            for (int k = 0; k < childOfUmlModelNode.getLength(); k++) {
                                if (childOfUmlModelNode.item(k).getNodeType() == Node.ELEMENT_NODE && childOfUmlModelNode.item(k).getNodeName().equals("UML:Namespace.ownedElement")) {
                                    var umlNamespaceOwnedElementNode = (Element) childOfUmlModelNode.item(k);
                                    var childOfUmlNamespaceOwnedElementNode = umlNamespaceOwnedElementNode.getChildNodes();
                                    for (int l = 0; l < childOfUmlNamespaceOwnedElementNode.getLength(); l++) {
                                        if (childOfUmlNamespaceOwnedElementNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfUmlNamespaceOwnedElementNode.item(l).getNodeName().equals("UML:UseCase")) {
                                            ucXmlElems.add((Element) childOfUmlNamespaceOwnedElementNode.item(l));
                                        } else if (childOfUmlNamespaceOwnedElementNode.item(l).getNodeType() == Node.ELEMENT_NODE && childOfUmlNamespaceOwnedElementNode.item(l).getNodeName().equals("UML:Package")) {
                                            var umlPackageNode = (Element) childOfUmlNamespaceOwnedElementNode.item(l);
                                            var childOfUmlPackageNode = umlPackageNode.getChildNodes();
                                            for (int m = 0; m < childOfUmlPackageNode.getLength(); m++) {
                                                if (childOfUmlPackageNode.item(m).getNodeType() == Node.ELEMENT_NODE && childOfUmlPackageNode.item(m).getNodeName().equals("UML:Namespace.ownedElement")) {
                                                    var innerUmlNamespaceOwnedElementNode = (Element) childOfUmlPackageNode.item(m);
                                                    var childOfInnerUmlNamespaceOwnedElementNode = innerUmlNamespaceOwnedElementNode.getChildNodes();
                                                    for (int n = 0; n < childOfInnerUmlNamespaceOwnedElementNode.getLength(); n++) {
                                                        if (childOfInnerUmlNamespaceOwnedElementNode.item(n).getNodeType() == Node.ELEMENT_NODE && childOfInnerUmlNamespaceOwnedElementNode.item(n).getNodeName().equals("UML:UseCase")) {
                                                            ucXmlElems.add((Element) childOfInnerUmlNamespaceOwnedElementNode.item(n));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            //TODO zmienić na logger
            System.out.println("Inny root tag name");
        }
        return ucXmlElems;
    }

    private List<Element> getUcXmlElemsFromPackagedElementInsideModel(Element modelNode) {
        var ucXmlElems = new ArrayList<Element>();
        var childOfModelNodes = modelNode.getChildNodes();
        for (int j = 0; j < childOfModelNodes.getLength(); j++) {
            if (childOfModelNodes.item(j).getNodeType() == Node.ELEMENT_NODE && childOfModelNodes.item(j).getNodeName().equals("packagedElement")) {
                var packagedElementNode = (Element) childOfModelNodes.item(j);
                var packagedElementNodeAttributes = getAttributesFromNamedNodeMap(packagedElementNode.getAttributes());
                var type = packagedElementNodeAttributes.get("type");
                if (type.equals("uml:UseCase")) {
                    ucXmlElems.add(packagedElementNode);
                } else if (type.equals("uml:Package")) {
                    ucXmlElems = new ArrayList<>(getUcXmlElemsFromPackagedElementInsideModel(packagedElementNode));
                }
            }
        }
        return ucXmlElems;
    }

    private Map<String, String> getAttributesFromNamedNodeMap(NamedNodeMap attributesRaw) {
        var attributesMap = new HashMap<String, String>();
        for (int i = 0; i < attributesRaw.getLength(); i++) {
            var node = attributesRaw.item(i);
            var nodeName = node.getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.split(":")[1]; // example: "xmi:version" --> "version"
            }
            attributesMap.put(nodeName, node.getTextContent());
        }
        return attributesMap;
    }

    private List<String> gatherRulesUseCases(Map<String, String> namespaces) {
        var ucMatches = new ArrayList<String>();
        ucMatches.add(".//UseCase[@Abstract='false']");    // visual paradigm 'Xml_structure': 'simple'
        ucMatches.add(".//Model[@modelType='UseCase']");   // visual paradigm 'Xml_structure': 'traditional'
        ucMatches.add(".//UMLUseCase");                    // Sinvas
        ucMatches.add(".//Behavioral_Elements.Use_Cases.UseCase/Foundation.Core.ModelElement.name");  // EnterpriseArchitect XMI 1.0 UML 1.3

        if (namespaces.containsKey("xmi")) {
            ucMatches.add(".//packagedElement[@xmi:type='uml:UseCase']");  // Papyrus {'xmi': 'http://www.omg.org/spec/XMI/20131001'}
            // EnterpriseArchitect xmi 2.1 >= 2.5.1, uml 2.1 >= 2.5.1 {'xmi': 'http://schema.omg.org/spec/XMI/2.1'}
        }
        if (namespaces.containsKey("xsi")) {
            ucMatches.add(".//packagedElement[@xsi:type='uml:UseCase']");  // GenMyModel {'xsi': 'http://www.w3.org/2001/XMLSchema-instance'}

        }
        if (namespaces.containsKey("UML")) {
            ucMatches.add(".//UML:UseCase");   // EnterpriseArchitect XMI 1.1 UML 1.3 {'UML': 'omg.org/UML1.3'},
            // EnterpriseArchitect XMI 1.2 UML 1.4 {'UML': 'org.omg.xmi.namespace.UML'}
        }
        return ucMatches;
    }

    private List<String> gatherRulesExtends(Map<String, String> namespaces) {
        var extMatches = new ArrayList<String>();
        extMatches.add(".//Extend[@BacklogActivityId='0']");

        if (namespaces.containsKey("xmi")) {
            extMatches.add(".//extend[@xmi:type='uml:Extend']");
        }
        if (namespaces.containsKey("xsi")) {
            extMatches.add(".//extend");
        }
        return extMatches;
    }

    private List<String> gatherRulesIncludes(Map<String, String> namespaces) {
        var incMatches = new ArrayList<String>();
        incMatches.add(".//Include[@BacklogActivityId='0']");

        if (namespaces.containsKey("xmi")) {
            incMatches.add(".//include[@xmi:type='uml:Include']");
        }
        if (namespaces.containsKey("xsi")) {
            incMatches.add(".//include");
        }
        return incMatches;
    }
}
