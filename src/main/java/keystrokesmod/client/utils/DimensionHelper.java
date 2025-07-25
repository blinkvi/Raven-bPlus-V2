package keystrokesmod.client.utils;

public class DimensionHelper implements IMinecraft {
    public static boolean isPlayerInNether() {
        return Utils.Player.isPlayerInGame() && mc.thePlayer.dimension == DIMENSIONS.NETHER.getDimensionID();
    }
    
    public static boolean isPlayerInEnd() {
        return Utils.Player.isPlayerInGame() && mc.thePlayer.dimension == DIMENSIONS.END.getDimensionID();
    }
    
    public static boolean isPlayerInOverworld() {
        return Utils.Player.isPlayerInGame() && mc.thePlayer.dimension == DIMENSIONS.OVERWORLD.getDimensionID();
    }
    
    enum DIMENSIONS
    {
        NETHER(-1), 
        OVERWORLD(0), 
        END(1);
        
        private final int dimensionID;
        
        private DIMENSIONS(final int n) {
            this.dimensionID = n;
        }
        
        public int getDimensionID() {
            return this.dimensionID;
        }
    }
}
