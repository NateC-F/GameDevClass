package gameframework.resourcemanagement;

import gameframework.sound.AudioData;
import gameframework.sound.GameClip;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/* This is a class that we use to easily load images, audio clips and other resources embedded in
 * the executable (jar file). It also does resource management by making sure we never load the same
 * resource twice from secondary storage.
 */
public class ResourceManager
{
    private static final String RESOURCE_FOLDER = "/resources/";
    private final static String LEVELS_FOLDER = "levels/";
    private final HashMap<String, Object> resourceMap;

    public ResourceManager()
    {
        resourceMap = new HashMap<String,Object>();
    }

    public interface ResourceProcessor
    {
        Object process(InputStream resourceStream) throws Exception;
    }

    public Object loadGeneralResource(String resourceName, String levelName,
                                      ResourceProcessor resourceProcessor)
    {
        String resourcePath = RESOURCE_FOLDER  + (levelName.isEmpty() ? "" :
                (LEVELS_FOLDER + levelName + "/")) + resourceName ;
        //Important, note that resource names should be unique per level
        Object resource = resourceMap.get(resourceName);

        if (resource == null)
        {
            /* if the resource isn't registered already then load it from the jar file and store
             * it in the resource map so that it can be retrieved easily using the resource name */
            try
            {
                resource = resourceProcessor.process(getClass().getResourceAsStream(resourcePath));
                resourceMap.put(resourceName, resource);
            }
            catch (Exception e)
            {
                System.out.println("Unable to load resource: " +
                        resourceName);
            }
        }
        return resource;
    }

    public BufferedImage loadImageResource(String name, String levelName)
    {
        return (BufferedImage)loadGeneralResource(name, levelName, new ResourceProcessor() {
            @Override
            public Object process(InputStream resourceStream) throws Exception
            {
                return ImageIO.read(resourceStream);
            }
        });
    }

    public ArrayList<String> loadTextResource(String name, String levelName)
    {
        return (ArrayList<String>)loadGeneralResource(name, levelName, new ResourceProcessor() {
            @Override
            public Object process(InputStream resourceStream) throws Exception
            {
                ArrayList<String> text = new ArrayList<String>();
                Scanner fileScanner = new Scanner(resourceStream);

                while (fileScanner.hasNextLine())
                    text.add(fileScanner.nextLine());

                return text;
            }
        });

    }

    public AudioData loadAudioResource(String name,
                                      String levelName)
    {
        return (AudioData) loadGeneralResource(name, levelName, new ResourceProcessor()
        {
            @Override
            public Object process(InputStream resourceStream) throws Exception
            {
                // Load the audio input stream (we only support raw audio, that is an uncompressed WAV )
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(resourceStream);
                // Get the audio format
                AudioFormat format = audioStream.getFormat();
                // Read all audio bytes into memory
                byte[] audioBytes = audioStream.readAllBytes();
                // Return the audio data container
                return new AudioData(audioBytes, format);
            }
        });
    }

    public Font loadFontResource(String name, float size)
    {
        return (Font) loadGeneralResource(name, "", new ResourceProcessor() {
            @Override
            public Object process(InputStream resourceStream) throws Exception
            {
                // Create the font from the resource stream
                Font font = Font.createFont(Font.TRUETYPE_FONT, resourceStream);

                // Derive it to the size you want
                return font.deriveFont(size);
            }
        });
    }

    /* Returns true if a resource has already been loaded from file and
     * is currently stored in RAM (in the resource map) for faster access. */
    public boolean resourceIsAlreadyLoaded(String name)
    {
        return resourceMap.get(name) != null;
    }

    public void freeResources() {resourceMap.clear();}

}
