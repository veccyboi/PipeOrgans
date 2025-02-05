package com.finchy.pipeorgans.block.generic.chordophones;


import com.finchy.pipeorgans.init.AllBlocks;
import com.finchy.pipeorgans.init.AllTags;
import com.finchy.pipeorgans.item.generic.GenericChordophoneStringItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class GenericChordophoneStringBlock extends Block {

    public static final IntegerProperty THICKNESS = IntegerProperty.create("thickness", 1, 5);

    public RegistryObject<? extends GenericChordophoneStringItem> stringItem;

    public GenericChordophoneStringBlock(Properties pProperties, RegistryObject<? extends GenericChordophoneStringItem> stringItem) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(THICKNESS, 1));
        this.stringItem = stringItem;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(THICKNESS);
    }


    /* left to do:
      - add model
      ✅ move placement to seperate items to allow different kinds of chordophones
      ✅ run through the whole chain of string to change thickness
      - other shit probably
      - add sounds
    */
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        //if (pLevel.isClientSide) return InteractionResult.PASS;

        ItemStack handStack = pPlayer.getItemInHand(pHand);

        int CurrentThickness = pState.getValue(THICKNESS);

        if (CurrentThickness < 5 && handStack.is(AllTags.Items.ADDS_STRING_THICKNESS)) {

            thickenAttachedString(CurrentThickness + 1, pPos, pLevel);
            consumeThicknessModificationItems(handStack, pPlayer, pHand);

        } else if (CurrentThickness > 1 && handStack.is(AllTags.Items.REMOVES_STRING_THICKNESS)) {

            thickenAttachedString(CurrentThickness - 1, pPos, pLevel);
            consumeThicknessModificationItems(handStack, pPlayer, pHand);
        }
        else if (!pPlayer.isCrouching()){
            return InteractionResult.PASS;
        }
        else {
            //play sounds here
        }

        return InteractionResult.SUCCESS;
    }

    private void consumeThicknessModificationItems(ItemStack stack, Player player, InteractionHand hand) {
        if (player.isCreative()) return;

        if (stack.is(AllTags.Items.STRING_THICKNESS_DAMAGES)) {
            stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
        } else if (stack.is(AllTags.Items.STRING_THICKNESS_USES)) {
            stack.shrink(1);
        }
    }

    protected void killAttachedString(BlockPos pos, LevelAccessor level) {

        int direction = 1;
        int heightOffset = 1;

        while (-2 < direction) {
            Block activeBlock = level.getBlockState(pos.above(heightOffset)).getBlock();

            if (activeBlock instanceof GenericChordophoneStringBlock) {
                level.setBlock(pos.above(heightOffset), Blocks.AIR.defaultBlockState(), 2);
                heightOffset += direction;
                continue;
            }
            else {
                direction -= 2;
                heightOffset = -1;
            }
        }
    }

    protected void thickenAttachedString(int newThickness, BlockPos pos, LevelAccessor level) {

        int direction = 1;
        int heightOffset = 0;

        while (-2 < direction) {
            Block activeBlock = level.getBlockState(pos.above(heightOffset)).getBlock();

            if (activeBlock instanceof GenericChordophoneStringBlock) {
                level.setBlock(pos.above(heightOffset), AllBlocks.HARP_STRING.get().defaultBlockState().setValue(THICKNESS, newThickness), 2);
                heightOffset += direction;
                continue;
            }
            else {
                direction -= 2;
                heightOffset = -1;
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(Items.STRING);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).is(AllBlocks.HARP_STRING.get()) && pLevel.getBlockState(pPos.below()).is(AllBlocks.TIED_FENCE.get());
    }

    @Override
    public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState)
    {
        if (pLevel instanceof Level) {
            Block.popResource((Level) pLevel, pPos, new ItemStack(Items.STRING, pState.getValue(THICKNESS)));
        }

        killAttachedString(pPos, pLevel);
    }
}
