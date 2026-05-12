package dev.armazi.antixray.obfuscation;

import dev.armazi.antixray.ArmaziAntiXray;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Obfuscation engine - replaces ores with fake blocks
 */
public class ObfuscationEngine {
    
    private final ArmaziAntiXray plugin;
    private static final Set<Material> VALUABLE_ORES = Set.of(
        Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
        Material.ANCIENT_DEBRIS,
        Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
        Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
        Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
        Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
        Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
        Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
        Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE
    );

    private static final Material[] REPLACEMENT_BLOCKS = {
        Material.STONE, Material.DEEPSLATE, Material.NETHERRACK
    };

    public ObfuscationEngine(ArmaziAntiXray plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        plugin.getPluginLogger().info("Obfuscation engine initialized");
    }

    /**
     * Obfuscate all ores in a chunk
     */
    public void obfuscateChunk(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getWorld().getMinHeight(); 
                     y < chunk.getWorld().getMaxHeight(); y++) {
                    Block block = chunk.getBlock(x, y, z);
                    
                    if (VALUABLE_ORES.contains(block.getType())) {
                        if (shouldObfuscate(block)) {
                            obfuscateBlock(block);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldObfuscate(Block block) {
        // Don't obfuscate if ore is exposed to air
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block adjacent = block.getRelative(dx, dy, dz);
                    if (adjacent.getType() == Material.AIR || adjacent.isLiquid()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void obfuscateBlock(Block block) {
        Material replacement = REPLACEMENT_BLOCKS[
            ThreadLocalRandom.current().nextInt(REPLACEMENT_BLOCKS.length)
        ];
        block.setType(replacement, false);
    }

    /**
     * Generate fake ores in a chunk
     */
    public void generateFakeOres(Chunk chunk) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int fakeOreCount = random.nextInt(5, 15);
        
        for (int i = 0; i < fakeOreCount; i++) {
            int x = random.nextInt(0, 16);
            int y = random.nextInt(0, 256);
            int z = random.nextInt(0, 16);
            
            Block block = chunk.getBlock(x, y, z);
            if (block.getType() == Material.STONE || block.getType() == Material.DEEPSLATE) {
                // Create fake ore vein
                createFakeVein(block);
            }
        }
    }

    private void createFakeVein(Block centerBlock) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int veinSize = random.nextInt(2, 6);
        
        for (int i = 0; i < veinSize; i++) {
            int dx = random.nextInt(-2, 3);
            int dy = random.nextInt(-2, 3);
            int dz = random.nextInt(-2, 3);
            
            Block relative = centerBlock.getRelative(dx, dy, dz);
            if (relative.getType() == Material.STONE || relative.getType() == Material.DEEPSLATE) {
                Material fakeOre = getRandomFakeOre();
                relative.setType(fakeOre, false);
            }
        }
    }

    private Material getRandomFakeOre() {
        Material[] fakeOres = {
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.ANCIENT_DEBRIS, Material.GOLD_ORE
        };
        return fakeOres[ThreadLocalRandom.current().nextInt(fakeOres.length)];
    }
}