package Commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;


public class SlashCommands {
    public SlashCommands(JDA bot){
        bot.updateCommands().addCommands(
                Commands.slash("zar", "2 tane zar atar.")
                        .addOption(OptionType.NUMBER, "miktar", "Yatirilacak para miktari", true)
        ).queue();
    }
}
