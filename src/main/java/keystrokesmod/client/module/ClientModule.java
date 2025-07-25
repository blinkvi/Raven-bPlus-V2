package keystrokesmod.client.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    protected ArrayList<Setting> settings = new ArrayList<Setting>();
    private final String moduleName;
    private final Category moduleCategory;
    protected boolean enabled;
    protected int keycode;
    private boolean isToggled = false;
    
    private final ModuleInfo moduleInfo;
	public boolean ignoreOnSave;
    
    protected ClientModule() {
        this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        Objects.requireNonNull(moduleInfo, "ModuleInfo annotation is missing on " + getClass().getName());
        this.moduleName = moduleInfo.name();
        this.moduleCategory = moduleInfo.category();
        this.keycode = moduleInfo.key();
        this.enabled = moduleInfo.enabled();
    }
    
    protected <E extends ClientModule> E withKeycode(final int i) {
        this.keycode = i;
        return (E)this;
    }
    
    protected <E extends ClientModule> E withEnabled(final boolean i) {
        this.enabled = i;
        try {
            this.setToggled(i);
        }
        catch (Exception ex) {}
        return (E)this;
    }

    public JsonObject getConfigAsJson() {
        final JsonObject settings = new JsonObject();
        for (final Setting setting : this.settings) {
            final JsonObject settingData = setting.getConfigAsJson();
            settings.add(setting.getName(), (JsonElement)settingData);
        }
        final JsonObject data = new JsonObject();
        data.addProperty("enabled", Boolean.valueOf(this.enabled));
        data.addProperty("keycode", (Number)this.keycode);
        data.add("settings", (JsonElement)settings);
        return data;
    }
    
    public void applyConfigFromJson(final JsonObject data) {
        try {
            this.keycode = data.get("keycode").getAsInt();
            this.setToggled(data.get("enabled").getAsBoolean());
            final JsonObject settingsData = data.get("settings").getAsJsonObject();
            for (final Setting setting : this.getSettings()) {
                if (settingsData.has(setting.getName())) {
                    setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
                }
            }
        }
        catch (NullPointerException ex) {}
    }
    
    public void keybind() {
        if (this.keycode != 0 && this.canBeEnabled()) {
            if (!this.isToggled && Keyboard.isKeyDown(this.keycode)) {
                this.toggle();
                this.isToggled = true;
            }
            else if (!Keyboard.isKeyDown(this.keycode)) {
                this.isToggled = false;
            }
        }
    }
    
    public boolean canBeEnabled() {
        return true;
    }
    
    public void enable() {
        this.enabled = true;
        this.onEnable();
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    public void disable() {
        this.enabled = false;
        this.onDisable();
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
    
    public void setToggled(final boolean enabled) {
        if (enabled) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    public String getName() {
        return this.moduleName;
    }
    
    public ArrayList<Setting> getSettings() {
        return this.settings;
    }
    
    public Setting getSettingByName(final String name) {
        for (final Setting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null;
    }
    
    public Category moduleCategory() {
        return this.moduleCategory;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void toggle() {
        if (this.enabled) {
            this.disable();
        }
        else {
            this.enable();
        }
    }
    
    public void update() {
    }
    
    public void guiUpdate() {
    }
    
    public void guiButtonToggled(final TickSetting b) {
    }
    
    public int getKeycode() {
        return this.keycode;
    }
    
    public void setbind(final int keybind) {
        this.keycode = keybind;
    }
    
    public void resetToDefaults() {
        this.keycode = moduleInfo.key();
        this.setToggled(moduleInfo.enabled());
        for (final Setting setting : this.settings) {
            setting.resetToDefaults();
        }
    }
    
    public void addSetting(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }
    
    public void onGuiClose() {
    }
    
    public String getBindAsString() {
        return (this.keycode == 0) ? "None" : Keyboard.getKeyName(this.keycode);
    }
    
    public void clearBinds() {
        this.keycode = 0;
    }
    
    public boolean shouldDisplay(HUD hud) {
        Map<Category, Boolean> visibility = new HashMap<>();
        visibility.put(Category.Client, hud.hideClient.isToggled());
        visibility.put(Category.Combat, hud.hideCombat.isToggled());
        visibility.put(Category.Minigames, hud.hideMinigames.isToggled());
        visibility.put(Category.Movement, hud.hideMovement.isToggled());
        visibility.put(Category.Other, hud.hideOther.isToggled());
        visibility.put(Category.Player, hud.hidePlayer.isToggled());
        visibility.put(Category.Render, hud.hideRender.isToggled());
        visibility.put(Category.World, hud.hideWorld.isToggled());

        Category curCategory = this.moduleInfo.category();
        return !visibility.getOrDefault(curCategory, false);
    }
    
    public void onTick() {}
    public void onDrag(int mouseX, int mouseY, float partialTicks) {}
    public void onAttackTargetEntityWithCurrentItem(final Entity en) {}
    
	public String getUnformattedTextForChat(String text) { return text; }
    
    public boolean onSendPacket(Packet packet) { return false; }
    public boolean onReceivePacket(Packet packet) { return false; }
}
