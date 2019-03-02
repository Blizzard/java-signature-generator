package com.blizzard.documentation.oauth2.demo.signature.service;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.CharacterItemsGuild;
import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.ClassName;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.ApiCrawler;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
@ConfigurationProperties(prefix="image-layout")
/**
 * {@inheritDoc}
 * Uses the Battle.net APIs to retrieve data
 */
public class SignatureImageServiceImpl implements SignatureImageService{

    private Integer totalWidth;
    private Integer totalHeight;
    private Integer insetX;
    private Integer insetY;
    private Integer nameX;
    private Integer nameY;
    private Float nameFontSize;
    private String nameFontColor;
    private Integer characterSummaryX;
    private Integer characterSummaryY;
    private Float characterSummaryFontSize;
    private String characterSummaryFontColor;
    private Integer itemLevelDetailX;
    private Integer itemLevelDetailY;
    private Float itemLevelDetailFontSize;
    private String itemLevelDetailFontColor;
    private Integer achievementPointsX;
    private Integer achievementPointsY;
    private Float achievementPointsFontSize;
    private String achievementPointsFontColor;

    private int imageType = BufferedImage.TYPE_INT_RGB;

    @Autowired
    private ApiCrawler apiCrawler;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private WowClassDefinitionService wowClassDefinitionService;

