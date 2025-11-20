package org.example.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.service.PptService;
import org.example.telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class PptController {

    @Autowired
    private PptService pptService;

    private int pauseSlide = 4;

    @Getter
    private boolean paused = true;

    private final TelegramBot telegramBot;

    @GetMapping("/")
    public String index(Model model) {
        return "redirect:/view/1";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, Model model) {

        pauseSlide = telegramBot.getPauseSlide();
        if (id == pauseSlide && paused) {
            telegramBot.sendAllMessages("Внимание вопрос: сколько матерей у Ани?");
            paused = false;

            model.addAttribute("next", id);
            model.addAttribute("slides", pptService.getSlideCount());
            return "test_viewer";
        }

        if (id < 1 || id > pptService.getSlideCount()) {
            return "redirect:/view/1";
        }

        model.addAttribute("current", id);
        model.addAttribute("numberSlide", pauseSlide);
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
