package keystrokesmod.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;

public class BlinkUtils implements IMinecraft {
	private static Double prevYMotion = null;
	private static boolean isStarted = false;
	public static boolean limiter = false;
	public static boolean blinking = false;
	private static final List<Packet<?>> packets = Collections.synchronizedList(new ArrayList<Packet<?>>());
	private static final List<Vec3> positions = Collections.synchronizedList(new ArrayList<Vec3>());

	public static void addPacket(final Packet<?> packet) {
		packets.add(packet);
	}

	public static void doBlink() {
		if (mc.isIntegratedServerRunning()) {
			return;
		}
		blinking = true;
		if (prevYMotion == null && mc.thePlayer != null) {
			prevYMotion = mc.thePlayer.motionY;
		}
		if (!isStarted && mc.thePlayer != null) {
			synchronized (positions) {
				positions
						.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY
								+ mc.thePlayer.getEyeHeight() / 2.0f, mc.thePlayer.posZ));
				positions.add(new Vec3(mc.thePlayer.posX,
						mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ));
			}
			isStarted = true;
			return;
		}
		if (mc.thePlayer != null) {
			synchronized (positions) {
				positions.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY,
						mc.thePlayer.posZ));
			}
		}
	}

	public static void sync(final boolean blinkSync, final boolean noSyncResetPos) {
		if (blinkSync) {
			try {
				limiter = true;
				while (!packets.isEmpty()) {
					mc.getNetHandler().addToSendQueue(packets.remove(0));
				}
			} catch (final Exception ex) {
			} finally {
				limiter = false;
			}
			synchronized (positions) {
				positions.clear();
			}
		} else {
			try {
				limiter = true;
				packets.clear();
			} catch (final Exception ex2) {
			} finally {
				limiter = false;
			}
			if (noSyncResetPos && mc.thePlayer != null) {
				synchronized (positions) {
					if (!positions.isEmpty() && positions.size() > 1) {
						mc.thePlayer.setPosition(positions.get(1).xCoord,
								positions.get(1).yCoord, positions.get(1).zCoord);
					}
				}
				if (prevYMotion != null) {
					mc.thePlayer.motionY = prevYMotion;
				}
			}
		}
	}

	public static void stopBlink() {
		synchronized (positions) {
			positions.clear();
		}
		prevYMotion = null;
		isStarted = false;
		blinking = false;
	}
}
