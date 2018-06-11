package com.example.openmapvalidator.controller;

import com.example.openmapvalidator.service.FileToDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/maps")
public class MapController {

    private final FileToDB fileToDBService;

    @Autowired
    public MapController(FileToDB fileToDBService) {
        this.fileToDBService = fileToDBService;
    }

    @GetMapping
    Map<String, Map<String, String>> readMap() {
        return fileToDBService.saveAndCallForCoordinates();
    }

}
