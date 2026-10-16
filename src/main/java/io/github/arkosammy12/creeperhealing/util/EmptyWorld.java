package io.github.arkosammy12.creeperhealing.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import com.mojang.renderpearl.backend.vulkan.init.FeatureSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of {@link  WorldView} backed by another instance.
 * This class has the explicit purpose of simulating an empty world to be used
 * when calling {@link  BlockState#canPlaceAt} to check if the block needs a supporting block.
 * This way we guarantee that all positions read from this instance correspond to empty block states.
 */
public class EmptyWorld implements WorldView {

    private final World world;

    public EmptyWorld(World world) {
        this.world = world;
    }

    @Nullable
    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {

        return new EmptyLevelChunk(this.world, new ChunkPos(chunkX, chunkZ), world.getRegistryManager().getOrThrow(Registries.BIOME).getOrThrow(Biomes.THE_VOID));
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return false;
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return this.world.getTopY(heightmap, x, z);
    }

    @Override
    public int getAmbientDarkness() {
        return 15;
    }

    @Override
    public BiomeManager getBiomeAccess() {
        return this.world.getBiomeAccess();
    }

    @Override
    public Holder<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return world.getRegistryManager().getOrThrow(Registries.BIOME).getOrThrow(Biomes.THE_VOID);
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public DimensionType getDimension() {
        return this.world.getDimension();
    }

    @Override
    public RegistryAccess getRegistryManager() {
        return this.world.getRegistryManager();
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return this.world.getEnabledFeatures();
    }

    @Override
    public EnvironmentAttributeAccess getEnvironmentAttributes() {
        return this.world.getEnvironmentAttributes();
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public LevelLightEngine getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box) {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }
}
