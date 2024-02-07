package FileManagement;

import Games.Player;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;

public class PlayerData {
    final static private File playerFilesDirectory = new File(Paths.get(".", "playerdata").toString());
    final private Player playerData;
    final private File playerFilePath;
    final private User user;

    private static Player deserializePlayerFile(File playerFile) {
        try (ObjectInputStream logStream = new ObjectInputStream(new FileInputStream(playerFile))) {
            var player = (Player) logStream.readObject();
            if (player == null) throw new RuntimeException("Player data was null for path " + playerFile);
            return player;
        } catch (IOException e) {
            throw new RuntimeException("Player file cannot be opened for reading: " + playerFile);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot import data from path: " + playerFile);
        }
    }

    private void createPlayerDataFile() {
        if (!playerFilesDirectory.mkdir()) {
            throw new RuntimeException("Cannot create data directory: " + playerFilesDirectory);
        }
        try (var fw = new FileWriter(playerFilePath); var bw = new BufferedWriter(fw)) {
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create player file for user " + user + " at " + playerFilePath);
        }
    }

    public PlayerData(User user) {
        this.user = user;

        playerFilePath = new File(
                Paths.get(playerFilesDirectory.toString(), user.getId() + ".ser").toString()
        );

        // Read player data from database if possible, if not, create a new Player altogether.
        if (playerFilePath.exists()) {
            playerData = deserializePlayerFile(playerFilePath);
            return;
        }
        createPlayerDataFile();
        playerData = new Player(user);
        logData(playerData);
    }

    public void logData(Player player) {
        try (
                var playerDataFileWriter = new RandomAccessFile(playerFilePath, "rw");
                var byteGetter = new ByteArrayOutputStream();
                var objectSerializer = new ObjectOutputStream(byteGetter);
        ) {
            objectSerializer.writeObject(player);
            objectSerializer.flush();
            byte[] data = byteGetter.toByteArray();
            playerDataFileWriter.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not export player data for user: " + user);
        }
    }

    public static Player[] getAllPlayers() {
        var empty = new Player[0];
        if (!playerFilesDirectory.exists()) {
            return empty;
        }
        File[] playerFiles = playerFilesDirectory.listFiles();
        if (playerFiles == null) return empty;
        return Arrays.stream(playerFiles)
                .map(PlayerData::deserializePlayerFile)
                .toArray(Player[]::new);
    }

    public Player playerData() {
        return playerData;
    }
}
