package gov.nih.nci.cabig.caaers.rules.common;

import gov.nih.nci.cabig.caaers.rules.brxml.Rule;
import gov.nih.nci.cabig.caaers.rules.exception.RuleException;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

/*import org.jibx.runtime.BindingDirectory;
 import org.jibx.runtime.IBindingFactory;
 import org.jibx.runtime.IMarshallingContext;
 import org.jibx.runtime.IUnmarshallingContext;
 import org.jibx.runtime.JiBXException;

 import com.thoughtworks.xstream.XStream;
 import com.thoughtworks.xstream.io.xml.DomDriver;*/

/**
 * 
 * @author Sujith Vellat Thayyilthodi
 * */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.rule.Package;

public class XMLUtil {
    private static final Log log = LogFactory.getLog(XMLUtil.class);

    public static Object unmarshal(String xml) {
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(
                            "gov.nih.nci.cabig.caaers.rules.brxml").createUnmarshaller();
            log.debug("reading the rule:" + xml);
            return unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new RuleException(e.getMessage(), e);
        }
    }

    public static String marshal(Object object) {
        StringWriter writer = new StringWriter();
        try {
            Marshaller marshaller = JAXBContext.newInstance("gov.nih.nci.cabig.caaers.rules.brxml")
                            .createMarshaller();
            marshaller.marshal(object, writer);
            log.debug("Before writing:" + writer.toString());
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuleException(e.getMessage(), e);
        }
    }

    /**
     * This utility method will convert the rule-xml to a Drools Package object
     * 
     * @param xml
     * @return
     */
    public static Package unmarshalToPackage(String xml) {
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        conf.setCompiler(PackageBuilderConfiguration.ECLIPSE);
        conf.setJavaLanguageLevel("1.5");
        Package droolsPackage = new Package();

        // merge the rule xml into the package
        try {
            Reader ruleReader = null;
            ruleReader = new StringReader(xml);
            PackageBuilder packageBuilder = new PackageBuilder(conf);
            packageBuilder.addPackageFromXml(ruleReader);
            droolsPackage = packageBuilder.getPackage();
        } catch (Exception e) {
            log.error("Error while converting xml form of rules into package\r\n XML rules :\r\n"
                            + xml, e);
            throw new RuleException(e.getMessage(), e);
        }
        return droolsPackage;
    }

    /*
     * public static String marshal(Rule rule) { StringWriter writer = new StringWriter(); try {
     * Marshaller marshaller =
     * JAXBContext.newInstance("gov.nih.nci.cabig.caaers.rules.brxml").createMarshaller();
     * marshaller.marshal( new JAXBElement( new
     * QName("http://caaers.cabig.nci.nih.gov/rules/brxml","rule"), Rule.class, rule ), writer);
     * return writer.toString(); } catch (JAXBException e) { throw new RuleException(e.getMessage(),
     * e); } }
     */

    /*
     * //REFERENCE THIS IF ANY DAY YOU WANT TO USE JIBX AS MECHANISM FOR DATABINDING public static
     * String toXML(Object rootElement) { IMarshallingContext mctx = null; StringWriter writer = new
     * StringWriter(); try { IBindingFactory bfact = BindingDirectory
     * .getFactory(rootElement.getClass()); mctx = bfact.createMarshallingContext();
     * mctx.setOutput(writer); mctx.marshalDocument(rootElement); } catch (JiBXException e) { throw
     * new RuleException(e.getMessage(), e); } //String returnString = "<?xml version=\"1.0\"
     * encoding=\"UTF-8\"?>" + writer.toString(); //return "<?xml version=\"1.0\"
     * encoding=\"UTF-8\"?>" + writer.toString(); return writer.toString(); }
     * 
     * public static Object toObject(String xml){ IUnmarshallingContext uctx = null; Object obj =
     * null; try { obj = uctx.unmarshalDocument(new StringReader(xml)); } catch (JiBXException e) {
     * throw new RuleException(e.getMessage(), e); } return obj; }
     * 
     * //REFERENCE THIS IF ANY DAY YOU WANT TO USE XSTREAM AS MECHANISM FOR DATABINDING
     * 
     * public static String getXML(Object obj) { XStream xstream = new XStream(new DomDriver());
     * updateAliases(xstream); //xstream.aliasField("testField", Test.class, "test"); return
     * xstream.toXML(obj); }
     * 
     * public static Object getObject(String xml) { XStream xstream = new XStream(new DomDriver());
     * updateAliases(xstream); //xstream.aliasField("testField", Test.class, "test"); return
     * xstream.fromXML(xml); }
     * 
     * private static void updateAliases(XStream xstream) { xstream.alias("ruleSet",
     * gov.nih.nci.cabig.caaers.rules.brxml.RuleSet.class); xstream.alias("rule",
     * gov.nih.nci.cabig.caaers.rules.brxml.Rule.class); xstream.alias("category",
     * gov.nih.nci.cabig.caaers.rules.brxml.Category.class); xstream.alias("ruleAttribute",
     * gov.nih.nci.cabig.caaers.rules.brxml.RuleAttribute.class); xstream.alias("metaData",
     * gov.nih.nci.cabig.caaers.rules.brxml.MetaData.class); xstream.alias("action",
     * gov.nih.nci.cabig.caaers.rules.brxml.Action.class); xstream.alias("column",
     * gov.nih.nci.cabig.caaers.rules.brxml.Column.class); }
     */

}
