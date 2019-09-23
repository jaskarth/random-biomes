package supercoder79.randombiomes.data;

public class SerializableBiomeData {
    public int rawID; //registry ID
    public int biomeID; //for loop num (0-9)
    public float depth;
    public float scale;
    public float temperature;
    public float rainfall;
    public int waterColor; //used for water fog color too
    public int oakTreeAmt;
    public int birchTreeAmt;
    public int spruceTreeAmt;
    public int oakLogAmt;
    public int birchLogAmt;
    public int spruceLogAmt;
    public int grassAmt;
    public int fernAmt;
    public int cactusAmt;
    public int deadBushAmt; //TODO
    public int surfaceBuilder;
    public int surfaceBuilderConfig;

    public SerializableBiomeData(int rawID, int biomeID, float depth, float scale, float temperature, float rainfall, int waterColor, int oakTreeAmt, int birchTreeAmt, int spruceTreeAmt, int oakLogAmt, int birchLogAmt, int spruceLogAmt, int grassAmt, int fernAmt, int cactusAmt, int deadBushAmt, int surfaceBuilder, int surfaceBuilderConfig) {
        this.rawID = rawID;
        this.biomeID = biomeID;
        this.depth = depth;
        this.scale = scale;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.waterColor = waterColor;
        this.oakTreeAmt = oakTreeAmt;
        this.birchTreeAmt = birchTreeAmt;
        this.spruceTreeAmt = spruceTreeAmt;
        this.oakLogAmt = oakLogAmt;
        this.birchLogAmt = birchLogAmt;
        this.spruceLogAmt = spruceLogAmt;
        this.grassAmt = grassAmt;
        this.fernAmt = fernAmt;
        this.cactusAmt = cactusAmt;
        this.deadBushAmt = deadBushAmt;
        this.surfaceBuilder = surfaceBuilder;
        this.surfaceBuilderConfig = surfaceBuilderConfig;
    }
}
