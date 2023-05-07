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

            var useCases = findUseCases(doc, xPath, ucExpressionsList, extExpressionsList, incExpressionsList);
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
            var useCase = useCasesList.stream().filter(x -> x.getUseCaseName().equals(entry.getKey())).findFirst()
                    .orElseThrow();
            for (var inc : entry.getValue().get("INCLUDE")) {
                var useCaseInInclude =
                        useCasesList.stream().filter(x -> x.getUseCaseName().equals(inc)).findFirst().orElseThrow();
                useCase.addRelations(useCaseInInclude.getId(), UseCase.RelationEnum.INCLUDE);
                modelFactory.createRelation(useCaseDiagram, UUID.randomUUID(), useCase.getId(),
                        useCaseInInclude.getId(), Relation.RelationType.INCLUDE);
            }
            for (var ext : entry.getValue().get("EXTEND")) {
                var useCaseInExtend =
                        useCasesList.stream().filter(x -> x.getUseCaseName().equals(ext)).findFirst().orElseThrow();
                useCase.addRelations(useCaseInExtend.getId(), UseCase.RelationEnum.EXTEND);
                modelFactory.createRelation(useCaseDiagram, UUID.randomUUID(), useCase.getId(), useCaseInExtend.getId(),
                        Relation.RelationType.EXTEND);
            }
        }
    }

    private Map<String, Map<String, List<String>>> findUseCases(Document doc, XPath xPath,
                                                                List<String> ucExpressionsList,
                                                                List<String> extExpressionsList,
                                                                List<String> incExpressionsList) throws Exception {

        var ucXmlElems = gatherXmlElems(doc, xPath, ucExpressionsList);
        var useCases = getUseCasesFromUcXmlElems(ucXmlElems);
        var useCasesList = new ArrayList<>(useCases.values());

        var extXmlElems = gatherXmlElems(doc, xPath, extExpressionsList);
        var extend = getExtendFromExtXmlElems(extXmlElems, useCases);

        var incXmlElems = gatherXmlElems(doc, xPath, incExpressionsList);
        var include = getIncludeFromIncXmlElems(incXmlElems, useCases);

        var matchedUseCases = matchIncludeExtend(useCasesList, include, extend);
        return matchedUseCases;
    }

    private Map<String, Map<String, List<String>>> matchIncludeExtend(List<String> useCasesList,
                                                                      Map<String, Pair<String, String>> include,
                                                                      Map<String, Pair<String, String>> extend) {
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

    private Map<String, Pair<String, String>> getExtendFromExtXmlElems(List<Element> extXmlElems,
                                                                       Map<String, String> useCases) {
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

    private Map<String, Pair<String, String>> getIncludeFromIncXmlElems(List<Element> incXmlElems,
                                                                        Map<String, String> useCases) {
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

    private List<Element> gatherXmlElems(Document doc, XPath xPath, List<String> expressionsList) throws Exception {
        for (String extExpression : expressionsList) {
            NodeList nodeList = (NodeList) xPath.compile(extExpression).evaluate(doc, XPathConstants.NODESET);
            if (nodeList.getLength() > 0) {
                List<Element> elements = new ArrayList<>();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    elements.add((Element) nodeList.item(i));
                }
                return elements;
            }
        }

        loggerService.logError("Brak elementów spełniających expressionsList (gatherXmlElems)");
        return new ArrayList<>();
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
                    if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE &&
                            childNodes.item(i).getNodeName().equals("Foundation.Core.ModelElement.name")) {
                        useCases.put(id, childNodes.item(i).getTextContent());
                    }
                }
            }
        }
        return useCases;
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
}
