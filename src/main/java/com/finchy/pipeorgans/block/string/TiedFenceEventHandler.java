package com.finchy.pipeorgans.block.string;

import com.finchy.pipeorgans.init.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TiedFenceEventHandler {
    @SubscribeEvent
    public static void rightClickedFence(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (!level.getBlockState(pos).is(Tags.Blocks.FENCES) || !event.getItemStack().is(Items.STRING)) {
            return;
        }

        event.setCanceled(true);

        if (level.isClientSide) {
            return;
        }
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        CompoundTag tag = stack.getOrCreateTag();

        BlockPos firstFence = null;
        if (tag.contains("first_fence")) {
            firstFence = NbtUtils.readBlockPos(tag.getCompound("first_fence"));
            if (areFencesUnaligned(firstFence, pos)) {
                tag.remove("first_fence");
                stack.setTag(tag);
                player.getCooldowns().addCooldown(Items.STRING, 5);
            }
        }

        if (player != null)
            if (tag.contains("first_fence")) {
                if (canTieFences(firstFence, pos, level)) {
                    if (firstFence != null && !firstFence.equals(pos)) {
                        createFences(firstFence.getY() < pos.getY() ? firstFence : pos, Math.abs(firstFence.getY() - pos.getY()), level);

                        tag.remove("first_fence");
                        stack.setTag(tag);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            player.getCooldowns().addCooldown(Items.STRING, 5);
                        }

                        if (stack.isEmpty()) {
                            stack.setTag(null);
                        }
                    }
                }
            } else {
                tag.put("first_fence", NbtUtils.writeBlockPos(pos));
                stack.setTag(tag);
                stack.removeTagKey("");
                player.getCooldowns().addCooldown(Items.STRING, 5);
            }
    }

    private static void createFences(BlockPos startPos, int height, Level level) {
        BlockState verticalString = AllBlocks.HARP_STRING.get().defaultBlockState();
        for (int i = 1; i < height; i++) {
            level.setBlock(startPos.above(i), verticalString, 2);
        }
    }

    private static boolean areFencesUnaligned(BlockPos firstPos, BlockPos secondPos) {
        if (firstPos.getX() != secondPos.getX() || firstPos.getZ() != secondPos.getZ()) return true;
        return Math.abs(secondPos.getY() - firstPos.getY()) > 13;
    }

    private static boolean canTieFences(BlockPos firstPos, BlockPos secondPos, Level level) {
        if (areFencesUnaligned(firstPos, secondPos)) return false;

        BlockPos currentPos = new BlockPos(firstPos.getX(), Math.min(secondPos.getY(), firstPos.getY()), firstPos.getZ());
        int height = Math.abs(firstPos.getY() - secondPos.getY());
        for (int i = 1; i < height; i++) {
            if (level.isEmptyBlock(currentPos.above(i))) {
                continue;
            }
            return false;
        }

        return true;
    }
}
