package Main;

import Commands.CheguListener;


import Commands.GameMessageListener;
import Commands.SlashCommands;
import FileManagement.LogBets;
import Games.Bets;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Bets> betsAndChallenges = new ArrayList<>();

    public static void main(String[] args) {
        //save or load bets and challenges
        new LogBets(betsAndChallenges, "betsAndChallenges");
        JDABuilder builder = JDABuilder.createDefault(System.getenv("DISCORD_API_KEY"));
        JDA bot = builder.build();
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        new SlashCommands(bot);
        builder.addEventListeners(new CheguListener());
        builder.addEventListeners(new GameMessageListener());


        try {
            builder.build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException("A fail has occurred while building the bot.");
        }
    }
}
