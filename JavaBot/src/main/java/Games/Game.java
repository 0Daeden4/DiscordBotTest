package Games;

import FileManagement.GrabPlayerData;
import FileManagement.PLayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public abstract class Game {
    private Player player = null;
    public Event event = null;
    public Game(){
        //initialize at main and pass the JDABuilder in it
    }
    public Game(MessageReceivedEvent messageReceivedEvent){
        GrabPlayerData dataGrabber = new GrabPlayerData(messageReceivedEvent);
        player = dataGrabber.userGameData(messageReceivedEvent);
        event = messageReceivedEvent;
    }
    public Game(SlashCommandInteractionEvent slashCommandInteractionEvent){
        GrabPlayerData dataGrabber = new GrabPlayerData(slashCommandInteractionEvent.getUser());
        player = dataGrabber.userGameData(slashCommandInteractionEvent.getUser());
        event = slashCommandInteractionEvent;
    }
    public Game(User user){ //for rock paper scissors
        GrabPlayerData dataGrabber = new GrabPlayerData(user);
        player = dataGrabber.userGameData(user);
    }

    public long getMoney(){
        return player.getMoney();
    }
    public String balanceToString(){
        return player.getBalance();
    }

    public void setPlayerMoney(long amount){
        player.setMoney(amount);
    }

    public Player getPlayer() {
        return player;
    }

    public MessageEmbed stringToEmbed(String title, String author, String description, Color color){
        EmbedBuilder eb = new EmbedBuilder();
        if(!title.isEmpty()) eb.setTitle(title);
        if(!author.isEmpty()) eb.setAuthor(author);
        if(!description.isEmpty()) eb.setDescription(description);
        if(color!=null) eb.setColor(color);
        MessageEmbed embed = eb.build();
        eb.clear();
        return embed;
    }


}
