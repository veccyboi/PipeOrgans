package com.finchy.pipeorgans.item.generic;

import com.finchy.pipeorgans.block.generic.chordophones.GenericChordophoneStringBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

public class GenericChordophoneStringItem extends Item {

    public RegistryObject<? extends GenericChordophoneStringBlock> stringBlock;

    public GenericChordophoneStringItem(Properties pProperties, RegistryObject<? extends GenericChordophoneStringBlock> stringBlock) {
        super(pProperties);
        this.stringBlock = stringBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        if (!level.getBlockState(pos).is(Tags.Blocks.FENCES)) {
            return InteractionResult.FAIL;
        }

        Player player = pContext.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        ItemStack stack = pContext.getItemInHand();

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
        return InteractionResult.SUCCESS;
    }

    private void createFences(BlockPos startPos, int height, Level level) {
        BlockState verticalString = this.stringBlock.get().defaultBlockState();
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
