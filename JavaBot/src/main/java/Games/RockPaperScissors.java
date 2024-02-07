package Games;

import FileManagement.PLayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
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
    public void accept(User challenger, Message m){ //accepter cagiriyor
        Player challengerPlayer = new Player(challenger);
        MessageChannel channel = m.getChannel();
        if(checkChallenge(challenger, m.getAuthor())){
            Bets bet =getBet(challenger, m.getAuthor());
            long challengerBalance = challengerPlayer.getMoney();
            long challengedBalance = player.getMoney();
            long amount = bet.getStandingBet();
            if(challengedBalance-amount<0){
                channel.sendMessage("Yetersiz bakiye.").queue();
                return;
            }
            if(challengerBalance-amount<0){
                channel.sendMessage(challenger.getName() +" kisisi yeterli paraya sahip degil.").queue();
                return;
            }
            play(challenger, m, amount);
            return;
        }
        channel.sendMessage("<@"+challenger.getId()+"> ile herhangi bir iddia mevcut degil.").queue();
    }
    public void challenge(User challenged, long amount, Message m){
        MessageChannel channel = m.getChannel();
        EmbedBuilder eb = new EmbedBuilder();
        if(player.getId().equals(challenged.getId())){
            eb.setDescription("**Kendinizi duelloya cagiramazsiniz!**").setColor(Color.RED);
            channel.sendMessageEmbeds(eb.build()).queue();
            eb.clear();
            return;
        }
        PLayerData challengedData = new PLayerData(challenged);
        long challengedBalance =challengedData.getPLayerData().getMoney();
        long playerBalance = getPlayer().getMoney();
        String challengedId = challenged.getId();
        if(playerBalance-amount<0){
            eb.setDescription("Yetersiz bakiye.").setColor(Color.RED);
            channel.sendMessageEmbeds(eb.build()).queue();
            eb.clear();
            return;
        }
        if(challengedBalance-amount<0){
            eb.setDescription(challenged.getName() +" kisisi yeterli paraya sahip degil.").setColor(Color.RED);
            channel.sendMessageEmbeds(eb.build()).queue();
            eb.clear();
            return;
        }
        addChallenge(m.getAuthor(), challenged, amount);
        eb.setTitle("**DUELLO DAVETI!**");
        eb.setDescription("<@"+player.getId()+"> kisisi <@"+challengedId
                +"> kisisini tas kagit makas oynamaya cagiriyor!\n" +
                "Aktif bahis: **" +amount+"**").setColor(Color.CYAN);
        channel.sendMessageEmbeds(eb.build()).queue();
        eb.clear();
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

    public void play(User challenger, Message m, long bet){
        PLayerData challengerData = new PLayerData(challenger); //to load the files
        Player challengerPLayer = challengerData.getPLayerData();
        long challengerBalance = challengerPLayer.getMoney();
        long challengedBalance = player.getMoney();

        String[] rps = new String[]{":rock:", ":scissors:", ":scroll:"};
        MessageChannel channel = m.getChannel();
        //subtract balance
        challengerBalance -= bet;
        challengedBalance -= bet;
//        challengerData.logData(challengerPLayer);
//        pLayerData.logData(player);
        int challengerTurn = new Random().nextInt(0,2);
        int challengedTurn = new Random().nextInt(0,2);

        EmbedBuilder eb = new EmbedBuilder();
        String outputMessage = "**"+challengerPLayer.getName()+ "** "+rps[challengerTurn] +" yapti!\n**" +
                player.getName()+"** "+ rps[challengedTurn]+" yapti!\n";
        if(challengedTurn==challengerTurn){
            eb.setTitle("**BERABERE!**");
            challengedBalance +=bet;
            challengerBalance +=bet;
        }else if(gameResult(challengerTurn, challengedTurn)){
            challengerBalance +=2*bet;
            eb.setTitle("**"+challengerPLayer.getName()+ " KAZANDI!**\n").setColor(Color.MAGENTA);
        }else{
            challengedBalance +=2*bet;
            eb.setTitle("**"+player.getName()+ " KAZANDI!**\n").setColor(Color.MAGENTA);
        }
        player.setMoney(challengedBalance);
        challengerPLayer.setMoney(challengerBalance);

        challengerData.logData(challengerPLayer);
        pLayerData.logData(player);
        eb.setDescription(outputMessage+ "**"+challenger.getName() +"** yeni bakiye: **" +challengerBalance +"**\n" +
                "**"+player.getName() +"**  yeni bakiye: **" +challengedBalance +"**");
        channel.sendMessageEmbeds(eb.build()).queue();
        eb.clear();
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
