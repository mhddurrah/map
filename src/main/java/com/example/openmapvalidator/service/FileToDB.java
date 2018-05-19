package com.example.openmapvalidator.service;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

@Service
public class FileToDB {

    public void saveToDB() {

        //Model model = new Model();
        //model.setName("burak");

        //entityManager.persist(model);

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        String s = null;

        try {

            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            // create a file with the working directory we wish
            File dir = new ClassPathResource("map").getFile();

            Process p = Runtime.getRuntime().exec("osm2pgsql --create --database map-db pan.osm", null, dir);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            System.exit(0);
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }


        //select * from planet_osm_point p where public_transport IS NULL AND name IS NOT NULL;

        String nodeId = "319683569";

        final String uri = "http://api.openstreetmap.org/api/0.6/node/" + nodeId;

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        System.out.println(result);


    }

    private void makeCallForCoordinates(String nodeId) {

        final String uri = "http://api.openstreetmap.org/api/0.6/node/" + nodeId;

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        File tmpFile = null;

        try {
            tmpFile = File.createTempFile("test", ".xml");
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(result);
            writer.close();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(tmpFile);
                doc.getDocumentElement().normalize();
                System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nodeList = doc.getElementsByTagName("node");
                //now XML is loaded as Document in memory, lets convert it to Object List
                for (int i = 0; i < nodeList.getLength(); i++) {
                    NamedNodeMap map = nodeList.item(i).getAttributes();
                    String lat = map.getNamedItem("lat").getNodeValue();
                    String lon = map.getNamedItem("lon").getNodeValue();
                    

                }

            } catch (SAXException | ParserConfigurationException | IOException e1) {
                e1.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
