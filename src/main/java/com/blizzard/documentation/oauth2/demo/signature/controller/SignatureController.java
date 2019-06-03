package com.blizzard.documentation.oauth2.demo.signature.controller;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.service.SignatureImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Main controller provided for this application.
 */
@Controller
@Log4j2
public class SignatureController {
    @Autowired
    private SignatureImageService signatureService;

    @Autowired
    private AppConfig appConfig;

    /**
     * Main mapping provided for this application.
     *
     * @param characterName The character name
     * @param realmName The programmatic realm name
     * @return an array of bytes renderable as an image.
     * @throws IOException if the downstream services are not available
     * @throws URISyntaxException should only happen if there is a configuration issue
     */
    @GetMapping(path = "/signature", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getSignature(
        @RequestParam(required = true) final String characterName,
        @RequestParam(required = true) final String realmName
    ) throws IOException, URISyntaxException {
        ImageOutputStream imageOutputStream = null;
        ImageWriter writer = null;
        try {
            // We need a byte array for returning our image.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Get a custom writer that uses a custom compression quality for our resulting jpeg
            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            // We've been assuming RGB in the app, so let's keep that up
            jpegParams.setDestinationType(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB));
            // Set the custom compression quality, and make sure that we use it
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(appConfig.getCompressionQuality());

            // Make an ImageOutputStream that accepts our ByteArrayOutputStream, without needing a lot of additional resources
            imageOutputStream = new MemoryCacheImageOutputStream(baos);

            // prep the writer to make a jpg type to our ImageOutputStream
            writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(imageOutputStream);

            // Use the writer to output the contents of the image into our ByteArrayOutputStream via the ImageOutputStream
            writer.write(null, new IIOImage(signatureService.generateSignature(characterName, realmName).getData(), null, null), jpegParams);

            // Return the resulting byte array
            return baos.toByteArray();
        } catch (IOException | URISyntaxException e) {
            log.error(e);
            throw e;
        } finally {
            // Clean up after ourselves
            if(imageOutputStream != null){
                imageOutputStream.close();
            }
            if(writer != null){
                writer.dispose();
            }
        }
    }
}
