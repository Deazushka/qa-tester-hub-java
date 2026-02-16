package com.qatester.hub.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Task 4 - Image Like/Dislike
 */
@Controller
public class Task4Controller {

    @Value("classpath:/static/images/")
    private Resource imagesResource;

    @GetMapping("/task4")
    public String task4(Model model) {
        List<String> images = getImageFiles();
        Collections.shuffle(images);
        model.addAttribute("images", images);
        return "task4";
    }

    private List<String> getImageFiles() {
        List<String> images = new ArrayList<>();
        try {
            Path imagesPath = Paths.get(imagesResource.getURI());
            if (Files.exists(imagesPath) && Files.isDirectory(imagesPath)) {
                images = Files.list(imagesPath)
                        .filter(Files::isRegularFile)
                        .map(p -> "/images/" + p.getFileName().toString())
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            // If images folder doesn't exist, return empty list
        }
        return images;
    }
}
