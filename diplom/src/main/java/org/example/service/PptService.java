package org.example.service;

import jakarta.annotation.PostConstruct;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PptService {

    private final List<BufferedImage> slides = new ArrayList<>();

    @PostConstruct
    public void load() throws IOException {

        String file = "D:\\diplom\\diplom\\diplom\\src\\main\\resources\\test.pptx";
        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(file));

        Dimension pgsize = ppt.getPageSize();

        for (XSLFSlide slide : ppt.getSlides()) {

            BufferedImage img = new BufferedImage(
                    pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = img.createGraphics();
            g2d.setTransform(new AffineTransform());
            g2d.setPaint(Color.white);
            g2d.fill(new Rectangle(0, 0, pgsize.width, pgsize.height));

            slide.draw(g2d);
            g2d.dispose();

            slides.add(img);
        }

        ppt.close();
    }

    public int getSlideCount() {
        return slides.size();
    }

    public BufferedImage getSlide(int index) {
        return slides.get(index);
    }
}
