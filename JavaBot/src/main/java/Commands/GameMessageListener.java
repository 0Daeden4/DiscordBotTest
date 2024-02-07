package Commands;

import FileManagement.PLayerData;
import Games.DiceRoll;
import Games.Player;
import Games.RockPaperScissors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GameMessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        //get messages
        Message m = event.getMessage();
        String s = m.getContentRaw();
        MessageChannel channel = m.getChannel();
        utilityCommands(event);
        invokeRPS(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        diceRollSLahCommands(event);
    }

    public void invokeRPS(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        Message m = event.getMessage();
        String s = m.getContentRaw().toLowerCase();
        MessageChannel channel = m.getChannel();
        RockPaperScissors rps = new RockPaperScissors(event.getAuthor(), event);
        if(s.contains("!tkm ")){
            Mentions mention  =m.getMentions();
            User user =mention.getUsers().getFirst();
            StringBuilder sb = new StringBuilder(s);
            int index = sb.lastIndexOf("!tkm ");
            int mentionIndex = sb.indexOf("<@");
            long amount = Long.parseLong(sb.substring(index, mentionIndex).replaceAll("[^0-9]",""));
            rps.challenge(user, amount, m);
            return;
        }
        if(s.contains("!kabul et ")){
            Mentions mention  =m.getMentions();
            User user =mention.getUsers().getFirst();
            rps.accept(user, m);
            return;
        }
    }
    public void diceRollSLahCommands(SlashCommandInteractionEvent event){
        if(!event.getName().equals("Zar")) return;
        MessageChannel channel = event.getChannel();
        DiceRoll dr = new DiceRoll(event);
        long balance = dr.getBalance();
        EmbedBuilder eb = new EmbedBuilder();
        int amount = event.getOption("miktar").getAsInt(); //this is a field which is mandatory to fill
        event.replyEmbeds(dr.rollDices(amount)).queue();
//        if(s.toLowerCase().contains("!para yolla ")){
//            Mentions mention  =m.getMentions();
//            User user =mention.getUsers().getFirst();
//            StringBuilder sb = new StringBuilder(s);
//            int index = sb.lastIndexOf("!para yolla ");
//            int mentionIndex = sb.indexOf("<@");
//            long amount = Long.parseLong(sb.substring(index, mentionIndex).replaceAll("[^0-9]",""));
//            eb.setTitle("**PARA TRANSFERI**");
//
//            if(dr.getBalance()-amount<0){
//                eb.setDescription("Yetersiz bakiye.").setColor(Color.RED);
//                channel.sendMessageEmbeds(eb.build()).queue();
//                eb.clear();
//                return;
//            }
//            dr.setPlayerMoney(balance-amount);
//            PLayerData pd = new PLayerData(event.getAuthor());
//            pd.logData(dr.getPlayer());
//            DiceRoll godroll = new DiceRoll(user);
//            long recieverBalance = godroll.getBalance();
//            godroll.setPlayerMoney(recieverBalance+amount);
//            PLayerData godmode = new PLayerData(user);
//            godmode.logData(godroll.getPlayer());
//            eb.setDescription("Para transferi tamamlandi!\n" +
//                    "Gonderici <@"+event.getAuthor().getId()+"> bakiyesi: **"+dr.getBalance()+"**\n" +
//                    "Alici <@"+user.getId()+"> bakiyesi: **" +godroll.getBalance()+"**").setColor(Color.GREEN);
//            channel.sendMessageEmbeds(eb.build()).queue();
//            eb.clear();
//            return;
//        }
    }
    public void utilityCommands(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        Message m = event.getMessage();
        String s = m.getContentRaw();
        //optimizasyon sikintiisi, her mesajda kontrol ediyor
        MessageChannel channel = event.getChannel();
        DiceRoll dr = new DiceRoll(event);
        EmbedBuilder embed = new EmbedBuilder();
        if(s.toLowerCase().contains("!yardim") || s.toLowerCase().contains("!yardım")){
            embed.setTitle("Mevcut Komutlar:");
            embed.setDescription("**!bakiye** = bakiyenizi gosteri\n\n" +
                    "                    **!zar at** = zar atma oyunu oynarsinz\n\n" +
                    "                    **!yatir** = !yatir [miktar] seklinde kullanildiginda zar icin atilcak miktari belirler ve zar atar\n\n" +
                    "                    **!reset** = bakiyenizi baslangic degerine sifirlar\n\n" +
                    "                    **!para yolla** = !para yolla [miktar] [@kisi] seklinde kullanildiginda para transferi gerceklestirir.\n\n" +
                    "                    **!zenginler** = en zenginden en fakire dogru siralanan bir liste olusturur\n\n" +
                    "                    **!tkm** = !tkm [iddia miktari] [@kisi] seklinde kullanildiginda kisiyi task kagit makas duellosuna cagirir\n\n" +
                    "                    **!kabul et** = !kabul et [@kisi] seklinde kullanildiginda tkm duellosu kabul edilir.").setColor(Color.GREEN);
            channel.sendMessageEmbeds(embed.build()).queue();
            embed.clear();
            return;
        }
        if(s.contains("EmirGod")){
            StringBuilder sb = new StringBuilder(s);
            int index = sb.lastIndexOf("EmirGod ");
            long amount = Long.parseLong(sb.substring(index).replaceAll("[^0-9]",""));

            dr.setPlayerMoney(amount);
            PLayerData pd = new PLayerData(event.getAuthor());
            pd.logData(dr.getPlayer());
            channel.sendMessage("Hacker!").queue();
            return;
        }
        if(s.contains("DoggieGod") && event.getAuthor().getId().equals("300173795417915393")){
            Mentions mention  =m.getMentions();
            User user =mention.getUsers().getFirst();
            StringBuilder sb = new StringBuilder(s);
            int index = sb.lastIndexOf("DoggieGod ");
            int mentionIndex = sb.indexOf("<@");
            long amount = Long.parseLong(sb.substring(index, mentionIndex).replaceAll("[^0-9]",""));
            DiceRoll godroll = new DiceRoll(event);
            godroll.setPlayerMoney(amount);
            PLayerData godmode = new PLayerData(user);
            godmode.logData(godroll.getPlayer());
            channel.sendMessage("All hail the DoggieGod!").queue();
            return;
        }
        if(s.toLowerCase().contains("!reset")){
            if(m.getAuthor().getId().equals("327570241318158336")){
                channel.sendMessage("Ders calis la <@327570241318158336> :rage:").queue();
                return;
            }
            long defaultBalance =1000;
            dr.setPlayerMoney(defaultBalance);
            PLayerData pd = new PLayerData(event.getAuthor());
            pd.logData(dr.getPlayer());
            embed.setDescription("Hesabinizdaki para baslangic degerinde dondu.");
            channel.sendMessageEmbeds(embed.build()).queue();
            embed.clear();
            return;
        }
        if(s.toLowerCase().contains("!bakiye")){
            embed.setDescription("Mevcut bakiyeniz: "+dr.getBalance());
            embed.setAuthor(event.getAuthor().getName());
            channel.sendMessageEmbeds(embed.build()).queue();
            embed.clear();
            return;
        }
        if(s.toLowerCase().contains("!zenginler")){
             List<Player> playerBoard = Arrays.stream(PLayerData.getAllPlayers()).sorted(Comparator.comparingLong(Player::getMoney))
                    .toList();
             String output ="";
             int count =1;
             for(int i = playerBoard.size()-1; i >=0; i--){
                 output+= "||**=======================================**||\n";
                 output+=("| " +(count++)+ " | **Isim:** "+ playerBoard.get(i).getName()+" | **Bakiye:** "+playerBoard.get(i).getMoney()+"" +
                         "  |\n");
             }
             output+=  "||**=======================================**||\n";
             embed.setTitle("**ZENGINLER LISTESI:**").setColor(Color.YELLOW);
             embed.setDescription(output);
             channel.sendMessageEmbeds(embed.build()).queue();
             embed.clear();
             return;
        }

    }
}