    /**
     * Query for the class name of the provided character and return it. This uses the {@link WowClassDefinitionService}
     * to resolve.
     *
     * @param classId
     * @return The {@link ClassName} appropriately filled out for the character in question
     */
    protected ClassName getClassName(final Integer classId){
        ClassName className = new ClassName();
        try {
            className = wowClassDefinitionService.getWoWClass(classId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        log.info(String.format("Found classId %s, transformed into className %s", classId, className.getName()));
        return className;
    }

    /**
     * Draws the portrait in the appropriate spot in the base image, returning the modified base image.
     *
     * @param portrait
     * @param baseImage
     * @return
     */
    protected BufferedImage addPortraitToBaseImage(final BufferedImage portrait, final BufferedImage baseImage){
        baseImage.getGraphics().drawImage(portrait, insetX, insetY, null);
        return baseImage;
    }

    /**
     * Draws the background in the appropriate spot on the base image, returning the modified base image.
     * @param background
     * @param baseImage
     * @return
     */
    protected BufferedImage addBackgroundToBaseImage(final BufferedImage background, final BufferedImage baseImage){
        baseImage.getGraphics().drawImage(background, 0, 0, null);
        return baseImage;
    }


    /**
     * {@inheritDoc}
     *
     * Specifically contacts the Battle.net APIs for character data, using the inset image instead of the avatar image.
     */
    @Override
    public BufferedImage generateSignature(final String characterName, final String realmName) throws IOException, URISyntaxException {
        log.trace(String.format("Received request for character name (%s) on realm (%s)", characterName, realmName));

        Map<String, String> params = new HashMap<>();
        params.put("fields", "guild,items");
        params.put("locale", "en_US");

        CharacterItemsGuild characterItemsResult = getDataForUrl(String.format("/wow/character/%s/%s", realmName.toLowerCase(), characterName.toLowerCase()), params, CharacterItemsGuild.class);

        ClassName className = getClassName(characterItemsResult.getClassId());

        // The image to build everything onto
        BufferedImage baseImage = new BufferedImage(totalWidth, totalHeight, imageType);

        // Resolve the character portrait
        BufferedImage characterPortrait = getCharacterPortrait(characterItemsResult);

        // Choose the background based on the faction, as we created faction specific background images for this
        BufferedImage background = getImageBackground(characterItemsResult);

        // Add our assets to the base image.
        // portrait first, then overlay the background on top for transition effect
        baseImage = addPortraitToBaseImage(characterPortrait, baseImage);
        baseImage = addBackgroundToBaseImage(background, baseImage);

        // Add the text to the image, and return the result
        return addTextToImage(baseImage, characterItemsResult, className);
    }

    /**
     * Retrieve the character portrait of the specified character.
     *
     * @param characterItemsGuild
     * @return
     * @throws IOException
     */
    protected BufferedImage getCharacterPortrait(final CharacterItemsGuild characterItemsGuild) throws IOException {
        // The portrait. We're using -inset.jpg, instead of -avatar.jpg, and you can read more about the differences
        // at https://develop.battle.net/documentation/guides/community-apis-world-of-warcraft-character-renders
        // This choice is purely for aesthetic reasons for our signature generator.
        URL characterPortraitURL = new URL(
                appConfig.getBaseImageUrl(),
                String.format(
                        "%s?alt=/wow/static/images/2d/avatar/%s-%s.jpg",
                        characterItemsGuild.getThumbnail(),
                        characterItemsGuild.getRace(),
                        characterItemsGuild.getGender()
                )
                        .replace("avatar.jpg", "inset.jpg") // Let's use the inset image, not the avatar
        );
        log.trace("Reading character portrait from %s", characterPortraitURL);
        BufferedImage characterPortrait = ImageIO.read( characterPortraitURL );
        return characterPortrait;
    }

    /**
     * Retrieve the background image used for building the signature.
     *
     * Presently uses different backgrounds based on the character's faction.
     *
     * @param characterItemsGuild
     * @return
     * @throws IOException
     */
    protected BufferedImage getImageBackground(final CharacterItemsGuild characterItemsGuild) throws IOException {
        BufferedImage background;
        switch(characterItemsGuild.getFaction()){
            case 0: // Alliance
                background = ImageIO.read(
                    new File(
                        ClassLoader.getSystemClassLoader().getResource("images/background-0.png").getFile()
                    )
                );
                break;
            case 1: // Horde
                background = ImageIO.read(
                    new File(
                        ClassLoader.getSystemClassLoader().getResource("images/background-1.png").getFile()
                    )
                );
                break;
            case 2: // Undeclared Pandas
            default:
                background = ImageIO.read(
                    new File(
                        ClassLoader.getSystemClassLoader().getResource("images/background-2.png").getFile()
                    )
                );
                break;
        }
        return background;
    }

    /**
     * Adds text to the provided image, and returns the modified image.
     *
     * @param baseImage
     * @param characterItemsGuild
     * @param className
     * @return
     * @throws IOException
     */
    protected BufferedImage addTextToImage(final BufferedImage baseImage, final CharacterItemsGuild characterItemsGuild, final ClassName className) throws IOException {
        ImagePlus baseImageImagePlus = new ImagePlus("baseImage", baseImage);
        ImageProcessor baseImageImageProcessor = baseImageImagePlus.getProcessor();

        baseImageImageProcessor.setAntialiasedText(true);
        BufferedImage intermediaryImageBufferedImage = baseImageImageProcessor.getBufferedImage();

        Graphics intermediaryImageBufferedImageGraphics = intermediaryImageBufferedImage.getGraphics();

        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            log.trace("Registering font Merriweather-Regular.ttf");
            localGraphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
                    ClassLoader.getSystemClassLoader().getResource("fonts/merriweather/Merriweather-Regular.ttf").getFile()
            )));
            log.trace("Registering font Merriweather-Bold.ttf");
            localGraphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
                    ClassLoader.getSystemClassLoader().getResource("fonts/merriweather/Merriweather-Bold.ttf").getFile()
            )));
            log.trace("Registering font Merriweather-BoldItalic.ttf");
            localGraphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
                    ClassLoader.getSystemClassLoader().getResource("fonts/merriweather/Merriweather-BoldItalic.ttf").getFile()
            )));
            log.trace("Registering font Merriweather-Italic.ttf");
            localGraphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
                    ClassLoader.getSystemClassLoader().getResource("fonts/merriweather/Merriweather-Italic.ttf").getFile()
            )));

        } catch (FontFormatException e) {
            log.error(e);
            // default font is fine
        }

        log.trace("Choosing font Merriweather, as Bold, size 10");
        intermediaryImageBufferedImageGraphics.setFont(new Font("Merriweather", Font.BOLD, 10));

        Graphics2D g2d = (Graphics2D) intermediaryImageBufferedImageGraphics;
        // Turn on anti-aliasing for text rendering as we're drawing it.
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Add the character name line
        g2d.setColor(Color.decode(nameFontColor));
        g2d.setFont(intermediaryImageBufferedImageGraphics.getFont().deriveFont(nameFontSize));
        g2d.drawString(characterItemsGuild.getName(), nameX, nameY);

        log.trace("Choosing font Merriweather, as Plain, size 10");
        g2d.setFont(new Font("Merriweather", Font.PLAIN, 10));

        // Add the character detail line
        g2d.setColor(Color.decode(characterSummaryFontColor));
        g2d.setFont(intermediaryImageBufferedImageGraphics.getFont().deriveFont(characterSummaryFontSize));
        if(characterItemsGuild.getGuild() != null && characterItemsGuild.getGuild().getName() != null && !characterItemsGuild.getGuild().getName().isEmpty()){
            g2d.drawString(String.format("Level %s %s of <%s> on %s", characterItemsGuild.getLevel(), className.getName(), characterItemsGuild.getGuild().getName(), characterItemsGuild.getRealm()), characterSummaryX, characterSummaryY);
        }else{
            g2d.drawString(String.format("Level %s %s on %s", characterItemsGuild.getLevel(), className.getName(), characterItemsGuild.getRealm()), characterSummaryX, characterSummaryY);
        }

        // Add the item level detail line
        g2d.setColor(Color.decode(itemLevelDetailFontColor));
        g2d.setFont(intermediaryImageBufferedImageGraphics.getFont().deriveFont(itemLevelDetailFontSize));
        g2d.drawString(String.format("Item Level: %s (%s)", characterItemsGuild.getItems().getAverageItemLevel(), characterItemsGuild.getItems().getAverageItemLevelEquipped()), itemLevelDetailX, itemLevelDetailY);

        // Add the achievement points line
        g2d.setColor(Color.decode(achievementPointsFontColor));
        g2d.setFont(intermediaryImageBufferedImageGraphics.getFont().deriveFont(achievementPointsFontSize));
        g2d.drawString(String.format("Achievement points: %s", characterItemsGuild.getAchievementPoints()), achievementPointsX, achievementPointsY);

        intermediaryImageBufferedImageGraphics.dispose();
        g2d.dispose();

        return intermediaryImageBufferedImage;
    }


    /**
     * Free logging when calling the apiCrawler seemed good.
     * @param path
     * @param klazz
     * @param <T>
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private <T> T getDataForUrl(final String path, final Map<String, String> params, final Class<T> klazz) throws IOException, URISyntaxException {
        try{
            T result = apiCrawler.getDataFromRelativePath(path, params, klazz, true);
            log.trace(String.format("%s", result));
            return result;
        }catch (IOException |URISyntaxException |HttpServerErrorException e) {
            log.error(String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
            throw e;
        }
    }

    /* *** Getters and setters used by @ConfigurationProperties(prefix="image-layout") *** */

    public Integer getTotalWidth() {
        return totalWidth;
    }

    public void setTotalWidth(Integer totalWidth) {
        this.totalWidth = totalWidth;
    }

    public Integer getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(Integer totalHeight) {
        this.totalHeight = totalHeight;
    }

    public Integer getInsetX() {
        return insetX;
    }

    public void setInsetX(Integer insetX) {
        this.insetX = insetX;
    }

    public Integer getInsetY() {
        return insetY;
    }

    public void setInsetY(Integer insetY) {
        this.insetY = insetY;
    }

    public Integer getNameX() {
        return nameX;
    }

    public void setNameX(Integer nameX) {
        this.nameX = nameX;
    }

    public Integer getNameY() {
        return nameY;
    }

    public void setNameY(Integer nameY) {
        this.nameY = nameY;
    }

    public Float getNameFontSize() {
        return nameFontSize;
    }

    public void setNameFontSize(Float nameFontSize) {
        this.nameFontSize = nameFontSize;
    }

    public String getNameFontColor() {
        return nameFontColor;
    }

    public void setNameFontColor(String nameFontColor) {
        this.nameFontColor = nameFontColor;
    }

    public Integer getCharacterSummaryX() {
        return characterSummaryX;
    }

    public void setCharacterSummaryX(Integer characterSummaryX) {
        this.characterSummaryX = characterSummaryX;
    }

    public Integer getCharacterSummaryY() {
        return characterSummaryY;
    }

    public void setCharacterSummaryY(Integer characterSummaryY) {
        this.characterSummaryY = characterSummaryY;
    }

    public Float getCharacterSummaryFontSize() {
        return characterSummaryFontSize;
    }

    public void setCharacterSummaryFontSize(Float characterSummaryFontSize) {
        this.characterSummaryFontSize = characterSummaryFontSize;
    }

    public String getCharacterSummaryFontColor() {
        return characterSummaryFontColor;
    }

    public void setCharacterSummaryFontColor(String characterSummaryFontColor) {
        this.characterSummaryFontColor = characterSummaryFontColor;
    }

    public Integer getItemLevelDetailX() {
        return itemLevelDetailX;
    }

    public void setItemLevelDetailX(Integer itemLevelDetailX) {
        this.itemLevelDetailX = itemLevelDetailX;
    }

    public Integer getItemLevelDetailY() {
        return itemLevelDetailY;
    }

    public void setItemLevelDetailY(Integer itemLevelDetailY) {
        this.itemLevelDetailY = itemLevelDetailY;
    }

    public Float getItemLevelDetailFontSize() {
        return itemLevelDetailFontSize;
    }

    public void setItemLevelDetailFontSize(Float itemLevelDetailFontSize) {
        this.itemLevelDetailFontSize = itemLevelDetailFontSize;
    }

    public String getItemLevelDetailFontColor() {
        return itemLevelDetailFontColor;
    }

    public void setItemLevelDetailFontColor(String itemLevelDetailFontColor) {
        this.itemLevelDetailFontColor = itemLevelDetailFontColor;
    }

    public Integer getAchievementPointsX() {
        return achievementPointsX;
    }

    public void setAchievementPointsX(Integer achievementPointsX) {
        this.achievementPointsX = achievementPointsX;
    }

    public Integer getAchievementPointsY() {
        return achievementPointsY;
    }

    public void setAchievementPointsY(Integer achievementPointsY) {
        this.achievementPointsY = achievementPointsY;
    }

    public Float getAchievementPointsFontSize() {
        return achievementPointsFontSize;
    }

    public void setAchievementPointsFontSize(Float achievementPointsFontSize) {
        this.achievementPointsFontSize = achievementPointsFontSize;
    }

    public String getAchievementPointsFontColor() {
        return achievementPointsFontColor;
    }

    public void setAchievementPointsFontColor(String achievementPointsFontColor) {
        this.achievementPointsFontColor = achievementPointsFontColor;
    }
}
