package sl.fside.services;

import com.google.inject.*;
import org.junit.jupiter.api.*;
import sl.fside.*;
import sl.fside.factories.*;
import sl.fside.model.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserService2Test {
    private final Injector injector = Guice.createInjector(new MainModule());
    private final XmlParserService2 xmlParserService2 = injector.getInstance(XmlParserService2.class);
    private final ModelFactory modelFactory = injector.getInstance(ModelFactory.class);

    @Test
    void parseXmlShop() {
        var project = modelFactory.createProject("test1");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/1. GenMyModel/shop.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var uc : useCaseList) {
            var useCaseName = uc.getUseCaseName();
            var useCasePrettyName = uc.getUseCasePrettyName();
            var relations = uc.getRelations();
            switch (useCaseName) {
                case "ship_order" -> {
                    assertEquals(useCasePrettyName, "Ship Order");
                    assertEquals(relations.size(), 0);
                }
                case "create_order" -> {
                    assertEquals(useCasePrettyName, "Create Order");
                    assertEquals(relations.size(), 0);
                }
                case "update_items" -> {
                    assertEquals(useCasePrettyName, "Update Items");
                    assertEquals(relations.size(), 0);
                }
                case "register_details" -> {
                    assertEquals(useCasePrettyName, "Register Details");
                    assertEquals(relations.size(), 0);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(useCaseDiagram.getUseCaseList().size(), 4);
        var expectedResult = "{ship_order={EXTEND=[], INCLUDE=[], NAME=[Ship Order]}, create_order={EXTEND=[], INCLUDE=[], NAME=[Create Order]}, update_items={EXTEND=[], INCLUDE=[], NAME=[Update Items]}, register_details={EXTEND=[], INCLUDE=[], NAME=[Register Details]}}";
        assert useCaseDiagram.getUseCasesRaw() != null;
        assertEquals(expectedResult, useCaseDiagram.getUseCasesRaw().toString());
    }

    @Test
    void parseXmlShopExt() {
        var project = modelFactory.createProject("test2");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/1. GenMyModel/shop_ext.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var uc : useCaseList) {
            var useCaseName = uc.getUseCaseName();
            var useCasePrettyName = uc.getUseCasePrettyName();
            var relations = uc.getRelations();
            switch (useCaseName) {
                case "ship_order" -> {
                    assertEquals(useCasePrettyName, "Ship Order");
                    assertEquals(relations.size(), 1);
                    for (var r : relations.entrySet()) {
                        UUID ucInRelationId = r.getKey();
                        String ucInRelationName = useCaseList.stream().filter(x -> x.getId() == ucInRelationId).findFirst().orElseThrow().getUseCaseName();
                        assertEquals(r.getValue().get(0), UseCase.RelationEnum.EXTEND);
                        assertEquals(ucInRelationName, "register_details");
                    }
                }
                case "create_order" -> {
                    assertEquals(useCasePrettyName, "Create Order");
                    assertEquals(relations.size(), 0);
                }
                case "update_items" -> {
                    assertEquals(useCasePrettyName, "Update Items");
                    assertEquals(relations.size(), 0);
                }
                case "register_details" -> {
                    assertEquals(useCasePrettyName, "Register Details");
                    assertEquals(relations.size(), 1);
                    for (var r : relations.entrySet()) {
                        UUID ucInRelationId = r.getKey();
                        String ucInRelationName = useCaseList.stream().filter(x -> x.getId() == ucInRelationId).findFirst().orElseThrow().getUseCaseName();
                        assertEquals(r.getValue().get(0), UseCase.RelationEnum.INCLUDE);
                        assertEquals(ucInRelationName, "create_order");
                    }
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(useCaseDiagram.getUseCaseList().size(), 4);
        var expectedResult = "{ship_order={EXTEND=[register_details], INCLUDE=[], NAME=[Ship Order]}, create_order={EXTEND=[], INCLUDE=[], NAME=[Create Order]}, update_items={EXTEND=[], INCLUDE=[], NAME=[Update Items]}, register_details={EXTEND=[], INCLUDE=[create_order], NAME=[Register Details]}}";
        assert useCaseDiagram.getUseCasesRaw() != null;
        assertEquals(expectedResult, useCaseDiagram.getUseCasesRaw().toString());
    }
}