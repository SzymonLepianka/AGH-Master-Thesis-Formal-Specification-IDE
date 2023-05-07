package sl.fside.services;

import com.google.inject.*;
import javafx.util.*;
import org.w3c.dom.*;
import sl.fside.factories.*;
import sl.fside.model.*;

import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;

public class XmlParserService {

    private final IModelFactory modelFactory;
    private final LoggerService loggerService;

    @Inject
    XmlParserService(IModelFactory modelFactory, LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
    }

    public void parseXml(UseCaseDiagram useCaseDiagram, File xmlFile) {
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
            List<String> ucExpressionsList = new ArrayList<>();
            String expression = "/*[local-name()='XMI']/*[local-name()='Model']/packagedElement[@type='uml:UseCase']";
            String expression2 = "/*[local-name()='Model']/packagedElement[@type='uml:UseCase']";
            String expression3 = "/Project/Models/Model[@Abstract='false']/ModelChildren/UseCase";
            String expression4 = "/Project/Models/Model[@Abstract='false']/ModelChildren/System/ModelChildren/UseCase";
            String expression5 =
                    "/Project/Models/Model[@composite='false']/ChildModels/Model/ChildModels/Model[@modelType='UseCase']";
            String expression6 = "/*[local-name()='XMI']/UMLProject/UMLModel/UMLUseCase";
            String expression7 =
                    "/XMI/XMI.content/Model_Management.Model/Foundation.Core.Namespace.ownedElement/Model_Management.Package/Foundation.Core.Namespace.ownedElement/Behavioral_Elements.Use_Cases.UseCase";
            String expression8 =
                    "/XMI/XMI.content/*[local-name()='Model']/*[local-name()='Namespace.ownedElement']/*[local-name()='UseCase']";
            String expression9 =
                    "/XMI/XMI.content/*[local-name()='Model']/*[local-name()='Namespace.ownedElement']/*[local-name()='Package']/*[local-name()='Namespace.ownedElement']/*[local-name()='UseCase']";
            String expression10 =
                    "/*[local-name()='XMI']/*[local-name()='Model']/packagedElement[@type='uml:Package']/packagedElement[@type='uml:UseCase']";
            ucExpressionsList.add(expression);
            ucExpressionsList.add(expression2);
            ucExpressionsList.add(expression3);
            ucExpressionsList.add(expression4);
            ucExpressionsList.add(expression5);
            ucExpressionsList.add(expression6);
            ucExpressionsList.add(expression7);
            ucExpressionsList.add(expression8);
            ucExpressionsList.add(expression9);
            ucExpressionsList.add(expression10);

            List<String> extExpressionsList = new ArrayList<>();
            String extExpression1 = "/*[local-name()='XMI']/*[local-name()='Model']/packagedElement/extend";
            String extExpression2 = "/*[local-name()='Model']/packagedElement/extend";
            String extExpression3 =
                    "/Project/Models/ModelRelationshipContainer/ModelChildren/ModelRelationshipContainer/ModelChildren/Extend";
            extExpressionsList.add(extExpression1);
            extExpressionsList.add(extExpression2);
            extExpressionsList.add(extExpression3);

            List<String> incExpressionsList = new ArrayList<>();
            String incExpression1 = "/*[local-name()='XMI']/*[local-name()='Model']/packagedElement/include";
            String incExpression2 = "/*[local-name()='Model']/packagedElement/include";
            String incExpression3 =
                    "/Project/Models/ModelRelationshipContainer/ModelChildren/ModelRelationshipContainer/ModelChildren/Include";
            incExpressionsList.add(incExpression1);
            incExpressionsList.add(incExpression2);
            incExpressionsList.add(incExpression3);


            findUseCases(useCaseDiagram, doc, xPath, ucExpressionsList, extExpressionsList, incExpressionsList);
            loggerService.logInfo("XML parsed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findUseCases(UseCaseDiagram useCaseDiagram, Document doc, XPath xPath, List<String> ucExpressionsList,
                              List<String> extExpressionsList, List<String> incExpressionsList) throws Exception {
        NodeList ucXmlElems = gatherXmlElems(doc, xPath, ucExpressionsList);
        var useCases = getUseCasesFromUcXmlElems(ucXmlElems);
        createUseCaseObjects(useCases, useCaseDiagram);

        var extXmlElems = gatherXmlElems(doc, xPath, extExpressionsList);
        var extend = getExtendFromExtXmlElems(extXmlElems, useCases);
        createRelationObjects(extend, Relation.RelationType.EXTEND, useCaseDiagram);

        var incXmlElems = gatherXmlElems(doc, xPath, incExpressionsList);
        var include = getIncludeFromIncXmlElems(incXmlElems, useCases);
        createRelationObjects(include, Relation.RelationType.INCLUDE, useCaseDiagram);

        // TODO INHERIT
    }

    private NodeList gatherXmlElems(Document doc, XPath xPath, List<String> expressionsList) throws Exception {
        for (String extExpression : expressionsList) {
            NodeList nodeList = (NodeList) xPath.compile(extExpression).evaluate(doc, XPathConstants.NODESET);
            if (nodeList.getLength() > 0) {
                return nodeList;
            }
        }

        loggerService.logWarning("Brak elementów spełniających expressionsList (gatherXmlElems)");
        return doc.createElement("emptyNodeList").getChildNodes(); // empty list
    }

    private Map<String, String> getUseCasesFromUcXmlElems(NodeList ucXmlElems) {
        var useCases = new HashMap<String, String>();
        for (int j = 0; j < ucXmlElems.getLength(); j++) {
            Node elem = ucXmlElems.item(j);
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
            } else if (elem.getNodeName().equals("Foundation.Core.ModelElement.name")) {
                useCases.put(id, elem.getTextContent());
            } else if (elementAttributes.containsKey("xmi.id")) {
                var childNodes = elem.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE &&
                            childNodes.item(i).getNodeName().equals("Foundation.Core.ModelElement.name")) {
                        useCases.put(id, childNodes.item(i).getTextContent());
                    }
                }
            }
        }
        return useCases;
    }

    private List<Pair<String, String>> getExtendFromExtXmlElems(NodeList extXmlElems, Map<String, String> useCases) {
        var extend = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < extXmlElems.getLength(); i++) {
            Node elem = extXmlElems.item(i);
            var elementAttributes = getAttributesFromNamedNodeMap(elem.getAttributes());
            if (elementAttributes.containsKey("extension")) {
                var from = useCases.get(elementAttributes.get("extension"));
                var to = useCases.get(elementAttributes.get("extendedCase"));
                extend.add(new Pair<>(from, to));
            } else if (elementAttributes.containsKey("extendedCase")) {
                var parentElementAttributes = getAttributesFromNamedNodeMap(elem.getParentNode().getAttributes());
                var from = parentElementAttributes.get("name");
                var to = useCases.get(elementAttributes.get("extendedCase"));
                extend.add(new Pair<>(from, to));
            } else if (elementAttributes.containsKey("From")) {
                var from = useCases.get(elementAttributes.get("To"));
                var to = useCases.get(elementAttributes.get("From"));
                extend.add(new Pair<>(from, to));
            }
        }
        return extend;
    }

    private List<Pair<String, String>> getIncludeFromIncXmlElems(NodeList incXmlElems, Map<String, String> useCases) {
        var include = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < incXmlElems.getLength(); i++) {
            Node elem = incXmlElems.item(i);
            var elementAttributes = getAttributesFromNamedNodeMap(elem.getAttributes());
            if (elementAttributes.containsKey("includingCase")) {
                var from = useCases.get(elementAttributes.get("includingCase"));
                var to = useCases.get(elementAttributes.get("addition"));
                include.add(new Pair<>(from, to));
            } else if (elementAttributes.containsKey("addition")) {
                var parentElementAttributes = getAttributesFromNamedNodeMap(elem.getParentNode().getAttributes());
                var from = parentElementAttributes.get("name");
                var to = useCases.get(elementAttributes.get("addition"));
                include.add(new Pair<>(from, to));
            } else if (elementAttributes.containsKey("From")) {
                var from = useCases.get(elementAttributes.get("From"));
                var to = useCases.get(elementAttributes.get("To"));
                include.add(new Pair<>(from, to));
            }
        }
        return include;
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

    private void createUseCaseObjects(Map<String, String> useCases, UseCaseDiagram useCaseDiagram) {
        for (var entry : useCases.entrySet()) {
            var useCase = modelFactory.createUseCase(useCaseDiagram, UUID.randomUUID(),
                    entry.getValue().replace(" ", "_").toLowerCase(), true);
            useCase.setPrettyName(entry.getValue());
        }
    }

    private void createRelationObjects(List<Pair<String, String>> relations, Relation.RelationType relationType,
                                       UseCaseDiagram useCaseDiagram) {
        for (var relation : relations) {
            UseCase useCaseFrom = useCaseDiagram.getUseCaseList().stream()
                    .filter(uc -> uc.getUseCasePrettyName().equals(relation.getKey())).findFirst().orElseThrow();
            UseCase useCaseTo = useCaseDiagram.getUseCaseList().stream()
                    .filter(uc -> uc.getUseCasePrettyName().equals(relation.getValue())).findFirst().orElseThrow();
            modelFactory.createRelation(useCaseDiagram, UUID.randomUUID(), useCaseFrom.getId(), useCaseTo.getId(),
                    relationType);
        }
    }
}
