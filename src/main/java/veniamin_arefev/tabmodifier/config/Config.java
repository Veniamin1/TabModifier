package veniamin_arefev.tabmodifier.config;

import ninja.leaping.configurate.ConfigurationOptions;
import veniamin_arefev.tabmodifier.TabModifier;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Config {
    public Path ConfigFile = Paths.get(TabModifier.getInstance().getConfigDir() + "/config.conf");
    public ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(ConfigFile).build();
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

    public CommentedConfigurationNode get() {
        return configNode;
    }

    public void setup() {
        get().getNode("Default").setComment("set prefix and suffix when user/group's unique prefix/suffix is missing/unset");
        get().getNode("Default", "prefix").setValue("&a[default]");
        get().getNode("Default", "suffix").setValue("&9[default]");
        get().getNode("Feature").setComment("disable some features");
        get().getNode("Feature", "enableprefix").setValue("true");
        get().getNode("Feature", "enablesuffix").setValue("true");
        get().getNode("Formatting").setComment("formatting for prefix and suffix " +
                "\\n {prefix} and {suffix} will be replaced by prefix and suffix");
        get().getNode("Formatting", "shoulddefaultprefixuseformatting").setValue("true");
        get().getNode("Formatting", "shoulddefaultsuffixuseformatting").setValue("true");
        get().getNode("Formatting", "prefixformat").setValue("{prefix} ");
        get().getNode("Formatting", "suffixformat").setValue(" {suffix}");
        get().getNode("header").setComment("set header of tablist");
        get().getNode("header").setValue("This is Header");
        get().getNode("footer").setComment("set footer of tablist");
        get().getNode("footer").setValue("This is Footer");
    }

    public String getPrefix() {
        return get().getNode("Default", "prefix").getString();
    }

    public String getSuffix() {
        return get().getNode("Default", "suffix").getString();
    }

    public boolean isPrefixEnabled() {
        return get().getNode("Feature", "enableprefix").getBoolean();
    }

    public boolean isSuffixEnabled() {
        return get().getNode("Feature", "enablesuffix").getBoolean();
    }

    public boolean isDefaultPrefixUseFormatting() {
        return get().getNode("Formatting", "shoulddefaultprefixuseformatting").getBoolean();
    }

    public boolean isDefaultSuffixUseFormatting() {
        return get().getNode("Formatting", "shoulddefaultsuffixuseformatting").getBoolean();
    }

    public String getPrefixFormat(){
        return get().getNode("Formatting", "prefixformat").getString();
    }

    public String getSuffixFormat(){
        return get().getNode("Formatting", "suffixformat").getString();
    }

    public String getHeader() {
        return get().getNode("header").getString();
    }

    public String getFooter() {
        return get().getNode("footer").getString();
    }
}
