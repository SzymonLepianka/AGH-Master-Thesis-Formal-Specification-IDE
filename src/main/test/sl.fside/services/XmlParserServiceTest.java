package sl.fside.services;

import com.google.inject.*;
import org.junit.jupiter.api.*;
import sl.fside.*;
import sl.fside.factories.*;
import sl.fside.model.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserServiceTest {
    private final Injector injector = Guice.createInjector(new MainModule());
    private final XmlParserService xmlParserService = injector.getInstance(XmlParserService.class);
    private final ModelFactory modelFactory = injector.getInstance(ModelFactory.class);

    @Test
    void parseXmlShop() {
        var project = modelFactory.createProject("test1");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File("xml_and_png_examples/1. GenMyModel/shop.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "ship_order" -> {
                    assertEquals("Ship Order", useCasePrettyName);
                }
                case "create_order" -> {
                    assertEquals("Create Order", useCasePrettyName);
                }
                case "update_items" -> {
                    assertEquals("Update Items", useCasePrettyName);
                }
                case "register_details" -> {
                    assertEquals("Register Details", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(4, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlShopExt() {
        var project = modelFactory.createProject("test2");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File("xml_and_png_examples/1. GenMyModel/shop_ext.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "ship_order" -> {
                    assertEquals("Ship Order", useCasePrettyName);
                }
                case "create_order" -> {
                    assertEquals("Create Order", useCasePrettyName);
                }
                case "update_items" -> {
                    assertEquals("Update Items", useCasePrettyName);
                }
                case "register_details" -> {
                    assertEquals("Register Details", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(4, useCaseDiagram.getUseCaseList().size());

        var relationsList = useCaseDiagram.getRelations();
        for (var relation : relationsList) {
            var actualUseCaseFrom =
                    useCaseList.stream().filter(uc -> uc.getId().equals(relation.getFromId())).findFirst().orElseThrow()
                            .getUseCasePrettyName();
            var actualUseCaseTo =
                    useCaseList.stream().filter(uc -> uc.getId().equals(relation.getToId())).findFirst().orElseThrow()
                            .getUseCasePrettyName();
            Relation.RelationType useCasePrettyType = relation.getType();
            switch (useCasePrettyType) {
                case INCLUDE -> {
                    assertEquals("Register Details", actualUseCaseFrom);
                    assertEquals("Create Order", actualUseCaseTo);
                }
                case EXTEND -> {
                    assertEquals("Ship Order", actualUseCaseFrom);
                    assertEquals("Register Details", actualUseCaseTo);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(2, useCaseDiagram.getRelations().size());
        assertEquals(1,
                useCaseDiagram.getRelations().stream().filter(r -> r.getType().equals(Relation.RelationType.EXTEND))
                        .count());
        assertEquals(1,
                useCaseDiagram.getRelations().stream().filter(r -> r.getType().equals(Relation.RelationType.INCLUDE))
                        .count());
    }

    @Test
    void parseXmlBank() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File("xml_and_png_examples/2. Papyrus/Bank.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "wpłata_pieniedzy" -> {
                    assertEquals("Wpłata pieniedzy", useCasePrettyName);
                }
                case "wypłata_pieniędzy" -> {
                    assertEquals("Wypłata pieniędzy", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(2, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlBankExtend() {
        var project = modelFactory.createProject("test4");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File("xml_and_png_examples/2. Papyrus/Bank_extend.xml"));

        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "usecase1" -> {
                    assertEquals("UseCase1", useCasePrettyName);
                }
                case "usecase2" -> {
                    assertEquals("UseCase2", useCasePrettyName);
                }
                case "usecase3" -> {
                    assertEquals("UseCase3", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(3, useCaseDiagram.getUseCaseList().size());

        var relationsList = useCaseDiagram.getRelations();
        for (var relation : relationsList) {
            var actualUseCaseFrom =
                    useCaseList.stream().filter(uc -> uc.getId().equals(relation.getFromId())).findFirst().orElseThrow()
                            .getUseCasePrettyName();
            var actualUseCaseTo =
                    useCaseList.stream().filter(uc -> uc.getId().equals(relation.getToId())).findFirst().orElseThrow()
                            .getUseCasePrettyName();
            Relation.RelationType useCasePrettyType = relation.getType();
            switch (useCasePrettyType) {
                case INCLUDE -> {
                    assertEquals("UseCase1", actualUseCaseFrom);
                    assertEquals("UseCase2", actualUseCaseTo);
                }
                case EXTEND -> {
                    assertEquals("UseCase2", actualUseCaseFrom);
                    assertEquals("UseCase3", actualUseCaseTo);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(2, useCaseDiagram.getRelations().size());
        assertEquals(1,
                useCaseDiagram.getRelations().stream().filter(r -> r.getType().equals(Relation.RelationType.EXTEND))
                        .count());
        assertEquals(1,
                useCaseDiagram.getRelations().stream().filter(r -> r.getType().equals(Relation.RelationType.INCLUDE))
                        .count());
    }

    @Test
    void parseXmlVpEx1() {
        var project = modelFactory.createProject("test5");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/3. Visual Paradigm/ex1/project1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "invest_money" -> {
                    assertEquals("Invest Money", useCasePrettyName);
                }
                case "manage_accounts" -> {
                    assertEquals("Manage Accounts", useCasePrettyName);
                }
                case "loan_money" -> {
                    assertEquals("Loan Money", useCasePrettyName);
                }
                case "manage_credit_account" -> {
                    assertEquals("Manage Credit Account", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(4, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlVpEx2() {
        var project = modelFactory.createProject("test6");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/3. Visual Paradigm/ex2/project2.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "repair" -> {
                    assertEquals("Repair", useCasePrettyName);
                }
                case "transfer_funds" -> {
                    assertEquals("Transfer Funds", useCasePrettyName);
                }
                case "deposit_funds" -> {
                    assertEquals("Deposit Funds", useCasePrettyName);
                }
                case "withdraw_cash" -> {
                    assertEquals("Withdraw Cash", useCasePrettyName);
                }
                case "maintenance" -> {
                    assertEquals("Maintenance", useCasePrettyName);
                }
                case "check_balances" -> {
                    assertEquals("Check Balances", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(6, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlVpEx3() {
        var project = modelFactory.createProject("test7");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/3. Visual Paradigm/ex3/project3_vp_simple_xml_format.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "evaluate_software_components" -> {
                    assertEquals("Evaluate Software Components", useCasePrettyName);
                }
                case "identify_software_requirements" -> {
                    assertEquals("Identify Software Requirements", useCasePrettyName);
                }
                case "define_software_deliverables" -> {
                    assertEquals("Define Software Deliverables", useCasePrettyName);
                }
                case "plan_software_production" -> {
                    assertEquals("Plan Software Production", useCasePrettyName);
                }
                case "evaluate_software_product" -> {
                    assertEquals("Evaluate Software Product", useCasePrettyName);
                }
                case "identify_software_components" -> {
                    assertEquals("Identify Software Components", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(6, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlVpEx3TraditionalXml() {
        var project = modelFactory.createProject("test8");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File(
                "xml_and_png_examples/3. Visual Paradigm/ex3_traditional_xml/project_3_vp_traditional_xml_format.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "evaluate_software_components" -> {
                    assertEquals("Evaluate Software Components", useCasePrettyName);
                }
                case "identify_software_requirements" -> {
                    assertEquals("Identify Software Requirements", useCasePrettyName);
                }
                case "define_software_deliverables" -> {
                    assertEquals("Define Software Deliverables", useCasePrettyName);
                }
                case "plan_software_production" -> {
                    assertEquals("Plan Software Production", useCasePrettyName);
                }
                case "evaluate_software_product" -> {
                    assertEquals("Evaluate Software Product", useCasePrettyName);
                }
                case "identify_software_components" -> {
                    assertEquals("Identify Software Components", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(6, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlVpExtendInclude() {
        var project = modelFactory.createProject("test9");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/3. Visual Paradigm/extend_include/project4.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "transfer_money" -> {
                    assertEquals("Transfer Money", useCasePrettyName);
                }
                case "pay_bills" -> {
                    assertEquals("Pay Bills", useCasePrettyName);
                }
                case "donate_money_to_charity" -> {
                    assertEquals("Donate Money to Charity", useCasePrettyName);
                }
                case "handle_abort" -> {
                    assertEquals("Handle Abort", useCasePrettyName);
                }
                case "withdraw_cash" -> {
                    assertEquals("Withdraw Cash", useCasePrettyName);
                }
                case "handle_invalid_password" -> {
                    assertEquals("Handle Invalid Password", useCasePrettyName);
                }
                case "login" -> {
                    assertEquals("Login", useCasePrettyName);
                }
                case "check_balance" -> {
                    assertEquals("Check Balance", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(8, useCaseDiagram.getUseCaseList().size());

        var relationsList = useCaseDiagram.getRelations();
        for (var relation : relationsList) {
            var actualUseCaseFrom =
                    useCaseList.stream().filter(uc -> uc.getId().equals(relation.getFromId())).findFirst().orElseThrow()
                            .getUseCasePrettyName();
            var actualUseCaseTo =
                    useCaseList.stream().filter(uc -> uc.getId().equals(relation.getToId())).findFirst().orElseThrow()
                            .getUseCasePrettyName();
            Relation.RelationType useCasePrettyType = relation.getType();
            switch (useCasePrettyType) {
                case INCLUDE -> {
                    switch (actualUseCaseFrom) {
                        case "Transfer Money", "Pay Bills", "Donate Money to Charity", "Withdraw Cash", "Check Balance" -> {
                        }
                        default -> assertEquals(0, 1);
                    }
                    assertEquals("Login", actualUseCaseTo);
                }
                case EXTEND -> {
                    switch (actualUseCaseFrom) {
                        case "Handle Invalid Password", "Handle Abort" -> {
                        }
                        default -> assertEquals(0, 1);
                    }
                    assertEquals("Login", actualUseCaseTo);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(7, useCaseDiagram.getRelations().size());
        assertEquals(2,
                useCaseDiagram.getRelations().stream().filter(r -> r.getType().equals(Relation.RelationType.EXTEND))
                        .count());
        assertEquals(5,
                useCaseDiagram.getRelations().stream().filter(r -> r.getType().equals(Relation.RelationType.INCLUDE))
                        .count());
    }

    @Test
    void parseXmlEaEx0() {
        var project = modelFactory.createProject("test10");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex0_xmi_1_0_uml_1_3/mobile_uml_1_3_XML_1_0.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx1() {
        var project = modelFactory.createProject("test11");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex1_xml_1_1_uml_1_3/mobile_uml_1_3_XML_1_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx2_1_4_XML_1_2() {
        var project = modelFactory.createProject("test12");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex2_xmi_1_2_uml_1_4/mobile_uml_1_4_XML_1_2.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx2_2_0_XML_2_1() {
        var project = modelFactory.createProject("test13");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex2_xmi_1_2_uml_1_4/mobile_uml_2_0_XML_2_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_1_1_XML_2_1() {
        var project = modelFactory.createProject("test14");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File(
                "xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_1_1_XML_2_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_1_2_XML_2_1() {
        var project = modelFactory.createProject("test15");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File(
                "xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_1_2_XML_2_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_1_XML_2_1() {
        var project = modelFactory.createProject("test16");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_1_XML_2_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_2_XML_2_1() {
        var project = modelFactory.createProject("test17");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_2_XML_2_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_3_XML_2_1() {
        var project = modelFactory.createProject("test18");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_3_XML_2_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_4_1_XML_2_4_2() {
        var project = modelFactory.createProject("test19");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File(
                "xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_4_1_XML_2_4_2.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_4_XML_2_4() {
        var project = modelFactory.createProject("test20");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram,
                new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_4_XML_2_4.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_5_1_XML_2_5_1() {
        var project = modelFactory.createProject("test21");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File(
                "xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_5_1_XML_2_5_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlEaEx3_2_5_XML_2_5() {
        var project = modelFactory.createProject("test22");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File(
                "xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_5_XML_2_5_1.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "request_a_tablet" -> {
                    assertEquals("Request a tablet", useCasePrettyName);
                }
                case "request_a_smart_phone" -> {
                    assertEquals("Request a smart phone", useCasePrettyName);
                }
                case "request_a_mobile_device" -> {
                    assertEquals("Request a mobile device", useCasePrettyName);
                }
                case "view_list_of_mobile_devices_to_approve" -> {
                    assertEquals("View list of mobile devices to approve", useCasePrettyName);
                }
                case "approve_a_mobile_device_reques" -> {
                    assertEquals("Approve a mobile device reques", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(5, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }

    @Test
    void parseXmlSinvas() {
        var project = modelFactory.createProject("test23");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        xmlParserService.parseXml(useCaseDiagram, new File("xml_and_png_examples/5.Sinvas/library.xml"));
        var useCaseList = useCaseDiagram.getUseCaseList();
        for (var useCase : useCaseList) {
            var useCaseName = useCase.getUseCaseName();
            var useCasePrettyName = useCase.getUseCasePrettyName();
            switch (useCaseName) {
                case "register_book_return" -> {
                    assertEquals("Register book return", useCasePrettyName);
                }
                case "register_book_loan" -> {
                    assertEquals("Register book loan", useCasePrettyName);
                }
                case "add_new_book" -> {
                    assertEquals("Add new book", useCasePrettyName);
                }
                case "query_book_availability" -> {
                    assertEquals("Query book availability", useCasePrettyName);
                }
                default -> assertEquals(0, 1);
            }
        }
        assertEquals(4, useCaseDiagram.getUseCaseList().size());
        assertTrue(useCaseDiagram.getRelations().isEmpty());
    }
}