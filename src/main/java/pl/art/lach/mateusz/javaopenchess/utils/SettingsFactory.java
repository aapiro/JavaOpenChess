/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.art.lach.mateusz.javaopenchess.utils;

import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;

/**
 * Settings Factory class.
 * @author Mateusz Lach (matlak, msl)
 */
public class SettingsFactory
{
    public static Settings getInstance(
            GameModes gameMode, GameTypes gameType,
            String playerWhiteName, String playerBlackName, 
            PlayerType playerWhiteType, PlayerType playerBlackType)
    {
        Settings result = new Settings();
        
        return result;
    }
    
    public static Settings getInstance(
            GameModes gameMode, GameTypes gameType,
            String playerWhiteName, String playerBlackName, 
            PlayerType playerWhiteType, PlayerType playerBlackType,
            boolean upsideDown)
    {
        Settings result = new Settings();
        
        return result;
    }
    
    public static Settings getInstance()
    {
        return new Settings();
    }
}
