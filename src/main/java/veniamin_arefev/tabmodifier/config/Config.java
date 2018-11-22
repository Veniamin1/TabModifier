package veniamin_arefev.tabmodifier.config;

import veniamin_arefev.tabmodifier.TabModifier;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Config {
    private Path ConfigFile = Paths.get(TabModifier.getInstance().getConfigDir() + "/config.conf");
    private ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(ConfigFile).build();
    private CommentedConfigurationNode configNode;
    private static Config confinstance;

    public static Config getInstance() {
        if (confinstance == null) {
            confinstance = new Config();
        }
        return confinstance;
    }

    public void loadConfig() {
        if (!Files.exists(ConfigFile)) {
            try {
                Files.createFile(ConfigFile);
                configNode = loader.load();
                setup();
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            load();
        }
    }

    public void load() {
        try {
            configNode = loader.load();
            if (getPrefixFormat().lastIndexOf("{prefix}") == -1){
                TabModifier.getInstance().logger.error("Incorrect prefix formatting, replaced it by default");
                configNode.getNode("Formatting", "prefixformat").setValue("{prefix} ");
            }
            if (getSuffixFormat().lastIndexOf("{suffix}") == -1){
                TabModifier.getInstance().logger.error("Incorrect suffix formatting, replaced it by default");
                configNode.getNode("Formatting", "suffixformat").setValue("{suffix} ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            loader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        configNode.getNode("Default").setComment("Set prefix and suffix when user/group's unique prefix/suffix is missing/unset");
        configNode.getNode("Default", "prefix").setValue("§a[default]");
        configNode.getNode("Default", "suffix").setValue("§9[default]");
        configNode.getNode("Feature").setComment("Some features settings");
        configNode.getNode("Feature", "enableprefix").setValue("true");
        configNode.getNode("Feature", "enablesuffix").setValue("true");
        configNode.getNode("Feature").getNode("Vanish").setComment("Vanish properties");
        configNode.getNode("Feature").getNode("Vanish", "preprefix").setComment("This prefix will be displayed" +
                " before player prefix, must be with space at the end");
        configNode.getNode("Feature").getNode("Vanish", "preprefix").setValue("§7§o[Vanished]§r ");
        configNode.getNode("Feature").getNode("Vanish", "enablehidingvanishedplayers").setValue("true");
        configNode.getNode("Feature").getNode("Vanish", "showvanishedplayerstopermittedplayers").setComment(
                "Show other permitted players, that you are vanished");
        configNode.getNode("Feature").getNode("Vanish", "showvanishedplayerstopermittedplayers").setValue("true");
        configNode.getNode("Feature").getNode("Vanish", "permission").setComment("Default is nucleus permission");
        configNode.getNode("Feature").getNode("Vanish", "permission").setValue("nucleus.vanish.see");
        configNode.getNode("Formatting").setComment("Formatting for prefix and suffix " +
                "\n {prefix} and {suffix} will be replaced by prefix and suffix");
        configNode.getNode("Formatting", "shoulddefaultprefixuseformatting").setValue("true");
        configNode.getNode("Formatting", "shoulddefaultsuffixuseformatting").setValue("true");
        configNode.getNode("Formatting", "prefixformat").setValue("{prefix}§r ");
        configNode.getNode("Formatting", "suffixformat").setValue(" {suffix}");
        configNode.getNode("header").setComment("Set header of tablist");
        configNode.getNode("header").setValue("This is Header");
        configNode.getNode("footer").setComment("Set footer of tablist");
        configNode.getNode("footer").setValue("This is Footer");
    }

    public String getPrefix() {
        return configNode.getNode("Default", "prefix").getString();
    }

    public String getSuffix() {
        return configNode.getNode("Default", "suffix").getString();
    }

    public boolean isPrefixEnabled() {
        return configNode.getNode("Feature", "enableprefix").getBoolean();
    }

    public String getPrePrefix() {
        return configNode.getNode("Feature").getNode("Vanish", "preprefix").getString();
    }

    public boolean isVanishEnabled() {
        return configNode.getNode("Feature").getNode("Vanish", "enablehidingvanishedplayers").getBoolean();
    }

    public boolean isVanishShowEnabled() {
        return configNode.getNode("Feature").getNode("Vanish", "showvanishedplayerstopermittedplayers").getBoolean();
    }

    public String getVanishPermission() {
        return configNode.getNode("Default", "suffix").getString();
    }

    public boolean isSuffixEnabled() {
        return configNode.getNode("Feature", "enablesuffix").getBoolean();
    }

    public boolean isDefaultPrefixUseFormatting() {
        return configNode.getNode("Formatting", "shoulddefaultprefixuseformatting").getBoolean();
    }

    public boolean isDefaultSuffixUseFormatting() {
        return configNode.getNode("Formatting", "shoulddefaultsuffixuseformatting").getBoolean();
    }

    public String getPrefixFormat(){
        return configNode.getNode("Formatting", "prefixformat").getString();
    }

    public String getSuffixFormat(){
        return configNode.getNode("Formatting", "suffixformat").getString();
    }

    public String getHeader() {
        return configNode.getNode("header").getString();
    }

    public String getFooter() {
        return configNode.getNode("footer").getString();
    }
}
