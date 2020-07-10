package com.IcedReaper.StripMe;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StripMe implements ModInitializer {
	public static final Identifier STRIP_ID = new Identifier("stripme", "strip");
	public static final Identifier DRESS_ID = new Identifier("stripme", "dress");

	public static final HashMap<BlockPos, BlockState> hashedBlockCache = new HashMap<>();

	// TODO: Add Config for this.
	private static List<String> defaultProfile = new ArrayList<>();

	static  {
		defaultProfile.add("minecraft:dirt");
		defaultProfile.add("minecraft:grass");
		defaultProfile.add("minecraft:tall_grass");
		defaultProfile.add("minecraft:grass_block");
		defaultProfile.add("minecraft:stone");
		defaultProfile.add("minecraft:diorite");
		defaultProfile.add("minecraft:granite");
		defaultProfile.add("minecraft:andesite");
		defaultProfile.add("minecraft:gravel");
		defaultProfile.add("minecraft:sand");
		defaultProfile.add("minecraft:sandstone");
		defaultProfile.add("minecraft:oak_log");
		defaultProfile.add("minecraft:dark_oak_log");
		defaultProfile.add("minecraft:spruce_log");
		defaultProfile.add("minecraft:birch_log");
		defaultProfile.add("minecraft:jungle_log");
		defaultProfile.add("minecraft:acacia_log");
		defaultProfile.add("minecraft:oak_leaves");
		defaultProfile.add("minecraft:dark_oak_leaves");
		defaultProfile.add("minecraft:spruce_leaves");
		defaultProfile.add("minecraft:birch_leaves");
		defaultProfile.add("minecraft:jungle_leaves");
		defaultProfile.add("minecraft:acacia_leaves");
		defaultProfile.add("minecraft:water");
		defaultProfile.add("minecraft:flowing_water");
		defaultProfile.add("minecraft:lava");
		defaultProfile.add("minecraft:flowing_lava");
		defaultProfile.add("minecraft:netherrack");
		defaultProfile.add("minecraft:end_stone");
		defaultProfile.add("minecraft:podzol");
		defaultProfile.add("minecraft:bamboo");
		defaultProfile.add("minecraft:seagrass");
		defaultProfile.add("minecraft:tall_seagrass");
	}

	@Override
	public void onInitialize() {
		System.out.println("General init");

		ServerSidePacketRegistry.INSTANCE.register(STRIP_ID, (packetContext, attachedData) -> {
			ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
			if(player == null) return;
			if(! player.isCreative()) return;

			World world = player.getEntityWorld();

			double chunkClearSizeX = 20; //(16 * config.getChunkRadiusX() / 2);
			double chunkClearSizeZ = 20; //(16 * config.getChunkRadiusZ() / 2);

			player.sendMessage(new LiteralText(Formatting.BOLD + "" + Formatting.GREEN + "StripMe! " + Formatting.RED + "WARNING! " + Formatting.WHITE + "World stripping initialized. Lag may occur."), false);

			int startX = (int) (player.getPos().getX() - chunkClearSizeX);
			int startY = (int) player.getPos().getY() + 16;
			int startZ = (int) (player.getPos().getZ() - chunkClearSizeZ);

			double finalX = player.getPos().getX() + chunkClearSizeX;
			double finalZ = player.getPos().getZ() + chunkClearSizeZ;

			for (int x = startX; (double) x <= finalX; x++) {
				for (int y = startY; (double) y >= 0; --y) {
					for (int z = startZ; (double) z <= finalZ; z++) {
						BlockPos targetBlockPos = new BlockPos(x, y , z);
						BlockState targetBlockState = world.getBlockState(targetBlockPos);
						Block targetBlock = targetBlockState.getBlock();

						if(targetBlock.equals(Blocks.AIR) || targetBlock.equals(Blocks.BEDROCK)) continue;

						Arrays.stream(defaultProfile.toArray()).filter(Registry.BLOCK.getId(targetBlock).toString() :: equals).forEachOrdered(s -> {
							hashedBlockCache.put(targetBlockPos, targetBlockState);
							//"minecraft:air" => config.getReplacementBlock()
							//3 => config.getBlockStateFlag()
							world.setBlockState(targetBlockPos, Registry.BLOCK.get(new Identifier("minecraft:air")).getDefaultState(), 3);
						});
					}
				}
			}

			player.sendMessage(new LiteralText(Formatting.GREEN + "StripMe! " + Formatting.WHITE + "World stripping finished successfully."), false);
		});


		ServerSidePacketRegistry.INSTANCE.register(DRESS_ID, (packetContext, attachedData) -> {
			ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
			if(player == null) return;
			if(! player.isCreative()) return;

			World world = player.getEntityWorld();
			if(world == null) return;

			double chunkClearSizeX = 20;//(16 * config.getChunkRadiusX() / 2);
			double chunkClearSizeZ = 20;//(16 * config.getChunkRadiusZ() / 2);

			player.sendMessage(new LiteralText(Formatting.BOLD + "" + Formatting.GREEN + "StripMe! " + Formatting.RED + "WARNING! " + Formatting.WHITE + "World dressing initialized. Lag may occur."), false);

			int startX = (int) (player.getPos().getX() - chunkClearSizeX);
			int startZ = (int) (player.getPos().getZ() - chunkClearSizeZ);

			double finalX = player.getPos().getX() + chunkClearSizeX;
			double finalY = player.getPos().getY() + 16;
			double finalZ = player.getPos().getZ() + chunkClearSizeZ;

			for (int x = startX; (double) x <= finalX; x++) {
				for (int y = 0; (double) y <= finalY; ++y) {
					for (int z = startZ; (double) z <= finalZ; z++) {
						BlockPos targetBlockPos = new BlockPos(x, y, z);
						if(hashedBlockCache.get(targetBlockPos) == null) continue;

						world.setBlockState(targetBlockPos, hashedBlockCache.get(targetBlockPos), 3);
					}
				}
			}

			player.sendMessage(new LiteralText(Formatting.GREEN + "StripMe! " + Formatting.WHITE + "World dressing finished successfully."), false);
		});
	}
}
