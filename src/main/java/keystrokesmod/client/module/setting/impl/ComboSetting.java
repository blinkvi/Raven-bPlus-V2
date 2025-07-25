package keystrokesmod.client.module.setting.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.clickgui.raven.components.ModuleComponent;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.setting.Setting;

public class ComboSetting extends Setting {
	private int index;
	private final List<String> list;
	
    public ComboSetting(String name, ClientModule module, Supplier<Boolean> visible, String current, String... modes) {
        super(name, module, visible);
        this.list = Collections.unmodifiableList(Arrays.asList(modes));
        setMode(current);
    }

    public ComboSetting(String name, ClientModule module, String current, String... modes) {
        this(name, module, () -> true, current, modes);
    }

    public ComboSetting(String name, ClientModule module, Enum<?> current, Enum<?>... enumModes) {
        this(name, module, () -> true, current, enumModes);
    }

    public ComboSetting(String name, ClientModule module, Supplier<Boolean> visible, Enum<?> current, Enum<?>... enumModes) {
        super(name, module, visible);
        this.list = Arrays.stream(enumModes).map(Enum::toString).collect(Collectors.toList());
        setMode(current.name());
    }

	public String getMode() {
		if (index >= list.size() || index < 0)
			index = 0;
		return list.get(index);
	}

	public void setMode(String mode) {
		this.index = list.indexOf(mode);
		if (this.index == -1) this.index = 0;
	}

	public <T extends Enum<T>> void setMode(T enumValue) {
		setMode(enumValue.name());
	}

	public boolean is(String mode) {
		return getMode().equals(mode);
	}

	public <T extends Enum<T>> boolean is(T enumValue) {
		return getMode().equals(enumValue.name());
	}

	@Override
	public JsonObject getConfigAsJson() {
		JsonObject data = new JsonObject();
		data.addProperty("type", getSettingType());
		data.addProperty("value", getMode());
		return data;
	}

	@Override
	public void applyConfigFromJson(JsonObject data) {
		if (!data.get("type").getAsString().equals(getSettingType()))
			return;
		String value = data.get("value").getAsString();
		setMode(value);
	}

	@Override
	public String getSettingType() {
		return "mode";
	}

	public void increment() {
		index = (index + 1) % list.size();
	}

	public void decrement() {
		index = (index - 1 + list.size()) % list.size();
	}

	public int getIndex() {
		return index;
	}

	public List<String> getList() {
		return list;
	}

	public void resetToDefaults() {
		setMode(list.get(0));
	}

	@Override
	public Component createComponent(ModuleComponent parent) {
		return null;
	}
}
