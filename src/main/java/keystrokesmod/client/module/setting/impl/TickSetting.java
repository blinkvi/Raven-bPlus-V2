package keystrokesmod.client.module.setting.impl;

import java.util.function.Supplier;

import com.google.gson.JsonObject;

import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.clickgui.raven.components.ModuleComponent;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.setting.Setting;

public class TickSetting extends Setting {
	private boolean isEnabled;
	private boolean defaultValue;
    public boolean isMethodButton;
    private Runnable method;
	
    public TickSetting(String name, ClientModule module, boolean value, Supplier<Boolean> visible) {
        super(name, module, visible);
		this.isEnabled = value;
		this.defaultValue = value;
    }

    public TickSetting(String name, ClientModule module, boolean value) {
        this(name, module, value, () -> true);
    }
    
    public TickSetting(String name, Runnable method) {
        this(name, null, method, () -> true);
    }

    public TickSetting(String name, ClientModule module, Runnable method, Supplier<Boolean> visibleCheck) {
		super(name, null, visibleCheck);
        this.isEnabled = false;
        this.isMethodButton = true;
        this.method = method;
    }

	@Override
	public void resetToDefaults() {
		this.isEnabled = this.defaultValue;
	}

	@Override
	public JsonObject getConfigAsJson() {
		final JsonObject data = new JsonObject();
		data.addProperty("type", this.getSettingType());
		data.addProperty("value", Boolean.valueOf(this.isToggled()));
		return data;
	}

	@Override
	public String getSettingType() {
		return "tick";
	}

	@Override
	public void applyConfigFromJson(final JsonObject data) {
		if (!data.get("type").getAsString().equals(this.getSettingType())) {
			return;
		}
		this.setEnabled(data.get("value").getAsBoolean());
	}

	@Override
	public Component createComponent(final ModuleComponent moduleComponent) {
		return null;
	}

	public boolean isToggled() {
		return this.isEnabled;
	}

	public void toggle() {
		this.isEnabled = !this.isEnabled;
	}

	public void enable() {
		this.isEnabled = true;
	}

	public void disable() {
		this.isEnabled = false;
	}

	public void setEnabled(final boolean b) {
		this.isEnabled = b;
	}
}
