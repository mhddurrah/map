package com.example.openmapvalidator.service;

import com.example.openmapvalidator.helper.Const;
import com.example.openmapvalidator.helper.PlaceCategoryMapper;
import com.example.openmapvalidator.model.foursquare.FoursquareResult;
import com.example.openmapvalidator.model.google.GoogleResult;
import com.example.openmapvalidator.model.microsoft.MicrosoftResult;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author senan.ahmedov
 */
@Service
public class FileToDB {

    private static final Logger logger = LoggerFactory.getLogger(FileToDB.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    DocumentBuilderFactory dbFactory;

    private static final ConcurrentMap<String, ConcurrentMap<String, String>> mapNameQueue = new ConcurrentHashMap<>();


    private void osmFileToDB(String fileName) {

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        try {

            String osmCommandPathRoot = "../../../bashscript/";
            ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {

                osmCommandPathRoot += "windows/osm2pgsql-bin/";
                builder.command(osmCommandPathRoot + Const.OSM_COMMAND, Const.OSM_COMMAND_CREATE_OPTION,
                        Const.OSM_COMMAND_USERNAME_OPTION, Const.PSQL_USERNAME, Const.OSM_COMMAND_DATABASE_OPTION,
                        Const.OSM_COMMAND_DATABASE_ARGUMENT, fileName);
            } else {
                osmCommandPathRoot += "unix/osm2pgsql/bin/";
                builder.command(osmCommandPathRoot + Const.OSM_COMMAND, Const.OSM_COMMAND_CREATE_OPTION,
                        Const.OSM_COMMAND_DATABASE_OPTION, Const.OSM_COMMAND_DATABASE_ARGUMENT, fileName);
            }

            builder.directory(new ClassPathResource("map").getFile());
            Process process = builder.start();
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;

        } catch (IOException e) {
            logger.debug("osm command execute exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private SqlSession getDBSession() throws IOException {
        String resource = "mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new
                SqlSessionFactoryBuilder().build(inputStream);

        return sqlSessionFactory.openSession();
    }

    public Map<String, Map<String, String>> saveAndCallForPlaceCoordinates(String fileName) {

        Map<String, Map<String, String>> nameMap = new ConcurrentHashMap<>();

        logger.debug("place data, openstreet from db");

        try  {

            osmFileToDB(fileName);

            SqlSession session = getDBSession();
            List<PlaceDBModel> list = session.selectList("selectPlaces");

            ExecutorService executorService = Executors.newFixedThreadPool(5);


            for (PlaceDBModel model : list) {
                Map<String, Map<String, String>> temporaryNameMap = new HashMap<>();

                logger.debug("Id: " + model.getOsm_id() + " Name: " + model.getName());

                try {
                    executorService.submit(new Runnable(){
                        public void run(){
                            makeApiCallForPlaceToCompare(model);

                            temporaryNameMap.putAll(makeApiCallForPlaceToCompare(model));
                            temporaryNameMap.putAll(nameMap);

                            nameMap = temporaryNameMap;
                        }
                    });
                    System.out.println(App.truckList[i]);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace(System.err);
                }

                

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
     * place category could be either amenity or shop attribute if amenity is not null it has priority
     *
     * @param dbModel
     * @return
     */
    private String getPlaceCategoryFromDBObject(PlaceDBModel dbModel) {
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

        String openStreetUriGet = Const.OPENSTREET_URI_GET_LONG_WITH_OSM_ID.replace("@OSM_ID", node.getOsm_id());

        String result = restTemplate.getForObject(openStreetUriGet, String.class);

        File tmpFile = File.createTempFile("test", ".xml");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(result);
        writer.close();

        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(tmpFile);
        doc.getDocumentElement().normalize();
        logger.debug("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nodeList = doc.getElementsByTagName("node");
        //now XML is loaded as Document in memory, lets convert it to Object List
        NamedNodeMap map = nodeList.item(0).getAttributes();
        String LAT = map.getNamedItem("lat").getNodeValue();
        String LON = map.getNamedItem("lon").getNodeValue();

        longAndLatMap.put("lat", LAT);
        longAndLatMap.put("lon", LON);

        return longAndLatMap;
    }

    private FoursquareResult makeFourSquareApiCall(String lat, String lon, String categoryId) throws IOException {

        String foursquareUriSearch = Const.FOURSQUARE_URI_SEARCH_WITH_LONG.replace("@LAT", lat)
                .replace("@LON", lon);

        String foursquareResultStr = restTemplate.getForObject(
                foursquareUriSearch, String.class);

        logger.debug(foursquareResultStr);

        return objectMapper.readValue(foursquareResultStr, FoursquareResult.class);
    }

    /**
     *
     * get google places result with latitude and longitude category of a place is mapped from openstreet
     *
     * @param lat
     * @param log
     * @param category
     * @return
     * @throws IOException
     */
    private GoogleResult makeGooglePlaceApiCall(String lat, String log, String category) throws IOException {

        /*String googleUriSearch = Const.GOOGLE_URI_SEARCH_WITH_LONG.replace("@LAT", lat)
                .replace("@LONG", log)
                .replace("@CATEGORY", category);*/
        String googleUriSearch = Const.GOOGLE_SEARCH_NEARBY.replace("@LAT", lat)
                .replace("@LONG", log);

        String googleResultStr = restTemplate.getForObject(
                googleUriSearch, String.class);

        logger.debug(googleResultStr);

        return objectMapper.readValue(googleResultStr, GoogleResult.class);
    }

    /**
     *
     * its just applied to USA but in any short time other countries will added
     *
     * @param lat
     */
    private MicrosoftResult makeMicrosoftPlaceApiCall(String lat, String log) throws IOException {

        String microsoftUriSearch = Const.MICROSOFTMAP_SEARCH_WITH_LONG.replace("@LAT", lat)
                .replace("@LOG", log);

        String microsoftResultStr = restTemplate.getForObject(
                microsoftUriSearch, String.class);

        logger.debug(microsoftResultStr);

        return objectMapper.readValue(microsoftResultStr, MicrosoftResult.class);
    }

    /**
     *
     * Retrieve google place with placeid in order to take name of a place
     *TODO NO NEENED ANYMORE
     * @param placeId
     * @return
     * @throws ParseException
     */
    private String makeGooglePlaceDetailCallWithPlaceID(String placeId) throws ParseException {
        String googleRetrieveWithPlace = Const.GOOGLE_RETRIEVE_WITH_PLACE_ID.replace("@PLACE_ID", placeId);

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

        // GOOGLE
     //   String openstreetCategory = getPlaceCategoryFromDBObject(node);
    //    Map<String, String> otherMapCategories = PlaceCategoryMapper.getOpenToOtherMaps().get(openstreetCategory);
//        String googleCategory = otherMapCategories.get("google");

    /*    if (googleCategory == null) {
            googleCategory = Const.OPEN_QUOTE + Const.CLOSE_QUOTE;
            logger.debug("!GOOGLE TYPE COULD NOT MAPPED FROM FOLLOWING OPENSTREET PLACE CATEGORY - {}",
                    openstreetCategory);
        }*/

        try {

            Map<String, String> latitudeAndLongitudeMap = makeOpenStreetApiCallWithOSMID(node);
            logger.debug("{}, {}", latitudeAndLongitudeMap.get("lat"), latitudeAndLongitudeMap.get("lon"));

            String lat = latitudeAndLongitudeMap.get("lat");
            String lon = latitudeAndLongitudeMap.get("lon");

            GoogleResult googleResult = makeGooglePlaceApiCall(lat, lon, null);

            String nameResultFromGooglePlace = "NULL";

            if (!googleResult.getResults().isEmpty()) {
                //String PLACE_ID = googleResult.getResults().get(0).getPlace_id();
                //logger.debug(PLACE_ID);

                //nameResultFromGooglePlace = makeGooglePlaceDetailCallWithPlaceID(PLACE_ID);
                nameResultFromGooglePlace = googleResult.getResults().get(0).getName();
            }

            // FOURSQUARE
            //String foursquareCategory = otherMapCategories.get("foursquare");
            FoursquareResult foursquareResult = makeFourSquareApiCall(lat, lon, null);

            String foursquareName = "NULL";
            if (!foursquareResult.getResponse().getVenues().isEmpty()) {
                foursquareName = foursquareResult.getResponse().getVenues().get(0).getName();
            }

            logger.debug("\n" + "*******COMPARE************");
            logger.debug("openst -> " + node.getName());
            logger.debug("googleMap -> " + nameResultFromGooglePlace);
            logger.debug("foursq -> " + foursquareName);

            //logger.debug("compare -> " + nameResultFromGooglePlace.equals(node.getName()));
            logger.debug("*********FINISH**************" + "\n");

            MicrosoftResult microsoftResult = makeMicrosoftPlaceApiCall(lat, lon);
            String microsoftPlaceName = microsoftResult.getResourceSets().get(0).
                    getResources().get(0).
                    getBusinessesAtLocation().get(0).
                    getBusinessInfo().getEntityName();

            if (true) {//!nameResultFromGooglePlace.equals(node.getName())) {
                String lngLat = latitudeAndLongitudeMap.get("lat") + "," + latitudeAndLongitudeMap.get("lon");

                Map<String, String> mapOfNames = new HashMap<>();
                mapOfNames.put("openstreet", node.getName());
                mapOfNames.put("google", nameResultFromGooglePlace);
                mapOfNames.put("foursquare", foursquareName);
                mapOfNames.put("microsoft", microsoftPlaceName);
                nameMap.put(lngLat, mapOfNames);
            }
            //TODO burada istatistik icin bir degeri arttir else kisminda


        } catch (IOException e) {
            e.printStackTrace();
       // } catch (ParseException e) {
         //   e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return nameMap;

    }

}
