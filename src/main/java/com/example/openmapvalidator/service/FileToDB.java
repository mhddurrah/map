package com.example.openmapvalidator.service;

import com.example.openmapvalidator.helper.PlaceTypeMapper;
import com.example.openmapvalidator.model.GoogleResult;
import com.example.openmapvalidator.mybatis.PlaceDBModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileToDB {

    @Value( "${googlemap.radius}" )
    private int RADIUS;

    @Value( "${googlemap.key}" )
    private String GOOGLE_KEY;

    @Value("${openstreet.getlongwithosmid}")
    private String OPENSTREET_URI_GET_LONG_WITH_OSM_ID;

    @Value("${googlemap.searchplacewithlong}")
    private String GOOGLE_URI_SEARCH_WITH_LONG;

    @Value("${googlemap.retrievewithplaceid}")
    private String GOOGLE_RETRIEVE_WITH_PLACE_ID;

    private void saveToDB(String fileName) {

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        String s = null;

        try {

            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            // create a file with the working directory we wish
            File dir = new ClassPathResource("map").getFile();

            String command = "osm2pgsql --create --database map-db @FILE_NAME";
            command = command.replace("@FILE_NAME", fileName);

            Process p = Runtime.getRuntime().exec(command, null, dir);

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

        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }


    }

    public Map<String, Map<String, String>> saveAndCallForCoordinates() {

        Map<String, Map<String, String>> nameMap = new HashMap<>();


        try  {

            saveToDB("mapp.osm");

            String resource = "mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new
                    SqlSessionFactoryBuilder().build(inputStream);

            SqlSession session = sqlSessionFactory.openSession();
            List<PlaceDBModel> list = session.selectList("selectPlaces");

            for (PlaceDBModel a : list) {
                Map<String, Map<String, String>> temporaryNameMap = new HashMap<>();

                System.out.println("Id: " + a.getOsm_id() + " Name: " + a.getName());

                temporaryNameMap.putAll(makeCallForCoordinates(a));
                temporaryNameMap.putAll(nameMap);

                nameMap = temporaryNameMap;

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return nameMap;


    }

    private String getTypeFromDBObject(PlaceDBModel dbModel) {
        if (dbModel.getAmenity() != null) {
            return dbModel.getAmenity();
        }

        return dbModel.getShop();
    }

    private Map<String, Map<String, String>> makeCallForCoordinates(PlaceDBModel node) {

        Map<String, Map<String, String>> nameMap = new HashMap<>();

        String openStreetUriGet = OPENSTREET_URI_GET_LONG_WITH_OSM_ID.replace("@OSM_ID", node.getOsm_id());

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(openStreetUriGet, String.class);

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
                    String LAT = map.getNamedItem("lat").getNodeValue();
                    String LOG = map.getNamedItem("lon").getNodeValue();

                    String googleUriSearch = GOOGLE_URI_SEARCH_WITH_LONG.replace("@LAT", LAT);
                    googleUriSearch = googleUriSearch.replace("@LONG", LOG);
                    googleUriSearch = googleUriSearch.replace("@RADIUS", String.valueOf(RADIUS));

                    PlaceTypeMapper typeMapper = new PlaceTypeMapper();
                    String TYPE = typeMapper.getOpenToGoogle().get(getTypeFromDBObject(node));

                    googleUriSearch = googleUriSearch.replace("@TYPE", TYPE);
                    googleUriSearch = googleUriSearch.replace("@KEY", GOOGLE_KEY);


                    String googleResultStr = restTemplate.getForObject(
                            googleUriSearch, String.class);

                    System.out.println(googleResultStr);

                    ObjectMapper mapper = new ObjectMapper();
                    GoogleResult googleResult = mapper.readValue(googleResultStr, GoogleResult.class);

                    if (googleResult.getResults().isEmpty()) {
                        // no place add error to place
                        continue;
                    }

                    String PLACE_ID = googleResult.getResults().get(0).getPlace_id();
                    System.out.println(PLACE_ID); //John


                    String googleRetrieveWithPlace = GOOGLE_RETRIEVE_WITH_PLACE_ID.replace("@PLACE_ID", PLACE_ID);

                    googleRetrieveWithPlace = googleRetrieveWithPlace.replace("@KEY", GOOGLE_KEY);


                    String googlePlaceDetailStr = restTemplate.getForObject(
                            googleRetrieveWithPlace, String.class);


                    org.json.simple.JSONObject obj;
                    try {

                        JSONParser parser = new JSONParser();
                        obj = (org.json.simple.JSONObject) parser.parse(googlePlaceDetailStr);

                        org.json.simple.JSONObject resultObject = (JSONObject) obj.get("result");

                        String nameResultFromGooglePlace = null;
                        nameResultFromGooglePlace = (String) resultObject.get("name");
                        System.out.println();

                        System.out.println("*******COMPARE************");
                        System.out.println("openst -> " + node.getName());
                        System.out.println("googleMap -> " + nameResultFromGooglePlace);
                        System.out.println("compare -> " + nameResultFromGooglePlace.equals(node.getName()));
                        System.out.println("*********FINISH**************");
                        System.out.println();

                        if (!nameResultFromGooglePlace.equals(node.getName())) {
                            String lngLat = LAT + "," + LOG;

                            Map<String, String> mapOfNames = new HashMap<>();
                            mapOfNames.put("openstreet", node.getName());
                            mapOfNames.put("google", nameResultFromGooglePlace);
                            nameMap.put(lngLat, mapOfNames);
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            } catch (SAXException | ParserConfigurationException | IOException e1) {
                e1.printStackTrace();
            }

            System.out.println();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return nameMap;

    }

}
