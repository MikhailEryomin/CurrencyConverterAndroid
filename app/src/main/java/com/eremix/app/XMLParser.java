package com.eremix.app;

import android.content.Context;

import androidx.annotation.NonNull;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLParser {

    private final ExchangeRatesModel model;

    public XMLParser(ExchangeRatesModel model) {
        this.model = model;
    }

    //данный класс должен спарсить XML файл и записать необходимые данные через пробел в каждую строчку
    //Потом данные можно выгрузить из внутреннего приватного хранилища.
    //На этом полномочия этого класса окончены.
    public void parseXMLandSaveData(String xmlContent, @NonNull Context context, String fileName) {

        String lastCurrencyDate = "01.01.2000";

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlContent)));

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            Node root = doc.getDocumentElement();

            lastCurrencyDate = doc.getDocumentElement().getAttribute("Date");
            System.out.println("EEE PARSED! " + xmlContent);
            model.setLastCurrencyDate(lastCurrencyDate);

            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                StringBuilder rateData = new StringBuilder();
                double nominal = 1;

                NodeList id_node = nodeList.item(i).getChildNodes();
                for (int j = 0; j < id_node.getLength(); j++) {

                    Node node = id_node.item(j);
                    String node_name = node.getNodeName();


                    if (node_name.equals("CharCode")) {
                        rateData.append(node.getTextContent() + " ");
                    }
                    if (node_name.equals("Name")) {
                        rateData.append(node.getTextContent().replaceAll(" ", "  ") + " ");
                    }
                    if (node_name.equals("Nominal")) {
                        nominal = Double.parseDouble(node.getTextContent());
                        System.out.println("Nominal= " + nominal);
                    }
                    if (node_name.equals("Value")) {
                        double value = Double.parseDouble(node.getTextContent().replaceAll(",", ".")) / nominal;
                        rateData.append(value);
                        //System.out.println(value + " " + value / nominal);
                    }
                }
                fos.write(rateData.toString().getBytes());
                fos.write("\n".getBytes());
            }

            fos.close();

        } catch (ParserConfigurationException | IOException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

}
