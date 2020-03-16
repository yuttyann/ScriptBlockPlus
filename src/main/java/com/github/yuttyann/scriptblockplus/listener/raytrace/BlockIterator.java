package com.github.yuttyann.scriptblockplus.listener.raytrace;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BlockIterator implements Iterator<BlockIterator.BData> {

    private final double maxDistance;

    private static final int gridSize = 1 << 24;

    private boolean end = false;

    private BData[] blockQueue = new BData[3];
    {
        blockQueue[0] = new BData();
        blockQueue[1] = new BData();
        blockQueue[2] = new BData();
    }

    public static class BData {

        private Block b;
        private BlockFace f;

        public BData put(@NotNull Block b, @NotNull BlockFace f) {
            this.b = b;
            this.f = f;
            return this;
        }

        public Block getBlock() {
            return b;
        }

        public BlockFace getFace() {
            return f;
        }
    }

    private int currentBlock = 0;
    private int currentDistance = 0;
    private int maxDistanceInt;

    private int secondError;
    private int thirdError;

    private int secondStep;
    private int thirdStep;

    private BlockFace mainFace;
    private BlockFace secondFace;
    private BlockFace thirdFace;

    public BlockIterator(@NotNull World world, @NotNull Vector start, @NotNull Vector direction, double yOffset, int maxDistance) {
        this.maxDistance = maxDistance;

        Vector startClone = start.clone();
        startClone.setY(startClone.getY() + yOffset);

        double mainDirection = 0;
        double secondDirection = 0;
        double thirdDirection = 0;

        double mainPosition = 0;
        double secondPosition = 0;
        double thirdPosition = 0;

        Block startBlock = world.getBlockAt(NumberConversions.floor(startClone.getX()),
                NumberConversions.floor(startClone.getY()), NumberConversions.floor(startClone.getZ()));
        if (getXLength(direction) > mainDirection) {
            mainFace = getXFace(direction);
            mainDirection = getXLength(direction);
            mainPosition = getXPosition(direction, startClone, startBlock);

            secondFace = getYFace(direction);
            secondDirection = getYLength(direction);
            secondPosition = getYPosition(direction, startClone, startBlock);

            thirdFace = getZFace(direction);
            thirdDirection = getZLength(direction);
            thirdPosition = getZPosition(direction, startClone, startBlock);
        }
        if (getYLength(direction) > mainDirection) {
            mainFace = getYFace(direction);
            mainDirection = getYLength(direction);
            mainPosition = getYPosition(direction, startClone, startBlock);

            secondFace = getZFace(direction);
            secondDirection = getZLength(direction);
            secondPosition = getZPosition(direction, startClone, startBlock);

            thirdFace = getXFace(direction);
            thirdDirection = getXLength(direction);
            thirdPosition = getXPosition(direction, startClone, startBlock);
        }
        if (getZLength(direction) > mainDirection) {
            mainFace = getZFace(direction);
            mainDirection = getZLength(direction);
            mainPosition = getZPosition(direction, startClone, startBlock);

            secondFace = getXFace(direction);
            secondDirection = getXLength(direction);
            secondPosition = getXPosition(direction, startClone, startBlock);

            thirdFace = getYFace(direction);
            thirdDirection = getYLength(direction);
            thirdPosition = getYPosition(direction, startClone, startBlock);
        }
        double d = mainPosition / mainDirection;
        double secondd = secondPosition - secondDirection * d;
        double thirdd = thirdPosition - thirdDirection * d;

        secondError = NumberConversions.floor(secondd * gridSize);
        secondStep = NumberConversions.round(secondDirection / mainDirection * gridSize);
        thirdError = NumberConversions.floor(thirdd * gridSize);
        thirdStep = NumberConversions.round(thirdDirection / mainDirection * gridSize);

        if (secondError + secondStep <= 0) {
            secondError = -secondStep + 1;
        }
        if (thirdError + thirdStep <= 0) {
            thirdError = -thirdStep + 1;
        }

        Block lastBlock;
        lastBlock = startBlock.getRelative(mainFace.getOppositeFace());

        if (secondError < 0) {
            secondError += gridSize;
            lastBlock = lastBlock.getRelative(secondFace.getOppositeFace());
        }
        if (thirdError < 0) {
            thirdError += gridSize;
            lastBlock = lastBlock.getRelative(thirdFace.getOppositeFace());
        }

        secondError -= gridSize;
        thirdError -= gridSize;
        blockQueue[0].b = lastBlock;
        currentBlock = -1;

        scan();
        boolean startBlockFound = false;
        for (int cnt = currentBlock; cnt >= 0; cnt--) {
            if (blockEquals(blockQueue[cnt].b, startBlock)) {
                currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }
        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        }

        maxDistanceInt = NumberConversions.round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
    }

    public BlockIterator(@NotNull Location location, double yOffset, int maxDistance) {
        this(location.getWorld(), location.toVector(), location.getDirection(), yOffset, maxDistance);
    }

    public BlockIterator(@NotNull LivingEntity entity, int maxDistance) {
        this(entity.getLocation(), entity.getEyeHeight(), maxDistance);
    }

    private boolean blockEquals(@NotNull Block a, @NotNull Block b) {
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    private BlockFace getXFace(@NotNull Vector direction) {
        return (direction.getX() > 0) ? BlockFace.EAST : BlockFace.WEST;
    }

    private BlockFace getYFace(@NotNull Vector direction) {
        return (direction.getY() > 0) ? BlockFace.UP : BlockFace.DOWN;
    }

    private BlockFace getZFace(@NotNull Vector direction) {
        return (direction.getZ() > 0) ? BlockFace.SOUTH : BlockFace.NORTH;
    }

    private double getXLength(@NotNull Vector direction) {
        return Math.abs(direction.getX());
    }

    private double getYLength(@NotNull Vector direction) {
        return Math.abs(direction.getY());
    }

    private double getZLength(@NotNull Vector direction) {
        return Math.abs(direction.getZ());
    }

    private double getPosition(double direction, double position, int blockPosition) {
        return direction > 0 ? position - blockPosition : blockPosition + 1 - position;
    }

    private double getXPosition(@NotNull Vector direction, @NotNull Vector position, @NotNull Block block) {
        return getPosition(direction.getX(), position.getX(), block.getX());
    }

    private double getYPosition(@NotNull Vector direction, @NotNull Vector position, @NotNull Block block) {
        return getPosition(direction.getY(), position.getY(), block.getY());
    }

    private double getZPosition(Vector direction, Vector position, Block block) {
        return getPosition(direction.getZ(), position.getZ(), block.getZ());
    }

    public boolean hasNext() {
        scan();
        return currentBlock != -1;
    }

    @NotNull
    public BData next() {
        scan();
        if (currentBlock <= -1) {
            throw new NoSuchElementException();
        } else {
            BData t = blockQueue[currentBlock--];
            t.f = convert(t.f);
            return t;
        }
    }

    @NotNull
    public BlockFace convert(@NotNull BlockFace f) {
        switch (f) {
            case SOUTH:
                return BlockFace.NORTH;
            case NORTH:
                return BlockFace.SOUTH;
            case EAST:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.EAST;
            case UP:
                return BlockFace.DOWN;
            case DOWN:
                return BlockFace.UP;
            default:
                return BlockFace.SELF;
        }
    }

    private void scan() {
        if (currentBlock >= 0) {
            return;
        }
        if (maxDistance != 0 && currentDistance > maxDistanceInt) {
            end = true;
            return;
        }
        if (end) {
            return;
        }
        currentDistance++;
        secondError += secondStep;
        thirdError += thirdStep;
        if (secondError > 0 && thirdError > 0) {
            blockQueue[2].put(blockQueue[0].b.getRelative(mainFace), mainFace);
            if (((long) secondStep) * ((long) thirdError) < ((long) thirdStep) * ((long) secondError)) {
                blockQueue[1].put(blockQueue[2].b.getRelative(secondFace), secondFace);
                blockQueue[0].put(blockQueue[1].b.getRelative(thirdFace), thirdFace);
            } else {
                blockQueue[1].put(blockQueue[2].b.getRelative(thirdFace), thirdFace);
                blockQueue[0].put(blockQueue[1].b.getRelative(secondFace), secondFace);
            }
            thirdError -= gridSize;
            secondError -= gridSize;
            currentBlock = 2;
        } else if (secondError > 0) {
            blockQueue[1].put(blockQueue[0].b.getRelative(mainFace), mainFace);
            blockQueue[0].put(blockQueue[1].b.getRelative(secondFace), secondFace);
            secondError -= gridSize;
            currentBlock = 1;
        } else if (thirdError > 0) {
            blockQueue[1].put(blockQueue[0].b.getRelative(mainFace), mainFace);
            blockQueue[0].put(blockQueue[1].b.getRelative(thirdFace), thirdFace);
            thirdError -= gridSize;
            currentBlock = 1;
        } else {
            blockQueue[0].put(blockQueue[0].b.getRelative(mainFace), mainFace);
            currentBlock = 0;
        }
    }
}