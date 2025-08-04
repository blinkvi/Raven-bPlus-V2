package keystrokesmod.client.module;

import java.util.*;
import org.lwjgl.input.Keyboard;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import keystrokesmod.client.module.modules.client.HUD;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;

public class ClientModule implements IMinecraft {
    protected final ArrayList<Setting> settings = new ArrayList<>();
    private final ModuleInfo moduleInfo;
    private final String moduleName;
    private final Category moduleCategory;
    protected boolean enabled;
    protected int keycode;
    private boolean isToggled = false;
    public boolean ignoreOnSave;

    protected ClientModule() {
        this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        Objects.requireNonNull(moduleInfo, "ModuleInfo annotation is missing on " + getClass().getName());
        this.moduleName = moduleInfo.name();
        this.moduleCategory = moduleInfo.category();
        this.keycode = moduleInfo.key();
        this.enabled = moduleInfo.enabled();
    }

    protected <E extends ClientModule> E withEnabled(boolean state) {
        this.enabled = state;
        try {
            this.setToggled(state);
        } catch (Exception ignored) {}
        return (E) this;
    }

    public JsonObject getConfigAsJson() {
        JsonObject settingsJson = new JsonObject();
        for (Setting setting : this.settings) {
            settingsJson.add(setting.getName(), setting.getConfigAsJson());
        }

        JsonObject data = new JsonObject();
        data.addProperty("enabled", this.enabled);
        data.addProperty("keycode", this.keycode);
        data.add("settings", settingsJson);
        return data;
    }

    public void applyConfigFromJson(JsonObject data) {
        try {
            this.keycode = data.get("keycode").getAsInt();
            this.setToggled(data.get("enabled").getAsBoolean());

            JsonObject settingsData = data.get("settings").getAsJsonObject();
            for (Setting setting : this.settings) {
                if (settingsData.has(setting.getName())) {
                    setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
                }
            }
        } catch (NullPointerException ignored) {}
    }

    public void keybind() {
        if (this.keycode != 0 && this.canBeEnabled()) {
            if (!this.isToggled && Keyboard.isKeyDown(this.keycode)) {
                this.toggle();
                this.isToggled = true;
            } else if (!Keyboard.isKeyDown(this.keycode)) {
                this.isToggled = false;
            }
        }
    }

    public void setToggled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void toggle() {
        if (this.enabled) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void enable() {
        this.enabled = true;
        this.onEnable();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        this.enabled = false;
        this.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public boolean canBeEnabled() {
        return true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getName() {
        return this.moduleName;
    }

    public Category moduleCategory() {
        return this.moduleCategory;
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public void addSetting(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null;
    }

    public void resetToDefaults() {
        this.keycode = moduleInfo.key();
        this.setToggled(moduleInfo.enabled());
        for (Setting setting : this.settings) {
            setting.resetToDefaults();
        }
    }

    public String getBindAsString() {
        return (this.keycode == 0) ? "None" : Keyboard.getKeyName(this.keycode);
    }

    public void clearBinds() {
        this.keycode = 0;
    }

    public int getKeycode() {
        return this.keycode;
    }

    public void setbind(int keybind) {
        this.keycode = keybind;
    }

    public boolean shouldDisplay(HUD hud) {
        Map<Category, Boolean> visibility = new HashMap<>();
        visibility.put(Category.Client, hud.hideClient.isToggled());
        visibility.put(Category.Combat, hud.hideCombat.isToggled());
        visibility.put(Category.Movement, hud.hideMovement.isToggled());
        visibility.put(Category.Other, hud.hideOther.isToggled());
        visibility.put(Category.Player, hud.hidePlayer.isToggled());
        visibility.put(Category.Render, hud.hideRender.isToggled());
        visibility.put(Category.World, hud.hideWorld.isToggled());

        return !visibility.getOrDefault(this.moduleCategory, false);
    }

    public void onEnable() {}

    public void onDisable() {}

    public void update() {}

    public void guiUpdate() {}

    public void guiButtonToggled(TickSetting setting) {}

    public void onGuiClose() {}

    public void onAttackTargetEntityWithCurrentItem(Entity entity) {}

    public boolean onSend(Packet<?> packet) {
        return false;
    }

    public boolean onReceive(Packet<?> packet) {
        return false;
    }
}
