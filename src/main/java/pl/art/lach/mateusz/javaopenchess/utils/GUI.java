/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.art.lach.mateusz.javaopenchess.utils;

import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import pl.art.lach.mateusz.javaopenchess.core.Colors;

/** 
 * Class representing the game interface which is seen by a player and
 * where are lockated available for player opptions, current games and where
 * can he start a new game (load it or save it)
 * 
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 */
public class GUI
{
    private static Properties configFile;
    
    private static final String IMAGE_PATH = "theme/%s/images/%s";

    private static final Logger LOG = Logger.getLogger(GUI.class);
    
    private static final String JAR_FILENAME = "[a-zA-Z0-9%!@\\-#$%^&*\\(\\)\\[\\]\\{\\}\\.\\,\\s]+\\.jar";
    
    private static final String CONFIG_FILENAME = "config.txt";
    
    private static final String THEME = "THEME";
    
    private static final String SLASH = "/";
    
    private static final String SPACE_IN_HEX = "%20";
    
    private Game game;

    /**
     * Default constructor
     */
    public GUI()
    {
        this.game = new Game();
    }

    public static Image loadPieceImage(String pieceName, Colors color, int size, String fileExt)
    {
        String colorSymbol = String.valueOf(color.getSymbol()).toUpperCase();
        return loadImage(pieceName + "-" + colorSymbol + size + fileExt);
    }
    
    /**
     * Method load image by a given name with extension
     * @param name : string of image to load for ex. "chessboard.jpg"
     * @return  : image or null if cannot loadng
     */
    public static Image loadImage(String name)
    {
        if (null == getConfigFile())
        {
            return null;
        }
        return loadAndReturnImage(name);
    }

    private static Image loadAndReturnImage(String name)
    {
        Image img = null;
        try
        {
            String imageLink = String.format(
                IMAGE_PATH,
                getConfigFile().getProperty(THEME, "default"),
                name
            );
            LOG.debug(THEME + ": " + getConfigFile().getProperty(THEME));
            img = ImageIO.read(JChessApp.class.getResourceAsStream(imageLink)); 
        }
        catch (Exception e)
        {
            LOG.error(String.format(
                "IOException, some error during getting config file!, message: %s stackTrace: %s",
                e.getMessage(), Arrays.toString(e.getStackTrace())
            ));
        }
        return img;
    }

    public static boolean themeIsValid(String name)
    {
        //TODO: implement me
        return true;
    }

    /**
     * Static function to get current location of JAR file.
     * @return 
     */
    public static String getJarPath()
    {
        String path = GUI.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile();
        
        path = path.replaceAll(JAR_FILENAME, "");
        int lastSlash = path.lastIndexOf(File.separator); 
        if (path.length() - 1 == lastSlash)
        {
            path = path.substring(0, lastSlash);
        }
        path = path.replace(SPACE_IN_HEX, " ");
        return path;
    }
    
    
    private static String getFullConfigFilePath() 
    {
        String result =  GUI.getJarPath() + CONFIG_FILENAME;
        if (result.startsWith(SLASH))
        {
            result = result.replaceFirst(SLASH, "");
        }
        return result;
    }
    

    public static synchronized Properties getConfigFile()
    {
        if (null == configFile) 
        {
            loadConfigFile();
        } 
        return configFile;
    }

    private static void loadConfigFile()
    {
        Properties defConfFile = new Properties();
        Properties confFile    = new Properties();
        
        File outFile = new File(getFullConfigFilePath());
        loadDefaultConfigFile(defConfFile);
        if (!outFile.exists())
        {
            saveConfigFileOutsideJar(defConfFile, outFile);
        }
        loadOuterConfigFile(confFile, outFile);
        configFile = confFile;
    }

    private static void loadOuterConfigFile(Properties confFile, File outFile)
    {
        try
        {
            confFile.load(new FileInputStream(outFile));
        }
        catch (IOException e)
        {
            LOG.error(String.format(
                "IOException, some error during getting config file!, message: %s stackTrace: %s",
                e.getMessage(), Arrays.toString(e.getStackTrace())
            ));
        }
    }

    private static void saveConfigFileOutsideJar(Properties defConfFile, File outFile)
    {
        try
        {
            defConfFile.store(new FileOutputStream(outFile), null);
        }
        catch (IOException e)
        {
            LOG.error(String.format(
                "IOException, some error during getting config file!, message: %s stackTrace: %s",
                e.getMessage(), Arrays.toString(e.getStackTrace())
            ));
        }
    }

    private static void loadDefaultConfigFile(Properties defConfFile)
    {
        try
        {
            InputStream is = GUI.class.getResourceAsStream(CONFIG_FILENAME);
            if (null != is) {
                defConfFile.load(is);
            }
        }
        catch (IOException | NullPointerException e)
        {
            LOG.error(String.format(
                "IOException, some error during getting config file!, message: %s stackTrace: %s",
                e.getMessage(), Arrays.toString(e.getStackTrace())
            ));
        }
    }

    /**
     * @return the game
     */
    public Game getGame()
    {
        return game;
    }
}
