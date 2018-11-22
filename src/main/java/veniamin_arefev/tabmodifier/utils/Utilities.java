package veniamin_arefev.tabmodifier.utils;

import me.lucko.luckperms.api.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.lucko.luckperms.api.caching.GroupData;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import veniamin_arefev.tabmodifier.TabModifier;
import veniamin_arefev.tabmodifier.config.Config;

import java.util.Optional;
import java.util.Random;

public class Utilities {

    private static Utilities instance;
    private boolean prefixEnabled;
    private boolean suffixEnabled;
    private boolean vanishEnabled;
    private boolean vanishShowEnabled;
    private String prePrefix;
    private String defaultPrefix;
    private String defaultSuffix;
    private boolean defaultPrefixUseFormatting;
    private boolean defaultSuffixUseFormatting;
    private String prefixFormat;
    private String suffixFormat;
    private String vanishPermission;


    public static Utilities getInstance() {
        if (instance == null) {
            instance = new Utilities();
        }
        return instance;
    }

    public void updateAllPlayers() {
        Config config = Config.getInstance();
        String header = config.getHeader();
        String footer = config.getFooter();
        prefixEnabled = config.isPrefixEnabled();
        suffixEnabled = config.isSuffixEnabled();
        defaultPrefix = config.getPrefix();
        defaultSuffix = config.getSuffix();
        defaultPrefixUseFormatting = config.isDefaultPrefixUseFormatting();
        defaultSuffixUseFormatting = config.isDefaultSuffixUseFormatting();
        prefixFormat = config.getPrefixFormat();
        suffixFormat = config.getSuffixFormat();
        vanishEnabled = config.isVanishEnabled();
        if (vanishEnabled){
            prePrefix = config.getPrePrefix();
            vanishShowEnabled = config.isVanishShowEnabled();
            vanishPermission = config.getVanishPermission();
        }
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            updateTargetPlayer(player);
            player.getTabList().setHeaderAndFooter(
                    TextSerializers.FORMATTING_CODE.deserialize(header),
                    TextSerializers.FORMATTING_CODE.deserialize(footer)
            );

        }
    }

    private void updateTargetPlayer(Player player) {
        Optional<ProviderRegistration<LuckPermsApi>> provider = Sponge.getServiceManager().getRegistration(LuckPermsApi.class);
        if (provider.isPresent()) {
            //configNode LuckPerms api instance
            LuckPermsApi api = provider.get().getProvider();
            String prefix, suffix;
            boolean hasPermission = false;
            if (vanishEnabled && vanishShowEnabled) {
                hasPermission = player.hasPermission(vanishPermission);
            }
            //update whole tabList of target player
            for (Player players : Sponge.getServer().getOnlinePlayers()) {
                if (vanishEnabled) {
                    if (player != players && vanishShowEnabled && !hasPermission) {
                        continue;
                    }
                }
                User user = api.getUser(players.getUniqueId());
                //get configNode prefix & deal with it
                prefix = getUniquePrefix(user, api.getContextsForPlayer(players));
                if (prefix == null) {
                    prefix = getGroupPrefix(api.getGroup(user.getPrimaryGroup()));
                    if (prefix == null) {
                        //use default prefix
                        if (defaultPrefixUseFormatting) {
                            prefix = getFormattedPrefix(defaultPrefix);
                        } else prefix = defaultPrefix;
                    } else prefix = getFormattedPrefix(prefix);
                } else prefix = getFormattedPrefix(prefix);

                //get configNode suffix & deal with it
                suffix = getUniqueSuffix(user, api.getContextsForPlayer(players));
                if (suffix == null) {
                    suffix = getGroupSuffix(api.getGroup(user.getPrimaryGroup()));
                    if (suffix == null) {
                        //use default suffix
                        if (defaultSuffixUseFormatting) {
                            suffix = getFormattedSuffix(defaultSuffix);
                        } else suffix = defaultSuffix;
                    } else suffix = getFormattedSuffix(suffix);
                } else suffix = getFormattedSuffix(suffix);
                //check for existing player entry and create it for vanished players
                TabListEntry tabListEntry;
                Optional<TabListEntry> optional = player.getTabList().getEntry(players.getUniqueId());
                if (player.getTabList().getEntry(players.getUniqueId()).isPresent()){
                    tabListEntry = player.getTabList().getEntry(players.getUniqueId()).get();
                } else {
                    tabListEntry = TabListEntry.builder()
                            .list(player.getTabList())
                            .displayName(players.getDisplayNameData().displayName().get())
                            .profile(players.getProfile())
                            .latency(new Random().nextInt(1100))
                            .build();
                    player.getTabList().addEntry(tabListEntry);
                }
                //refresh tabList
                if (prefixEnabled) {
                    if (suffixEnabled) {
                        //prefix + name + suffix
                        tabListEntry.setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(prefix + players.getName() + suffix + "  ")
                        );
                    } else {
                        //prefix + name
                        tabListEntry.setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(prefix + players.getName() + "  ")
                        );
                    }
                } else {
                    if (suffixEnabled) {
                        //name + suffix
                        tabListEntry.setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(players.getName() + suffix + "  ")
                        );
                    } else {
                        //only name
                        tabListEntry.setDisplayName(
                                TextSerializers.FORMATTING_CODE.deserialize(players.getName() + "  ")
                        );
                    }
                }
                //apply vanish prePrefix for already done display name
                if (vanishEnabled && players.get(Keys.VANISH).isPresent() && players.get(Keys.VANISH).get()){
                    tabListEntry.setDisplayName(TextSerializers.FORMATTING_CODE.deserialize(prePrefix)
                            .concat(tabListEntry.getDisplayName().get()));
                }
            }
        } else {
            TabModifier.getInstance().logger.error("LuckPerms not found");
        }
    }

    private String getUniquePrefix(User user, Contexts contexts) {
        UserData userdata = user.getCachedData();
        MetaData metadata = userdata.getMetaData(contexts);
        return metadata.getPrefix();
    }

    private String getUniqueSuffix(User user, Contexts contexts) {
        UserData userdata = user.getCachedData();
        MetaData metadata = userdata.getMetaData(contexts);
        return metadata.getSuffix();
    }

    private String getGroupPrefix(Group group) {
        GroupData groupdata = group.getCachedData();
        MetaData metadata = groupdata.getMetaData(Contexts.global());
        return metadata.getPrefix();
    }

    private String getGroupSuffix(Group group) {
        GroupData groupdata = group.getCachedData();
        MetaData metadata = groupdata.getMetaData(Contexts.global());
        return metadata.getSuffix();
    }

    private String getFormattedPrefix(String prefix) {
        return prefixFormat.replaceFirst("\\u007Bprefix\\u007D",prefix);
    }

    private String getFormattedSuffix(String suffix) {
        return suffixFormat.replaceFirst("\\u007Bsuffix\\u007D",suffix);
    }
}