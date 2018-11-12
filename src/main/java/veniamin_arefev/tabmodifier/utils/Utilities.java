package veniamin_arefev.tabmodifier.utils;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.GroupData;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import veniamin_arefev.tabmodifier.TabModifier;
import veniamin_arefev.tabmodifier.config.Config;

import java.util.Optional;

public class Utilities {

    private static Utilities instance;
    private boolean isPrefixEnabled;
    private boolean isSuffixEnabled;
    private String defaultPrefix;
    private String defaultSuffix;
    private boolean defaultPrefixUseFormatting;
    private boolean defaultSuffixUseFormatting;
    private String prefixFormat;
    private String suffixFormat;


    public static Utilities getInstance() {
        if (instance == null) {
            instance = new Utilities();
        }
        return instance;
    }

    public void updateAllPlayers() {
        isPrefixEnabled = Config.getInstance().isPrefixEnabled();
        isSuffixEnabled = Config.getInstance().isSuffixEnabled();
        defaultPrefix = Config.getInstance().getPrefix();
        defaultSuffix = Config.getInstance().getSuffix();
        defaultPrefixUseFormatting = Config.getInstance().isDefaultPrefixUseFormatting();
        defaultSuffixUseFormatting = Config.getInstance().isDefaultSuffixUseFormatting();
        prefixFormat = Config.getInstance().getPrefixFormat();
        suffixFormat = Config.getInstance().getSuffixFormat();
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            updateTargetPlayer(player);
            player.getTabList().setHeaderAndFooter(
                    TextSerializers.FORMATTING_CODE.deserialize(Config.getInstance().getHeader()),
                    TextSerializers.FORMATTING_CODE.deserialize(Config.getInstance().getFooter())
            );

        }
    }

    public void updateTargetPlayer(Player player) {
        Optional<ProviderRegistration<LuckPermsApi>> provider = Sponge.getServiceManager().getRegistration(LuckPermsApi.class);
        Optional<ProviderRegistration<NucleusAPI>> provider1 = Sponge.getServiceManager().getRegistration(NucleusAPI.class);
        if (provider.isPresent()) {
            //get luckperms api instance
            LuckPermsApi api = provider.get().getProvider();
            String prefix, suffix;
            //update whole tablist
            for (Player players : Sponge.getServer().getOnlinePlayers()) {
                User user = api.getUser(players.getUniqueId());
                //get prefix & deal with it
                prefix = getUniquePrefix(user, api.getContextsForPlayer(players));
                if (prefix == null) {
                    prefix = getGroupPrefix(api.getGroup(user.getPrimaryGroup()));
                    TabModifier.getInstance().logger.info("Group quarry");
                    if (prefix == null) {
                        //use default prefix
                        if (defaultPrefixUseFormatting) {
                            prefix = getFormattedPrefix(defaultPrefix);
                        } else prefix = defaultPrefix;
                    } else prefix = getFormattedPrefix(prefix);
                } else prefix = getFormattedPrefix(prefix);

                //get suffix & deal with it
                suffix = getUniqueSuffix(user, api.getContextsForPlayer(players));
                if (suffix == null) {
                    suffix = getGroupSuffix(api.getGroup(user.getPrimaryGroup()));
                    TabModifier.getInstance().logger.info("Group quarry");
                    if (suffix == null) {
                        //use default suffix
                        if (defaultSuffixUseFormatting) {
                            suffix = getFormattedSuffix(defaultSuffix);
                        } else suffix = defaultSuffix;
                    } else suffix = getFormattedSuffix(suffix);
                } else suffix = getFormattedSuffix(suffix);
                //refresh tablist
                //player.getTabList().getEntry(player.getUniqueId()).get().setDisplayName();
                if (isPrefixEnabled) {
                    if (isSuffixEnabled) {
                        //prefix + name + suffix
                        player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(prefix + players.getName() + suffix + "  ")
                        );
                    } else {
                        //prefix + name
                        player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(prefix + players.getName() + "  ")
                        );
                    }
                } else {
                    if (isSuffixEnabled) {
                        //name + suffix
                        player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(players.getName() + suffix + "  ")
                        );
                    } else {
                        //only name
                        player.getTabList().getEntry(players.getUniqueId()).get().setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(players.getName() + "  ")
                        );
                    }
                }
            }
        } else {
            TabModifier.getInstance().logger.error("LuckPerms not found");
        }
    }

    public String getUniquePrefix(User user, Contexts contexts) {
        UserData userdata = user.getCachedData();
        MetaData metadata = userdata.getMetaData(contexts);
        return metadata.getPrefix();
    }

    public String getUniqueSuffix(User user, Contexts contexts) {
        UserData userdata = user.getCachedData();
        MetaData metadata = userdata.getMetaData(contexts);
        return metadata.getSuffix();
    }

    public String getGroupPrefix(Group group) {
        GroupData groupdata = group.getCachedData();
        MetaData metadata = groupdata.getMetaData(Contexts.global());
        return metadata.getPrefix();
    }

    public String getGroupSuffix(Group group) {
        GroupData groupdata = group.getCachedData();
        MetaData metadata = groupdata.getMetaData(Contexts.global());
        return metadata.getSuffix();
    }

    public String getFormattedPrefix(String prefix) {
        return prefixFormat.replaceFirst("\\p{Punct}prefix\\p{Punct}",prefix);
    }

    public String getFormattedSuffix(String suffix) {
        return suffixFormat.replaceFirst("\\p{Punct}suffix\\p{Punct}",suffix);
    }
}