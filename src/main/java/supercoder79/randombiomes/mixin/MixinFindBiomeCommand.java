package supercoder79.randombiomes.mixin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terraformersmc.terraform.command.FindBiomeCommand;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;

@Mixin(FindBiomeCommand.class)
public class MixinFindBiomeCommand {
	private static int timeout = 120000;

	/**
	 * @author SC79
	 */
	@Overwrite
	public static void register() {
		CommandRegistry.INSTANCE.register(false, (dispatcher) -> {
			LiteralArgumentBuilder<ServerCommandSource> builder = (LiteralArgumentBuilder) CommandManager.literal("findbiome").requires((source) -> {
				return source.hasPermissionLevel(2);
			});
			Registry.BIOME.stream().forEach((biome) -> {
				LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)builder.then(CommandManager.literal(((Identifier) (Registry.BIOME.getId(biome))).toString()).executes((context) -> {
					return execute((ServerCommandSource)context.getSource(), biome);
				}));
			});
			dispatcher.register(builder);
		});
	}

	private static int execute(ServerCommandSource source, Biome biome) {
		(new Thread(() -> {
			BlockPos executorPos = new BlockPos(source.getPosition());
			BlockPos biomePos = null;
			TranslatableText biomeName = new TranslatableText(biome.getTranslationKey(), new Object[0]);

			try {
				biomePos = spiralOutwardsLookingForBiome2(source, source.getWorld(), biome, (double)executorPos.getX(), (double)executorPos.getZ());
			} catch (CommandSyntaxException var6) {
				var6.printStackTrace();
			}

			if (biomePos == null) {
				source.sendFeedback((new TranslatableText(source.getMinecraftServer() instanceof DedicatedServer ? "optimizeWorld.stage.failed" : "commands.terraform.findbiome.fail", new Object[]{biomeName, timeout / 1000})).setStyle((new Style()).setColor(Formatting.RED)), true);
			} else {
				BlockPos finalBiomePos = biomePos;
				source.getMinecraftServer().execute(() -> {
					int distance = MathHelper.floor(getDistance2(executorPos.getX(), executorPos.getZ(), finalBiomePos.getX(), finalBiomePos.getZ()));
					Text coordinates = Texts.bracketed(new TranslatableText("chat.coordinates", new Object[]{finalBiomePos.getX(), "~", finalBiomePos.getZ()})).setStyle((new Style()).setColor(Formatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + finalBiomePos.getX() + " ~ " + finalBiomePos.getZ())).setHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip", new Object[0]))));
					source.sendFeedback(new TranslatableText("commands.locate.success", new Object[]{biomeName, coordinates, distance}), true);
				});
			}
		})).start();
		return 0;
	}

	private static BlockPos spiralOutwardsLookingForBiome2(ServerCommandSource source, World world, Biome biomeToFind, double startX, double startZ) throws CommandSyntaxException {
		double a = 16.0D / Math.sqrt(3.141592653589793D);
		double b = 2.0D * Math.sqrt(3.141592653589793D);
		double dist = 0.0D;
		long start = System.currentTimeMillis();
		BlockPos.PooledMutable pos = BlockPos.PooledMutable.get();
		int previous = 0;
		int i = 0;

		for(int n = 0; dist < 2.147483647E9D; ++n) {
			if (System.currentTimeMillis() - start > (long)timeout) {
				return null;
			}

			double rootN = Math.sqrt((double)n);
			dist = a * rootN;
			double x = startX + dist * Math.sin(b * rootN);
			double z = startZ + dist * Math.cos(b * rootN);
			pos.set(x, 0.0D, z);
			if (previous == 3) {
				previous = 0;
			}

			String dots = previous == 0 ? "." : (previous == 1 ? ".." : "...");
			if (source.getEntity() instanceof PlayerEntity && !(source.getMinecraftServer() instanceof DedicatedServer)) {
				source.getPlayer().sendChatMessage(new TranslatableText("commands.terraform.findbiome.scanning", new Object[]{dots}), MessageType.GAME_INFO);
			}

			if (i == 9216) {
				++previous;
				i = 0;
			}

			++i;
			if (world.getBiome(pos).equals(biomeToFind)) {
				pos.close();
				if (source.getEntity() instanceof PlayerEntity && !(source.getMinecraftServer() instanceof DedicatedServer)) {
					source.getPlayer().sendChatMessage(new TranslatableText("commands.terraform.findbiome.found", new Object[]{new TranslatableText(biomeToFind.getTranslationKey(), new Object[0]), (System.currentTimeMillis() - start) / 1000L}), MessageType.GAME_INFO);
				}

				return new BlockPos((int)x, 0, (int)z);
			}
		}

		return null;
	}

	private static double getDistance2(int posX, int posZ, int biomeX, int biomeZ) {
		return (double)MathHelper.sqrt(Math.pow((double)(biomeX - posX), 2.0D) + Math.pow((double)(biomeZ - posZ), 2.0D));
	}
}
