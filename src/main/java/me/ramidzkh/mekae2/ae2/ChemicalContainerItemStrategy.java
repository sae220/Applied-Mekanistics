package me.ramidzkh.mekae2.ae2;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import me.ramidzkh.mekae2.MekCapabilities;
import mekanism.api.Action;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

public class ChemicalContainerItemStrategy implements ContainerItemStrategy<MekanismKey, ItemStack> {

    @Override
    @Nullable
    public GenericStack getContainedStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        var handler = stack.getCapability(MekCapabilities.CHEMICAL.item());

        if (handler != null) {
            var chemical = handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE);
            var key = MekanismKey.of(chemical);

            if (key != null) {
                return new GenericStack(key, chemical.getAmount());
            }
        }

        return null;
    }

    @Override
    @Nullable
    public ItemStack findCarriedContext(Player player, AbstractContainerMenu menu) {
        var carried = menu.getCarried();

        if (carried.getCapability(MekCapabilities.CHEMICAL.item()) != null) {
            return carried;
        }

        return null;
    }

    @Override
    public @Nullable ItemStack findPlayerSlotContext(Player player, int slot) {
        var carried = player.getInventory().getItem(slot);

        if (carried.getCapability(MekCapabilities.CHEMICAL.item()) != null) {
            return carried;
        }

        return null;
    }

    @Override
    public long extract(ItemStack context, MekanismKey what, long amount, Actionable mode) {
        var handler = context.getCapability(MekCapabilities.CHEMICAL.item());

        if (handler == null) {
            return 0L;
        }

        return handler.extractChemical(what.withAmount(amount), Action.fromFluidAction(mode.getFluidAction()))
                .getAmount();
    }

    @Override
    public long insert(ItemStack context, MekanismKey what, long amount, Actionable mode) {
        var handler = context.getCapability(MekCapabilities.CHEMICAL.item());

        if (handler == null) {
            return 0L;
        }

        return amount - handler.insertChemical(what.withAmount(amount), Action.fromFluidAction(mode.getFluidAction()))
                .getAmount();
    }

    @Override
    public void playFillSound(Player player, MekanismKey what) {
        player.playNotifySound(SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void playEmptySound(Player player, MekanismKey what) {
        player.playNotifySound(SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    @Nullable
    public GenericStack getExtractableContent(ItemStack context) {
        return getContainedStack(context);
    }
}
