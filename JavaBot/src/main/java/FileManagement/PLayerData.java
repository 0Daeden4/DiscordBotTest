package FileManagement;

import Games.Player;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PLayerData {
    private Player playerData = null;
    private File logFilePath;

    public PLayerData(User user){
        File logDirectory = new File(Paths.get("").toAbsolutePath()+File.separator +"PlayerDatas"); //Creates a log file inside the PlayerData directory named after the gameName
        logFilePath = new File(logDirectory.getAbsolutePath()+File.separatorChar+user.getId()+".xml");


        if(logFilePath.exists()){ // Import logDirectory file
            ObjectInputStream importLogs = null;
            try {
                importLogs = new ObjectInputStream(new FileInputStream(logFilePath));
                try {
                    playerData = (Player) importLogs.readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Error while importing Object data!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Something wrong happened while importing logs!");
                throw new RuntimeException(e);
            }
            finally {
                try {
                    if(importLogs==null){
                        //rafLog.close();
                        return;
                    }
                    importLogs.close();
                    return;
                } catch (IOException e) {
                    System.out.println("File import failed!");
                    throw new RuntimeException(e);
                }
            }
        }

        //Create new logDirectory file
        BufferedWriter logs = null;
        try {
            logDirectory.mkdirs();
            logs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilePath))); //create log file for user
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                logs.close();
            } catch (IOException e) {
                System.out.println("Log file could not be created!");
                throw new RuntimeException(e);
            }
        }
        playerData = new Player(user);
        logData(playerData); //save default player for user


    }

    public void logData(Player player){
        ObjectOutputStream logger = null;
        RandomAccessFile rafLogger = null;
        ByteArrayOutputStream byteGetter = null;
        try {
            rafLogger = new RandomAccessFile(logFilePath, "rw");
            byteGetter = new ByteArrayOutputStream();
            logger = new ObjectOutputStream(byteGetter); //output seklini byte array yaptik
            logger.writeObject(player); //byte arraye player objesini yazdik
            logger.flush(); //flushladik
            byte [] data = byteGetter.toByteArray(); //byte arrayi kaydettik

            rafLogger.write(data); //byte arrayimizi belgeye yazdik
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not export user data!");
        }finally {
            try {
                rafLogger.close();
                byteGetter.close();
                logger.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not close ObjectOutputStream 'logger'!");
            }
        }

    }

    public static Player[] getAllPlayers(){
        File logDirectory = new File(Paths.get("").toAbsolutePath()+File.separator +"PlayerDatas");
        if(logDirectory.exists()){
            File[] playerLogs =logDirectory.listFiles();
            Player[] players = new Player[playerLogs.length];
            ObjectInputStream[] importLogs = new ObjectInputStream[playerLogs.length];
            for(int i =0; i < importLogs.length; i++){
                try {
                    importLogs[i] = new ObjectInputStream(new FileInputStream(playerLogs[i]));
                    players[i] =((Player) importLogs[i].readObject());
                } catch (IOException e) {
                    throw new RuntimeException("Unable to get FileInputStream in getAllPlayers();");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Unable to read object in getAllPlayers();");
                }finally {
                    try {
                        importLogs[i].close();
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to close object reading in getAllPlayers();");
                    }
                }
            }
            return players;
        }
        return null;
    }

    public Player getPLayerData(){
        if(playerData!=null){
            return playerData;
        }
        throw new NullPointerException("PLayer data is null!");
    }




    //GETTERS AND SETTERS

}
