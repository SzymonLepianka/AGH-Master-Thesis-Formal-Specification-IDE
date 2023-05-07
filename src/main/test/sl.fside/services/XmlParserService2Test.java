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

    @Test
    void parseXmlBank() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/2. Papyrus/Bank.xml"));
        var expectedResult = "{wpłata_pieniedzy={EXTEND=[], INCLUDE=[], NAME=[Wpłata pieniedzy]}, wypłata_pieniędzy={EXTEND=[], INCLUDE=[], NAME=[Wypłata pieniędzy]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlBankExtend() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/2. Papyrus/Bank_extend.xml"));
        var expectedResult = "{usecase1={EXTEND=[], INCLUDE=[usecase2], NAME=[UseCase1]}, usecase2={EXTEND=[usecase3], INCLUDE=[], NAME=[UseCase2]}, usecase3={EXTEND=[], INCLUDE=[], NAME=[UseCase3]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlVpEx1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/3. Visual Paradigm/ex1/project1.xml"));
        var expectedResult = "{invest_money={EXTEND=[], INCLUDE=[], NAME=[Invest Money]}, manage_accounts={EXTEND=[], INCLUDE=[], NAME=[Manage Accounts]}, loan_money={EXTEND=[], INCLUDE=[], NAME=[Loan Money]}, manage_credit_account={EXTEND=[], INCLUDE=[], NAME=[Manage Credit Account]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlVpEx2() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/3. Visual Paradigm/ex2/project2.xml"));
        var expectedResult = "{repair={EXTEND=[], INCLUDE=[], NAME=[Repair]}, transfer_funds={EXTEND=[], INCLUDE=[], NAME=[Transfer Funds]}, deposit_funds={EXTEND=[], INCLUDE=[], NAME=[Deposit Funds]}, withdraw_cash={EXTEND=[], INCLUDE=[], NAME=[Withdraw Cash]}, maintenance={EXTEND=[], INCLUDE=[], NAME=[Maintenance]}, check_balances={EXTEND=[], INCLUDE=[], NAME=[Check Balances]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlVpEx3() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/3. Visual Paradigm/ex3/project3_vp_simple_xml_format.xml"));
        var expectedResult = "{evaluate_software_components={EXTEND=[], INCLUDE=[], NAME=[Evaluate Software Components]}, identify_software_requirements={EXTEND=[], INCLUDE=[], NAME=[Identify Software Requirements]}, define_software_deliverables={EXTEND=[], INCLUDE=[], NAME=[Define Software Deliverables]}, plan_software_production={EXTEND=[], INCLUDE=[], NAME=[Plan Software Production]}, evaluate_software_product={EXTEND=[], INCLUDE=[], NAME=[Evaluate Software Product]}, identify_software_components={EXTEND=[], INCLUDE=[], NAME=[Identify Software Components]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlVpEx3TraditionalXml() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/3. Visual Paradigm/ex3_traditional_xml/project_3_vp_traditional_xml_format.xml"));
        var expectedResult = "{evaluate_software_components={EXTEND=[], INCLUDE=[], NAME=[Evaluate Software Components]}, identify_software_requirements={EXTEND=[], INCLUDE=[], NAME=[Identify Software Requirements]}, define_software_deliverables={EXTEND=[], INCLUDE=[], NAME=[Define Software Deliverables]}, plan_software_production={EXTEND=[], INCLUDE=[], NAME=[Plan Software Production]}, evaluate_software_product={EXTEND=[], INCLUDE=[], NAME=[Evaluate Software Product]}, identify_software_components={EXTEND=[], INCLUDE=[], NAME=[Identify Software Components]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlVpExtendInclude() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/3. Visual Paradigm/extend_include/project4.xml"));
        var expectedResult = "{transfer_money={EXTEND=[], INCLUDE=[login], NAME=[Transfer Money]}, pay_bills={EXTEND=[], INCLUDE=[login], NAME=[Pay Bills]}, donate_money_to_charity={EXTEND=[], INCLUDE=[login], NAME=[Donate Money to Charity]}, handle_abort={EXTEND=[login], INCLUDE=[], NAME=[Handle Abort]}, withdraw_cash={EXTEND=[], INCLUDE=[login], NAME=[Withdraw Cash]}, handle_invalid_password={EXTEND=[login], INCLUDE=[], NAME=[Handle Invalid Password]}, login={EXTEND=[], INCLUDE=[], NAME=[Login]}, check_balance={EXTEND=[], INCLUDE=[login], NAME=[Check Balance]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx0() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex0_xmi_1_0_uml_1_3/mobile_uml_1_3_XML_1_0.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex1_xml_1_1_uml_1_3/mobile_uml_1_3_XML_1_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx2_1_4_XML_1_2() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex2_xmi_1_2_uml_1_4/mobile_uml_1_4_XML_1_2.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx2_2_0_XML_2_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex2_xmi_1_2_uml_1_4/mobile_uml_2_0_XML_2_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_1_1_XML_2_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_1_1_XML_2_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_1_2_XML_2_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_1_2_XML_2_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_1_XML_2_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_1_XML_2_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_2_XML_2_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_2_XML_2_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_3_XML_2_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_3_XML_2_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_4_1_XML_2_4_2() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_4_1_XML_2_4_2.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_4_XML_2_4() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_4_XML_2_4.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_5_1_XML_2_5_1() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_5_1_XML_2_5_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlEaEx3_2_5_XML_2_5() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/4.Enterprise Architect/ex3_xmi_2_1_uml_2_1/mobile_uml_2_5_XML_2_5_1.xml"));
        var expectedResult = "{request_a_tablet={EXTEND=[], INCLUDE=[], NAME=[Request a tablet]}, request_a_smart_phone={EXTEND=[], INCLUDE=[], NAME=[Request a smart phone]}, request_a_mobile_device={EXTEND=[], INCLUDE=[], NAME=[Request a mobile device]}, view_list_of_mobile_devices_to_approve={EXTEND=[], INCLUDE=[], NAME=[View list of mobile devices to approve]}, approve_a_mobile_device_reques={EXTEND=[], INCLUDE=[], NAME=[Approve a mobile device reques]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }

    @Test
    void parseXmlSinvas() {
        var project = modelFactory.createProject("test3");
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());
        var useCases = xmlParserService2.parseXml2(useCaseDiagram, new File("xml_and_png_examples/5.Sinvas/library.xml"));
        var expectedResult = "{register_book_return={EXTEND=[], INCLUDE=[], NAME=[Register book return]}, register_book_loan={EXTEND=[], INCLUDE=[], NAME=[Register book loan]}, add_new_book={EXTEND=[], INCLUDE=[], NAME=[Add new book]}, query_book_availability={EXTEND=[], INCLUDE=[], NAME=[Query book availability]}}";
        assert useCases != null;
        assertEquals(expectedResult, useCases.toString());
    }
}