package keystrokesmod.client.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderTextEvent extends Event {
	public String text;

	public RenderTextEvent(String text) {
		this.text = text;
	}
}