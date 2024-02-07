package FileManagement;

import Games.Player;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GrabPlayerData {

    public <T> GrabPlayerData(T event){
        userGameData(event);
    }
    public GrabPlayerData(User user){
        userGameData(user);
    }
    public <T> Player userGameData(T  event){

        if(event.getClass() == SlashCommandInteractionEvent.class){
            switch (((SlashCommandInteractionEvent) event).getName()){
                case "21" -> {
                    User user = ((SlashCommandInteractionEvent) event).getUser();
                    PLayerData blackjackData = new PLayerData(user);
                    return blackjackData.getPLayerData();
                }
                //more features in the future
            }

            throw new RuntimeException("No games were found with this name!");
        }
        if(event.getClass() == MessageReceivedEvent.class){
            Message m = ((MessageReceivedEvent) event).getMessage();
            String s = m.getContentRaw();

            User user = ((MessageReceivedEvent) event).getAuthor();
            PLayerData blackjackData = new PLayerData(user);
            return blackjackData.getPLayerData();

        }

        return null;
    }
    public Player userGameData(User user){
        PLayerData blackjackData = new PLayerData(user);
        return blackjackData.getPLayerData();
    }
}
