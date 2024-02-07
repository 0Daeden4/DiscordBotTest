package Games;

import FileManagement.PLayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public class DiceRoll extends Game{
    private PLayerData pLayerData;
    private Player player;

    public DiceRoll(MessageReceivedEvent messageEvent) {
        super(messageEvent);
        pLayerData = new PLayerData(messageEvent.getAuthor());
        player = getPlayer();
    }
    public DiceRoll(SlashCommandInteractionEvent slashEvent){
        super(slashEvent);
        pLayerData = new PLayerData(slashEvent.getUser());
        player = getPlayer();
    }

    public MessageEmbed rollDices(long bet){
        long balance = getMoney();
        if(balance-bet<0) return stringToEmbed("","","**Yetersiz Bakiye**", Color.red);
        int dice1 = new Random().nextInt(1,6);
        int dice2 = new Random().nextInt(1,6);

        String[] emojiArray = new String[]{":zero:",":one:",":two:",":three:",":four:",":five:",":six:"};
        String output = "Ilk zarin degeri: "+emojiArray[dice1] +"\n2. zarin degeri: " + emojiArray[dice2];
        balance-=bet;
        byte hasWon = 0;

        String title;
        Color embedColor;
        if(dice1 == dice2){
            switch (dice1){
                case 6->{setPlayerMoney(balance+bet*6);
                output+= "\nKazanilan deger: **" +bet*6+"**";
                }
                case 5->{setPlayerMoney(balance+bet*5);
                    output+= "\nKazanilan deger: **" +bet*5+"**";
                }
                case 4->{setPlayerMoney(balance+bet*4);
                    output+= "\nKazanilan deger: **" +bet*4+"**";
                }
                case 3->{setPlayerMoney(balance+bet*3);
                    output+= "\nKazanilan deger: **" +bet*3+"**";
                }
                case 2->{setPlayerMoney(balance+bet*2);
                    output+= "\nKazanilan deger: **" +bet*2+"**";
                }
                case 1->{setPlayerMoney(balance+bet*1);
                    output+= "\nKazanilan deger: **" +bet+"**";
                }
            }
        }else if(dice1==3&& dice2==1){
            setPlayerMoney(balance+bet*15);
            output+= "\nJACKPOT 31!!!!  Kazanilan deger:  **" +bet*15+"**";
            hasWon++;
        }else {
            setPlayerMoney(balance);
            output+= "\nKaybedilen miktar: **" +bet+"**";
            hasWon--;
        }
        if(hasWon<0){
            title= "**KAYBETTINIZ**";
            embedColor = Color.RED;
        }else if(hasWon>0){
            title = "**JACKPOT**";
            embedColor = Color.YELLOW;
        }else {
            title ="**KAZANDINIZ**";
            embedColor = Color.GREEN;
        }
        pLayerData.logData(player);
        return stringToEmbed(title, player.getName(),output +"\n\nYeni bakiyeniz: **" +balance+"**\n", embedColor );
    }

    public long getBalance(){
        return player.getMoney();
    }

}
