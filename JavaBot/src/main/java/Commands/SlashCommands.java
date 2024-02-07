package Commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;


public class SlashCommands {
    public SlashCommands(JDA bot){
        bot.updateCommands().addCommands(
                Commands.slash("zar", "2 tane zar atar.")
                        .addOption(OptionType.INTEGER, "miktar", "Yatirilacak para miktari", true)
        ).addCommands(
                Commands.slash("tkm","Sectiginiz bir kisiyi tas kagit makas oynamaya cagirir.")
                        .addOption(OptionType.USER,"kisi","Duelloya cagirilacak kisiyi belirler."
                                ,true)
                        .addOption(OptionType.INTEGER,"miktar","Iddiada oynanilacak miktari belirler."
                                ,true)
        ).queue();
    }
}
