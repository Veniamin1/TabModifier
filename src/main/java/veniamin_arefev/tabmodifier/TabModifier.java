package veniamin_arefev.tabmodifier;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import veniamin_arefev.tabmodifier.commands.CommandMain;
import veniamin_arefev.tabmodifier.commands.CommandReload;
import veniamin_arefev.tabmodifier.config.Config;
import veniamin_arefev.tabmodifier.utils.Utilities;

import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "tabmodifier",
        name = "Tab Modifier",
        description = "This plugin provides configurations for TAB multiplayer GUI",
        version = "1.5.0",
        authors = "NipoCN and Veniamin_arefev",
        dependencies = {
                @Dependency(id = "luckperms", optional = false),
                @Dependency(id = "nucleus", optional = true)
        }
        )
public class TabModifier {
    @Inject
    private Game game;

    @Inject
    public Logger logger;

    @Inject
    @org.spongepowered.api.config.ConfigDir(sharedRoot = false)
    private Path ConfigDir;

    private static TabModifier instance;

    public static TabModifier getInstance() {
        if (instance == null) {
            instance = new TabModifier();
        }
        return instance;
    }

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {
        instance = this;
        if (!Files.exists(ConfigDir)) {
            try {
                Files.createDirectories(ConfigDir);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Config.getInstance().loadConfig();
        CommandSpec MainCommand = CommandSpec.builder()
                .description(Text.of("show version and author of plugin"))
                .executor(new CommandMain())
                .child(CommandSpec.builder()
                        .description(Text.of("reload plugin"))
                        .executor(new CommandReload())
                        .build(), "reload"
                )
                .build();
        game.getCommandManager().register(this, MainCommand, "tabmodifier", "tab");
        logger.info("---------------------------------------------------------");
        logger.info("Thank you for using TabModifier made by NipoCN and reworked by Veniamin_arefev");
        logger.info("---------------------------------------------------------");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        this.refresh();
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event, @Root Player player) {
        this.refresh();
    }

    public Path getConfigDir() {
        return ConfigDir;
    }

    public void refresh() {
        //create task
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            Utilities.getInstance().updateAllPlayers();
        }).delayTicks(20).submit(TabModifier.getInstance());
    }
}