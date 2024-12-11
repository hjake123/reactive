package dev.hyperlynx.reactive.fx.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record LitmusData(List<Component> power_lines, List<Component> reaction_lines) {}
