/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import core.apiFramework.CSOOptimateObject;
import core.apiFramework.OptimateObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import optimate.PressureURFVariable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parameters.var.AbstractVariable;
import parameters.var.user.CustomScalarVariable;

/**
 *
 * @author aarong
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File temp = new File("/u/xeons24/people/aarong/testingdoc.xml");
        try {
            JAXBContext context = JAXBContext.newInstance(OptimateObject.class, CSOOptimateObject.class, PressureURFVariable.class,
                    AbstractVariable.class, CustomScalarVariable.class, Holder.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Holder holder = new Holder();
            PressureURFVariable var1 = new PressureURFVariable();
            PressureURFVariable var2 = new PressureURFVariable();
            holder._data.add(var1);
            holder._data.add(var2);

            HashMap<String, OptimateObject> map = new HashMap<String, OptimateObject>();
            map.put(getID(var1), var1);
            map.put(getID(var2), var2);

            m.marshal(holder, System.out);
            m.marshal(holder, new FileOutputStream(temp));

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(temp);

            NodeList nodes = doc.getElementsByTagName("_data");

            int len = nodes.getLength();

            for (int i = 0; i < len; i++) {

                NodeList list = nodes.item(i).getChildNodes();

                for (int j = 0; j < list.getLength(); j++) {
                    Node node = list.item(j);

                    if (node.getNodeName().equals("ID")) {
                        for (Entry<String, OptimateObject> entry : map.entrySet()) {
                            if (node.getTextContent().equals(entry.getKey())) {
                                NamedNodeMap attr = nodes.item(i).getAttributes();
                                Node nodeAttr = attr.getNamedItem("xsi:type");
                                System.out.println(nodeAttr.getTextContent());
                                nodeAttr.setTextContent(updateType(entry.getValue()));
                            }
                        }
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(temp);
            transformer.transform(source, result);

        } catch (JAXBException ex) {
            printStackTrace(ex);
        } catch (FileNotFoundException ex) {
            printStackTrace(ex);
        } catch (ParserConfigurationException ex) {
            printStackTrace(ex);
        } catch (SAXException ex) {
            printStackTrace(ex);
        } catch (IOException ex) {
            printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            printStackTrace(ex);
        } catch (TransformerConfigurationException ex) {
            printStackTrace(ex);
        } catch (TransformerException ex) {
            printStackTrace(ex);
        } catch (NoSuchFieldException ex) {
            printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            printStackTrace(ex);
        }

    }

    static void printStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        System.out.println(sw.toString());
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class Holder {

        ArrayList<AbstractVariable> _data;

        public Holder() {
            _data = new ArrayList<AbstractVariable>();
        }
    }

    static String getID(OptimateObject oo) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class clazz = OptimateObject.class;
        Field field = clazz.getDeclaredField("ID");
        field.setAccessible(true);
        return (String) field.get(oo);
    }

    static String updateType(OptimateObject oo) {
        Class clazz = oo.getClass();
        String type = clazz.getSimpleName();
        System.out.println("SimpleName: " + type);
       
        if (clazz.isAnnotationPresent(XmlType.class)) {
            type = ((XmlType) clazz.getAnnotation(XmlType.class)).name();
        } else {
            char[] stringArray = type.trim().toCharArray();
            stringArray[0] = Character.toLowerCase(stringArray[0]);
            type = new String(stringArray);
        }
        
        System.out.println("xsi:type " + type);
        return type;
    }

}
