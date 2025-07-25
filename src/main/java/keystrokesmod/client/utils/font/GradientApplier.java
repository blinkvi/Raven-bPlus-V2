package keystrokesmod.client.utils.font;

import java.awt.Color;

@FunctionalInterface
public interface GradientApplier {
    Color colour(int i);
}