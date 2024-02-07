package Games;

import FileManagement.PLayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RockPaperScissors extends Game{
    private PLayerData pLayerData;
    private Player player;
    //challenger, challenger, accepted


    //challenger, challenged, bet
    public static List<Bets> betsAndChallenges = new ArrayList<>();

    public RockPaperScissors(User InitiatorUser, MessageReceivedEvent event){
        super(InitiatorUser);

        pLayerData = new PLayerData(event.getAuthor());
        this.player = super.getPlayer();
    }
    public RockPaperScissors(User InitiatorUser, Event event) throws UnexpectedException {
        super(InitiatorUser);

        if(event.getClass() == ButtonInteractionEvent.class){
            pLayerData = new PLayerData(((ButtonInteractionEvent) event).getUser());
        } else if (event.getClass()== SlashCommandInteractionEvent.class) {
            pLayerData = new PLayerData(((SlashCommandInteractionEvent)event).getUser());
        }else{
            throw new UnexpectedException("Invalid event type entered in constructor.");
        }

        this.player = super.getPlayer();
    }
    public MessageEmbed accept(User challenger, User challenged){ //accepter cagiriyor
        Player challengerPlayer = new Player(challenger);
        if(checkChallenge(challenger, challenged)){
            String description;
            Bets bet =getBet(challenger, challenged);
            long challengerBalance = challengerPlayer.getMoney();
            long challengedBalance = player.getMoney();
            long amount = bet.getStandingBet();
            if(challengedBalance-amount<0){
                description = "Yetersiz bakiye.";
                stringToEmbed("","",description, Color.red);
            }
            if(challengerBalance-amount<0){
                description =challenger.getName() +" kisisi yeterli paraya sahip degil.";
                return stringToEmbed("", "", description, Color.red);
            }
            return play(challenger, amount);

        }
        return stringToEmbed("","","<@"+challenger.getId()+"> ile herhangi bir iddia mevcut degil.", Color.red);
    }
    public MessageEmbed challenge(User challenged, long amount, SlashCommandInteractionEvent event){
        String title;
        String description;
        String author;
        Color color;
        if(player.getId().equals(challenged.getId())){
            description ="**Kendinizi duelloya cagiramazsiniz!**";
            color = Color.red;
            return stringToEmbed("","",description,color);
        }
        PLayerData challengedData = new PLayerData(challenged);
        long challengedBalance =challengedData.getPLayerData().getMoney();
        long playerBalance = getPlayer().getMoney();
        String challengedId = challenged.getId();
        if(playerBalance-amount<0){
            description ="Yetersiz bakiye.";
            color =Color.red;
            return stringToEmbed("","",description,color);
        }
        if(challengedBalance-amount<0){
            description =challenged.getName() +" kisisi yeterli paraya sahip degil.";
            color =Color.red;
            return stringToEmbed("","",description,color);
        }
        addChallenge(event.getUser(), challenged, amount);
        title ="**DUELLO DAVETI!**";
        description ="<@"+player.getId()+"> kisisi <@"+challengedId
                +"> kisisini tas kagit makas oynamaya cagiriyor!\n" +
                "Aktif bahis: **" +amount+"**";
        color = Color.cyan;
        return stringToEmbed(title,"",description,color);
    }
    public static boolean checkChallenge(User Challenger, User Challenged){
        return betsAndChallenges.stream()
                .anyMatch(bac -> bac.getChallengerID().equals(Challenger.getId())
                        &&bac.getChallengedID().equals(Challenged.getId()));
    }
    public static void addChallenge(User challenger, User challenged, long bet){
         //displays whether the challenged player and the bet
        if(checkChallenge(challenger, challenged)){
            betsAndChallenges.stream().filter(bac-> checkChallenge(challenger, challenged))
                    .forEach(bac-> bac.setStandingBet(bet));
        }else{
            betsAndChallenges.add(new Bets(challenger.getId(), challenged.getId(), bet));
        }
    }
    public static Bets getBet(User challenger, User challenged){
        if(checkChallenge(challenger, challenged)){
        return betsAndChallenges.stream()
                .filter(bac -> bac.getChallengerID().equals(challenger.getId())
                        && bac.getChallengedID().equals(challenged.getId()))
                .findFirst().get();
        }
        throw new NullPointerException("No challenges exist with the given parameters! getBet();");
    }

    public MessageEmbed play(User challenger, long bet){
        PLayerData challengerData = new PLayerData(challenger); //to load the files
        Player challengerPLayer = challengerData.getPLayerData();
        long challengerBalance = challengerPLayer.getMoney();
        long challengedBalance = player.getMoney();
        String description;
        Color color;
        String title;
        String[] rps = new String[]{":rock:", ":scissors:", ":scroll:"};
        //subtract balance
        challengerBalance -= bet;
        challengedBalance -= bet;
//        challengerData.logData(challengerPLayer);
//        pLayerData.logData(player);
        int challengerTurn = new Random().nextInt(0,2);
        int challengedTurn = new Random().nextInt(0,2);
        String outputMessage = "**"+challengerPLayer.getName()+ "** "+rps[challengerTurn] +" yapti!\n**" +
                player.getName()+"** "+ rps[challengedTurn]+" yapti!\n";
        if(challengedTurn==challengerTurn){
            title="**BERABERE!**";
            color =Color.white;
            challengedBalance +=bet;
            challengerBalance +=bet;
        }else if(gameResult(challengerTurn, challengedTurn)){
            challengerBalance +=2*bet;
            title="**"+challengerPLayer.getName()+ " KAZANDI!**\n";
            color=Color.magenta;
        }else{
            challengedBalance +=2*bet;
            title ="**"+player.getName()+ " KAZANDI!**\n";
            color =Color.MAGENTA;
        }
        player.setMoney(challengedBalance);
        challengerPLayer.setMoney(challengerBalance);

        challengerData.logData(challengerPLayer);
        pLayerData.logData(player);
        description =outputMessage+ "**"+challenger.getName() +"** yeni bakiye: **" +challengerBalance +"**\n" +
                "**"+player.getName() +"**  yeni bakiye: **" +challengedBalance +"**";
        return stringToEmbed(title,"",description,color);
    }
    private boolean gameResult(int challenger, int challenged){
        //true if challenged wins, false if challenged looses
        return challenged-challenger > 0;
    }

    //GETTER AND SETTER

    public PLayerData getpLayerData() {
        return pLayerData;
    }

    public void setpLayerData(PLayerData pLayerData) {
        this.pLayerData = pLayerData;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
