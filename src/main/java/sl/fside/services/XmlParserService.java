package sl.fside.services;

import com.google.inject.*;
import javafx.util.*;
import org.json.*;
import org.w3c.dom.*;
import sl.fside.factories.*;
import sl.fside.model.*;

import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class XmlParserService {

    private final IModelFactory modelFactory;
    private final LoggerService loggerService;
    private final String CONFIG_FILENAME = "config.json";

    @Inject
    XmlParserService(IModelFactory modelFactory, LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
    }

    public void parseXml(UseCaseDiagram useCaseDiagram, File xmlFile) throws Exception {
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

        // get expressions from config
        String content = Files.readString(Paths.get(CONFIG_FILENAME));
        JSONObject jsonObject = new JSONObject(content);
        JSONObject expressions = jsonObject.getJSONObject("xml_parsing_expressions");
        JSONArray useCases = expressions.getJSONArray("use_cases");
        JSONArray includeRelations = expressions.getJSONObject("relations").getJSONArray("include");
        JSONArray extendRelations = expressions.getJSONObject("relations").getJSONArray("extend");
        JSONArray generalizationRelations = expressions.getJSONObject("relations").getJSONArray("generalization");
        List<String> ucExpressionsList = new ArrayList<>();
        for (int i = 0; i < useCases.length(); i++) {
            ucExpressionsList.add(useCases.getString(i));
        }
        List<String> incExpressionsList = new ArrayList<>();
        for (int i = 0; i < includeRelations.length(); i++) {
            incExpressionsList.add(includeRelations.getString(i));
        }
        List<String> extExpressionsList = new ArrayList<>();
        for (int i = 0; i < extendRelations.length(); i++) {
            extExpressionsList.add(extendRelations.getString(i));
        }
        List<String> genExpressionsList = new ArrayList<>();
        for (int i = 0; i < generalizationRelations.length(); i++) {
            genExpressionsList.add(generalizationRelations.getString(i));
        }

        // get and save UseCases
        findUseCases(useCaseDiagram, doc, ucExpressionsList, incExpressionsList, extExpressionsList,
                genExpressionsList);
        loggerService.logInfo("XML parsed");
    }

    private void findUseCases(UseCaseDiagram useCaseDiagram, Document doc, List<String> ucExpressionsList,
                              List<String> incExpressionsList, List<String> extExpressionsList,
                              List<String> genExpressionsList) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();

        NodeList ucXmlElems = gatherXmlElems(doc, xPath, ucExpressionsList);
        var useCases = getUseCasesFromUcXmlElems(ucXmlElems);
        createUseCaseObjects(useCases, useCaseDiagram);

        var incXmlElems = gatherXmlElems(doc, xPath, incExpressionsList);
        var include = getIncludeFromIncXmlElems(incXmlElems, useCases);
        createRelationObjects(include, Relation.RelationType.INCLUDE, useCaseDiagram);

        var extXmlElems = gatherXmlElems(doc, xPath, extExpressionsList);
        var extend = getExtendFromExtXmlElems(extXmlElems, useCases);
        createRelationObjects(extend, Relation.RelationType.EXTEND, useCaseDiagram);

        var genXmlElems = gatherXmlElems(doc, xPath, genExpressionsList);
        var generalization = getGeneralizationFromGenXmlElems(genXmlElems, useCases);
        createRelationObjects(generalization, Relation.RelationType.GENERALIZATION, useCaseDiagram);

        addPrimIdxToRelation(useCaseDiagram);
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

    private List<Pair<String, String>> getGeneralizationFromGenXmlElems(NodeList genXmlElems,
                                                                        Map<String, String> useCases) {
        var generalization = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < genXmlElems.getLength(); i++) {
            Node elem = genXmlElems.item(i);
            var elementAttributes = getAttributesFromNamedNodeMap(elem.getAttributes());
            if (elementAttributes.containsKey("general") && elementAttributes.containsKey("specific")) {
                var from = useCases.get(elementAttributes.get("specific"));
                var to = useCases.get(elementAttributes.get("general"));
                generalization.add(new Pair<>(from, to));
            } else if (elementAttributes.containsKey("general")) {
                var parentElementAttributes = getAttributesFromNamedNodeMap(elem.getParentNode().getAttributes());
                var from = parentElementAttributes.get("name");
                var to = useCases.get(elementAttributes.get("general"));
                generalization.add(new Pair<>(from, to));
            }
            // TODO dodać inne modelery diagramów
        }
        return generalization;
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

    private void addPrimIdxToRelation(UseCaseDiagram useCaseDiagram) {
        List<Relation> relations = useCaseDiagram.getRelations();
        for (var uc : useCaseDiagram.getUseCaseList()) {
            List<Relation> matchedRelations = new ArrayList<>(relations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.INCLUDE && r.getToId().equals(uc.getId()))
                    .toList());
            matchedRelations.addAll(relations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.EXTEND && r.getFromId().equals(uc.getId()))
                    .toList());
            //TODO INHERIT
            for (int i = 0; i < matchedRelations.size(); i++) {
                matchedRelations.get(i).setPrimIdx(i + 1);
            }
        }
    }
}
