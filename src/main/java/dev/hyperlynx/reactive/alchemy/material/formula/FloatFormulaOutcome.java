package dev.hyperlynx.reactive.alchemy.material.formula;

import dev.hyperlynx.reactive.alchemy.Power;

import java.util.Map;

public interface FloatFormulaOutcome {
    float calculate(Map<Power, Integer> formula);
}
