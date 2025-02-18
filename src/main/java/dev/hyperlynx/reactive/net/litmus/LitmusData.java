package dev.hyperlynx.reactive.net.litmus;

import net.minecraft.network.chat.Component;

import java.util.List;

public record LitmusData(List<Component> power_lines, List<Component> reaction_lines) {}
