package veniamin_arefev.tabmodifier.commands;

import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.command.spec.*;
import org.spongepowered.api.text.*;
import org.spongepowered.api.text.format.TextColors;
import veniamin_arefev.tabmodifier.TabModifier;

public class CommandMain implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource Sender, CommandContext args) throws CommandException {
        if (!Sender.hasPermission("tabmodifier.use")) {
            Sender.sendMessage(Text.of(TextColors.RED, "you do not have permission to use this command"));
            return CommandResult.success();
        }
        Sender.sendMessage(Text.of(TextColors.GOLD, "Plugin Info: "));
        Sender.sendMessage(Text.of(TextColors.GOLD, "Name: TabModifier"));
        Sender.sendMessage(Text.of(TextColors.GOLD, "Version: v1.7.0"));
        Sender.sendMessage(Text.of(TextColors.GOLD, "Author: NipoCN and Veniamin_arefev"));
        return CommandResult.success();
    }
}
