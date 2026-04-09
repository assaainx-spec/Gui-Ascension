package net.thejadeproject.ascension.refactor_packages.util.value_modifiers;

import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

/**
 * a modifier that is applied to a value
 * has a modifierIdentifier used to identify it, and a groupIdentifier
 * group identifier is used exclusively by multiply base and multiply final during calculation
 *
 */
public class ValueContainerModifier {
    private final double val;
    private final ModifierOperation operation;
    private final ResourceLocation modifierIdentifier;
    private final ResourceLocation groupIdentifier;

    public ValueContainerModifier(double val, ModifierOperation operation, ResourceLocation modifierIdentifier) {
        this(val,operation,modifierIdentifier,ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"default"));
    }
    public ValueContainerModifier(double val, ModifierOperation operation, ResourceLocation modifierIdentifier, ResourceLocation groupIdentifier) {
        this.val = val;
        this.operation = operation;
        this.modifierIdentifier = modifierIdentifier;
        this.groupIdentifier = groupIdentifier;
    }


    public ModifierOperation getOperation(){return operation;}
    public ResourceLocation getIdentifier(){return this.modifierIdentifier;}
    public ResourceLocation getGroupIdentifier(){return this.groupIdentifier;}
    public double getVal(){return this.val;}
}
