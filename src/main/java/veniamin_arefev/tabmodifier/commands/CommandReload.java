package veniamin_arefev.tabmodifier.commands;

import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.command.spec.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import veniamin_arefev.tabmodifier.TabModifier;
import veniamin_arefev.tabmodifier.config.Config;

public class CommandReload implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource Sender, CommandContext args) throws CommandException {
        if (!Sender.hasPermission("tabmodifier.reload")) {
            Sender.sendMessage(Text.of(TextColors.RED, "you do not have permission to use this command"));
            return CommandResult.success();
        }
        Config.getInstance().load();
        TabModifier.getInstance().refresh();
        Sender.sendMessage(Text.of(TextColors.RED, "Sucessfully reloaded TabModifier"));
        return CommandResult.success();
    }
}
