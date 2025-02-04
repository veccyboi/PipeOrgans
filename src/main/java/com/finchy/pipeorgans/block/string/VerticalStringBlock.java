package com.finchy.pipeorgans.block.string;


import com.finchy.pipeorgans.init.AllBlocks;
import com.finchy.pipeorgans.init.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class VerticalStringBlock extends Block {

    public static final IntegerProperty THICKNESS = IntegerProperty.create("thickness", 1, 5);

    public VerticalStringBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(THICKNESS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(THICKNESS);
    }


    /* left to do:
      - add model
      - add placement between two fences with string (mixins)
      - run through the whole chain of string to change thickness
      - add block drop of string equal to how thick the string is
      - pick block
      - drop string when removing
      - other shit probably
      - add sounds
    */
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        //if (pLevel.isClientSide) return InteractionResult.PASS;

        ItemStack handStack = pPlayer.getItemInHand(pHand);

        int CurrentThickness = pState.getValue(THICKNESS);

        if (CurrentThickness < 5 && handStack.is(AllTags.Items.ADDS_STRING_THICKNESS)) {

            BlockState newState = pState.setValue(THICKNESS, CurrentThickness + 1);
            pLevel.setBlock(pPos, newState, 2);
            onThicknessModification(handStack, pPlayer, pHand);

        } else if (CurrentThickness > 1 && handStack.is(AllTags.Items.REMOVES_STRING_THICKNESS)) {

            BlockState newState = pState.setValue(THICKNESS, CurrentThickness - 1);
            pLevel.setBlock(pPos, newState, 2);
            onThicknessModification(handStack, pPlayer, pHand);
        }
        else if (!pPlayer.isCrouching()){
            return InteractionResult.PASS;
        }
        else {
            //passTheFunkyBeatToTheFence(pLevel, pPos);
        }

        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    protected void passTheFunkyBeatToTheFence(Level level, BlockPos pos) {


        int blocksChecked = 0;
        while (blocksChecked <= 12) {
            Block blockBelow = level.getBlockState(pos.below(blocksChecked)).getBlock();

            if (blockBelow instanceof VerticalStringBlock) {
                ++blocksChecked;
            } else if (blockBelow instanceof TiedFenceBlock) {
                //tell TiedFenceBlock to ring
                break;
            } else {
                break;
            }
        }
    }

    protected void packUpCommaItsTimeToGoHome() {

    }

    private void onThicknessModification(ItemStack stack, Player player, InteractionHand hand) {
        if (player.isCreative()) return;

        if (stack.is(AllTags.Items.STRING_THICKNESS_DAMAGES)) {
            stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
        } else if (stack.is(AllTags.Items.STRING_THICKNESS_USES)) {
            stack.shrink(1);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(Items.STRING);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).is(AllBlocks.VERTICAL_STRING.get()) && pLevel.getBlockState(pPos.below()).is(AllBlocks.TIED_FENCE.get());
    }
}
