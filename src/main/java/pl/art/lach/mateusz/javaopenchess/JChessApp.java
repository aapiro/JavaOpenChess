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
package pl.art.lach.mateusz.javaopenchess;

import org.jdesktop.application.SingleFrameApplication;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import java.util.Properties;
import java.io.IOException;
import java.awt.Window;

/**
 * The main class of the application.
 * @author Mateusz  Lach ( matlak, msl )
 * @author Damian Marciniak
 */
public class JChessApp extends SingleFrameApplication {
    
    protected static JChessView javaChessView; 
     
    public final static String LOG_FILE = "log4j.properties"; 
    
    public final static String MAIN_PACKAGE_NAME = JChessApp.class.getPackage().getName();

    /**
     * @return the jcv
     */
    public static JChessView getJavaChessView()
    {
        return javaChessView;
    }
     
    /**
     * At startup create and show the main frame of the application.
     */
    @Override 
    protected void startup()
    {
        javaChessView = new JChessView(this);
        show(getJavaChessView());
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override 
    protected void configureWindow(Window root) {}

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JChessApp
     */
    public static JChessApp getApplication() 
    {
        return Application.getInstance(JChessApp.class);
    }

    /**
     * Main method launching the application.
     * @param args
     */
    public static void main(String[] args) 
    {
        launch(JChessApp.class, args);
        Properties logProp = new Properties();
        try
        {   
            logProp.load(JChessApp.class.getClassLoader().getResourceAsStream(LOG_FILE)); 
            PropertyConfigurator.configure(logProp);
        }
        catch (NullPointerException | IOException e)
        {
            System.err.println("Logging not enabled : "+e.getMessage());
        } 
    }
}
