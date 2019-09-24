package supercoder79.randombiomes.data;

import java.util.Map;

/**
 * Holder class for JSON serializing biomes
 **/
public class SerializableBiomeData {
    public int rawID; //registry ID
    public int biomeID; //for loop num (0-9)
    public float depth;
    public float scale;
    public float temperature;
    public float rainfall;
    public int waterColor; //used for water fog color too
    public int surfaceBuilder;
    public int surfaceBuilderConfig;
    public int weight;
    public Map<String, Integer> features;

    public SerializableBiomeData(int rawID, int biomeID, float depth, float scale, float temperature, float rainfall, int waterColor, Map<String, Integer> features, int surfaceBuilder, int surfaceBuilderConfig, int weight) {
        this.rawID = rawID;
        this.biomeID = biomeID;
        this.depth = depth;
        this.scale = scale;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.waterColor = waterColor;
        this.surfaceBuilder = surfaceBuilder;
        this.surfaceBuilderConfig = surfaceBuilderConfig;
        this.weight = weight;
        this.features = features;
    }
}
