package com.polyglot.controller;

import com.polyglot.service.util.UtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("util")
public class UtilController {
    @Autowired
    private UtilService utilService;

    private static final Logger logger = LoggerFactory.getLogger(UtilController.class);

    @GetMapping("/get_all_languages")
    public List<String> getAllLanguages() {
        logger.info("REQUEST - /get_all_languages");
        return utilService.getAllLanguages();
    }
}
