package org.example.controller;

import org.example.service.PptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class PptController {

    @Autowired
    private PptService pptService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("slides", pptService.getSlideCount());
        return "viewer";
    }

    @GetMapping(value = "/slides/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getSlide(@PathVariable int id) throws IOException {
        var image = pptService.getSlide(id - 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
