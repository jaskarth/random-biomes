package supercoder79.randombiomes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import supercoder79.randombiomes.biome.RandomBiomeFeatures;
import supercoder79.randombiomes.biome.RandomSurfaceBuilders;
import supercoder79.randombiomes.config.ConfigData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RandomBiomeMod implements ModInitializer {
	@Override
	public void onInitialize() {
		RandomBiomeFeatures.init();
		RandomSurfaceBuilders.init();
		//Do config stuff
		try {
			Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
			path = Paths.get(path.toString(), "config", "randombiomes.json");
			if (Files.exists(path)) {
				System.out.println("Read config");
				Gson json = new GsonBuilder().create();
				ConfigData configData = json.fromJson(new FileReader(path.toString()), ConfigData.class);
				ConfigData.data = configData;
			} else {
				System.out.println("wrote config");
				ConfigData cfg = new ConfigData();
				Gson json = new GsonBuilder().setPrettyPrinting().create();
				FileWriter writer = new FileWriter(path.toString());
				json.toJson(cfg, writer);
				writer.flush();
				writer.close();
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
