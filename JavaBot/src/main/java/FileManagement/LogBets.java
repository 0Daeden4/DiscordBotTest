package FileManagement;

import Games.Bets;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static Main.Main.betsAndChallenges;
public class LogBets {

    public LogBets(List<Bets> bets, String objectName){
        File logDirectory = new File(Paths.get("").toAbsolutePath()+File.separator +"Logs"); //Creates a log file inside the PlayerData directory named after the gameName
        File logFilePath = new File(logDirectory.getAbsolutePath()+File.separatorChar+objectName+".ser");
        if(logFilePath.exists()){ //import logs
            //method to import
            ObjectInputStream ois = null;
            try {
                ois= new ObjectInputStream(new FileInputStream(logFilePath));
                betsAndChallenges = (ArrayList<Bets>) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("ObjectIO could not load object.");
            } catch (IOException e) {
                throw new RuntimeException("ObjectIO could not load object.");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    ois.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }

        //create and save logs
        logDirectory.mkdir();
        try {
            logFilePath.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("OBJECTIO object could not be created!");
        }
        logBets(bets, "betsAndChallenges");
    }
    public static void logBets(List<Bets> bets, String objectName){
        File logDirectory = new File(Paths.get("").toAbsolutePath()+File.separator +"Logs"); //Creates a log file inside the PlayerData directory named after the gameName
        File logFilePath = new File(logDirectory.getAbsolutePath()+File.separatorChar+objectName+".ser");
        RandomAccessFile raf =null;
        ObjectOutputStream os = null;
        ByteArrayOutputStream baos = null;
        try {
            raf=new RandomAccessFile(logFilePath, "rw");
            baos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(baos);
            os.writeObject(bets);
            os.flush();
            byte[] data = baos.toByteArray();
            raf.write(data);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("ObjectIO could not save object.");
        } catch (IOException e) {
            throw new RuntimeException("ObjectIO could not save object.");
        }finally {
            try {
                raf.close();
                os.close();
                baos.close();
            } catch (IOException e) {
                throw new RuntimeException("ObjectIO save stream could not be closed.");
            }
        }
    }
}
