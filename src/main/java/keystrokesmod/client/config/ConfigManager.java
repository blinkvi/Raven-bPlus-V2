package keystrokesmod.client.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import net.minecraft.client.Minecraft;

public class ConfigManager
{
    private final File configDirectory;
    private Config config;
    private final ArrayList<Config> configs;
    
    public ConfigManager() {
        this.configDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "keystrokes" + File.separator + "configs");
        this.configs = new ArrayList<Config>();
        if (!this.configDirectory.isDirectory()) {
            this.configDirectory.mkdirs();
        }
        this.discoverConfigs();
        final File defaultFile = new File(this.configDirectory, "default.bplus");
        this.config = new Config(defaultFile);
        if (!defaultFile.exists()) {
            this.save();
        }
    }
    
    public static boolean isOutdated(final File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);

            if (element != null && element.isJsonObject()) {
                JsonObject data = element.getAsJsonObject();
                return false;
            } else {
                System.err.println("JSON is not an object.");
                return true;
            }
        } catch (JsonSyntaxException | ClassCastException | IOException e) {
            e.printStackTrace();
            return true;
        }
    }
    
    public void discoverConfigs() {
        this.configs.clear();
        if (this.configDirectory.listFiles() == null || Objects.requireNonNull(this.configDirectory.listFiles()).length <= 0) {
            return;
        }
        for (final File file : Objects.requireNonNull(this.configDirectory.listFiles())) {
            if (file.getName().endsWith(".bplus") && !isOutdated(file)) {
                this.configs.add(new Config(new File(file.getPath())));
            }
        }
    }
    
    public Config getConfig() {
        return this.config;
    }
    
    public void save() {
        final JsonObject data = new JsonObject();
        data.addProperty("version", Raven.VERSION);
        data.addProperty("author", "Unknown");
        data.addProperty("notes", "");
        data.addProperty("intendedServer", "");
        data.addProperty("usedFor", (Number)0);
        data.addProperty("lastEditTime", (Number)System.currentTimeMillis());
        final JsonObject modules = new JsonObject();
        for (final ClientModule module : Raven.moduleManager.getModules()) {
            modules.add(module.getName(), (JsonElement)module.getConfigAsJson());
        }
        data.add("modules", (JsonElement)modules);
        this.config.save(data);
    }
    
    public void setConfig(final Config config) {
        this.config = config;
        final JsonObject data = config.getData().get("modules").getAsJsonObject();
        final List<ClientModule> knownModules = new ArrayList<ClientModule>(Raven.moduleManager.getModules());
        for (final ClientModule module : knownModules) {
            if (data.has(module.getName())) {
                module.applyConfigFromJson(data.get(module.getName()).getAsJsonObject());
            }
            else {
                module.resetToDefaults();
            }
        }
    }
    
    public void loadConfigByName(final String replace) {
        this.discoverConfigs();
        for (final Config config : this.configs) {
            if (config.getName().equals(replace)) {
                this.setConfig(config);
            }
        }
    }
    
    public ArrayList<Config> getConfigs() {
        this.discoverConfigs();
        return this.configs;
    }
    
    public void copyConfig(final Config config, final String s) {
        final File file = new File(this.configDirectory, s);
        final Config newConfig = new Config(file);
        newConfig.save(config.getData());
    }
    
    public void resetConfig() {
        for (final ClientModule module : Raven.moduleManager.getModules()) {
            module.resetToDefaults();
        }
        this.save();
    }
    
    public void deleteConfig(final Config config) {
        config.file.delete();
        if (config.getName().equals(this.config.getName())) {
            this.discoverConfigs();
            if (this.configs.size() < 2) {
                this.resetConfig();
                final File defaultFile = new File(this.configDirectory, "default.bplus");
                this.config = new Config(defaultFile);
                this.save();
            }
            else {
                this.config = this.configs.get(0);
            }
            this.save();
        }
    }
}
