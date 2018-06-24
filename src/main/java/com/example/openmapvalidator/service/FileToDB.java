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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.Executors;

@Service
public class FileToDB {

    private static final Logger logger = LoggerFactory.getLogger(FileToDB.class);

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

    @Autowired
    private RestTemplate restTemplate;


    private void osmFileToDB(String fileName) {

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        try {

            ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                //builder.command("osm2pgsql -c -d map-db -U postgres -S " +
                      //  "C:\\Users\\Yusuf\\Desktop\\Sanan\\Projects\\osm2pgsql-bin\\default.style @FILE_NAME");
                builder.command("osm2pgsql", "-c", "-d", "map-db", "-S",
                        "default.style",
                        fileName);
            } else {
                builder.command("osm2pgsql", "--create", "--database", "map-db", fileName);
            }

            builder.directory(new ClassPathResource("map").getFile());
            Process process = builder.start();
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;

            //p.destroy();
        } catch (IOException e) {
            logger.debug("osm command execute exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static String output(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();

        }
        return sb.toString();

    }



    private SqlSession getDBSession() throws IOException {
        String resource = "mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new
                SqlSessionFactoryBuilder().build(inputStream);

        return sqlSessionFactory.openSession();
    }

    public Map<String, Map<String, String>> saveAndCallForPlaceCoordinates() {

        Map<String, Map<String, String>> nameMap = new HashMap<>();

        logger.debug("place data, openstreet from db");

        try  {

            osmFileToDB("map.osm");

            SqlSession session = getDBSession();
            List<PlaceDBModel> list = session.selectList("selectPlaces");

            for (PlaceDBModel model : list) {
                Map<String, Map<String, String>> temporaryNameMap = new HashMap<>();

                logger.debug("Id: " + model.getOsm_id() + " Name: " + model.getName());

                temporaryNameMap.putAll(makeApiCallForPlaceToCompare(model));
                temporaryNameMap.putAll(nameMap);

                nameMap = temporaryNameMap;

            }

            logger.debug("");

            session.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return nameMap;


    }

    /**
     * place type could be either amenity or shop attribute if amenity is not null it has priority
     *
     * @param dbModel
     * @return
     */
    private String getPlaceTypeFromDBObject(PlaceDBModel dbModel) {
        if (dbModel.getAmenity() != null) {
            return dbModel.getAmenity();
        }

        return dbModel.getShop();
    }

    /**
     * Get latitude and longitude of a place which is stored in db retrieve with osm_id
     *
     * @param node
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private Map<String, String> makeOpenStreetApiCallWithOSMID(PlaceDBModel node) throws IOException, SAXException,
            ParserConfigurationException {

        Map<String, String> longAndLatMap = new HashMap<>();

        String openStreetUriGet = OPENSTREET_URI_GET_LONG_WITH_OSM_ID.replace("@OSM_ID", node.getOsm_id());

        String result = restTemplate.getForObject(openStreetUriGet, String.class);

        File tmpFile = File.createTempFile("test", ".xml");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(result);
        writer.close();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(tmpFile);
        doc.getDocumentElement().normalize();
        logger.debug("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nodeList = doc.getElementsByTagName("node");
        //now XML is loaded as Document in memory, lets convert it to Object List
        NamedNodeMap map = nodeList.item(0).getAttributes();
        String LAT = map.getNamedItem("lat").getNodeValue();
        String LOG = map.getNamedItem("lon").getNodeValue();

        longAndLatMap.put("lat", LAT);
        longAndLatMap.put("log", LOG);


        return longAndLatMap;

    }

    /**
     *
     * get google places result with latitude and longitude type of a place is mapped from openstreet
     *
     * @param lat
     * @param log
     * @param type
     * @return
     * @throws IOException
     */
    private GoogleResult makeGooglePlaceApiCall(String lat, String log, String type) throws IOException {

        String googleUriSearch = GOOGLE_URI_SEARCH_WITH_LONG.replace("@LAT", lat);
        googleUriSearch = googleUriSearch.replace("@LONG", log);
        googleUriSearch = googleUriSearch.replace("@RADIUS", String.valueOf(RADIUS));


        googleUriSearch = googleUriSearch.replace("@TYPE", type);
        googleUriSearch = googleUriSearch.replace("@KEY", GOOGLE_KEY);


        String googleResultStr = restTemplate.getForObject(
                googleUriSearch, String.class);

        logger.debug(googleResultStr);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(googleResultStr, GoogleResult.class);
    }

    /**
     *
     * Retrieve google place with placeid in order to take name of a place
     *
     * @param placeId
     * @return
     * @throws ParseException
     */
    private String makeGooglePlaceDetailCallWithPlaceID(String placeId) throws ParseException {
        String googleRetrieveWithPlace = GOOGLE_RETRIEVE_WITH_PLACE_ID.replace("@PLACE_ID", placeId);

        googleRetrieveWithPlace = googleRetrieveWithPlace.replace("@KEY", GOOGLE_KEY);


        String googlePlaceDetailStr = restTemplate.getForObject(
                googleRetrieveWithPlace, String.class);


        org.json.simple.JSONObject obj;

        JSONParser parser = new JSONParser();
        obj = (org.json.simple.JSONObject) parser.parse(googlePlaceDetailStr);

        org.json.simple.JSONObject resultObject = (JSONObject) obj.get("result");

        return (String) resultObject.get("name");
    }

    /**
     *
     * get result from google and compare with node
     *
     * @param node
     * @return
     */
    private Map<String, Map<String, String>> makeApiCallForPlaceToCompare(PlaceDBModel node) {

        Map<String, Map<String, String>> nameMap = new HashMap<>();

        PlaceTypeMapper typeMapper = new PlaceTypeMapper();
        String TYPE = typeMapper.getOpenToGoogle().get(getPlaceTypeFromDBObject(node));

        try {

            Map<String, String> latitudeAndLongitudeMap = makeOpenStreetApiCallWithOSMID(node);

            GoogleResult googleResult = makeGooglePlaceApiCall(latitudeAndLongitudeMap.get("lat"), latitudeAndLongitudeMap
                            .get("log"), TYPE);

            String nameResultFromGooglePlace = "NULL";

            if (!googleResult.getResults().isEmpty()) {
                String PLACE_ID = googleResult.getResults().get(0).getPlace_id();
                logger.debug(PLACE_ID);

                nameResultFromGooglePlace = makeGooglePlaceDetailCallWithPlaceID(PLACE_ID);

            }

            logger.debug("\n" + "*******COMPARE************");
            logger.debug("openst -> " + node.getName());
            logger.debug("googleMap -> " + nameResultFromGooglePlace);
            //logger.debug("compare -> " + nameResultFromGooglePlace.equals(node.getName()));
            logger.debug("*********FINISH**************" + "\n");

            if (!nameResultFromGooglePlace.equals(node.getName())) {
                String lngLat = latitudeAndLongitudeMap.get("lat") + "," + latitudeAndLongitudeMap.get("log");

                Map<String, String> mapOfNames = new HashMap<>();
                mapOfNames.put("openstreet", node.getName());
                mapOfNames.put("google", nameResultFromGooglePlace);
                nameMap.put(lngLat, mapOfNames);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return nameMap;

    }

}
