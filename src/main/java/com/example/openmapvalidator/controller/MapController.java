package com.example.openmapvalidator.controller;

import com.example.openmapvalidator.service.FileToDB;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/maps")
public class MapController {

    private final FileToDB fileToDBService;

    @Autowired
    public MapController(FileToDB fileToDBService) {
        this.fileToDBService = fileToDBService;
    }

    @PostMapping
    public Map<String, Map<String, String>> uploadFile(@RequestParam MultipartFile file) {

        try {
            File localFile = new File(new ClassPathResource("map").getFile(), file.getOriginalFilename());

            //FileUtils.copyInputStreamToFile(file, new ClassPathResource("map").getFile());
            FileUtils.writeByteArrayToFile(localFile, file.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Map<String, String>> map =
                fileToDBService.saveAndCallForPlaceCoordinates(file.getOriginalFilename());

        return map;

        /*Map<String, Map<String, String>> map = null;
        try {
            map = mockData();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;*/
    }

    private Map<String, Map<String, String>> mockData() throws IOException {
        String json = "{\"48.1800796,16.3276520\":{\"foursquare\":\"NULL\",\"google\":\"Tiefgarage " +
                "F\u00FCchselhofpark\",\"openstreet\":\"F\u00FCchselhofparkgarage\"},\"48.1792934," +
                "16.3266492\":{\"foursquare\":\"NULL\",\"google\":\"UTOPIA\",\"openstreet\":\"Club UTOPIA\"},\"48.1798179,16.3273043\":{\"foursquare\":\"Hofer\",\"google\":\"HOFER\",\"openstreet\":\"Hofer\"},\"48.1790217,16.3264600\":{\"foursquare\":\"Eni Ruckergasse\",\"google\":\"eni ServiceStation\",\"openstreet\":\"Agip\"}}";
        Map<String, Object> map = new HashMap<String, Object>();

        // convert JSON string to Map
        Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
        Gson gson = new Gson();
        Map<String, Map<String, String>> myMap = gson.fromJson(json, type);

        return myMap;
    }
}