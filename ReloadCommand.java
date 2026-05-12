package dev.armazi.antixray.core;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Tracks mining behavior for a specific player
 */
public class PlayerXrayProfile {
    
    private final Player player;
    private final Deque<MiningRecord> miningHistory;
    private int violationCount;
    private long lastViolationTime;
    private double averageSuspicion;

    public PlayerXrayProfile(Player player) {
        this.player = player;
        this.miningHistory = new ConcurrentLinkedDeque<>();
        this.violationCount = 0;
        this.lastViolationTime = 0;
        this.averageSuspicion = 0;
    }

    public void recordMine(Block block) {
        MiningRecord record = new MiningRecord(
            block.getType(),
            block.getLocation(),
            Instant.now()
        );
        miningHistory.addLast(record);
        
        // Keep only last 1000 records
        if (miningHistory.size() > 1000) {
            miningHistory.removeFirst();
        }
    }

    public List<MiningRecord> getRecentMines(int count) {
        List<MiningRecord> recent = new ArrayList<>();
        int collected = 0;
        for (MiningRecord record : miningHistory.descendingIterator()) {
            if (collected >= count) break;
            recent.add(record);
            collected++;
        }
        return recent;
    }

    public void incrementViolation() {
        violationCount++;
        lastViolationTime = System.currentTimeMillis();
    }

    public int getViolationCount() {
        return violationCount;
    }

    public long getLastViolationTime() {
        return lastViolationTime;
    }

    public double getAverageSuspicion() {
        return averageSuspicion;
    }

    public void setAverageSuspicion(double score) {
        this.averageSuspicion = score;
    }

    public Deque<MiningRecord> getMiningHistory() {
        return miningHistory;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Mining record data class
     */
    public static class MiningRecord {
        private final org.bukkit.Material material;
        private final org.bukkit.Location location;
        private final Instant timestamp;

        public MiningRecord(org.bukkit.Material material, org.bukkit.Location location, Instant timestamp) {
            this.material = material;
            this.location = location;
            this.timestamp = timestamp;
        }

        public org.bukkit.Material getMaterial() { return material; }
        public org.bukkit.Location getLocation() { return location; }
        public Instant getTimestamp() { return timestamp; }
    }
}