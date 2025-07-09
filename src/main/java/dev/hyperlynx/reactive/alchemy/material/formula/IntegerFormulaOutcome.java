package dev.hyperlynx.reactive.alchemy.material.formula;

import dev.hyperlynx.reactive.alchemy.Power;

import java.util.Map;

public interface IntegerFormulaOutcome {
    int calculate(Map<Power, Integer> formula);
}
