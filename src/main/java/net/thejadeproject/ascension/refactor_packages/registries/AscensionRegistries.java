package net.thejadeproject.ascension.refactor_packages.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.paths.IPath;
import net.thejadeproject.ascension.refactor_packages.skills.ISkill;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodline;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityForm;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public class AscensionRegistries {
    public static class Stats{
        public static final ResourceKey<Registry<Stat>> STAT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation
                .fromNamespaceAndPath(AscensionCraft.MOD_ID,"stats"));
        public static final Registry<Stat> STATS_REGISTRY = new RegistryBuilder<>(STAT_REGISTRY_KEY)
                .create();
    }
    public static class EntityForms{
        public static final ResourceKey<Registry<IEntityForm>> ENTITY_FORM_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation
                .fromNamespaceAndPath(AscensionCraft.MOD_ID,"entity_forms"));
        public static final Registry<IEntityForm> ENTITY_FORMS_REGISTRY = new RegistryBuilder<>(ENTITY_FORM_REGISTRY_KEY)
                .create();
    }
    public static class Techniques{
        public static final ResourceKey<Registry<ITechnique>> TECHNIQUES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,"techniques"
        ));
        public static final Registry<ITechnique> TECHNIQUES_REGISTRY = new RegistryBuilder<>(TECHNIQUES_REGISTRY_KEY)
                .defaultKey(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"empty"))
                .create();
    }
    public static class Physiques{

        public static final ResourceKey<Registry<IPhysique>> PHYSIQUE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation
                .fromNamespaceAndPath(AscensionCraft.MOD_ID,"physiques"));
        public static final Registry<IPhysique> PHSIQUES_REGISTRY = new RegistryBuilder<>(PHYSIQUE_REGISTRY_KEY)
                .defaultKey(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"mortal"))
                .create();
    }
    public static class Bloodlines{

        public static final ResourceKey<Registry<IBloodline>> BLOODLINE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation
                .fromNamespaceAndPath(AscensionCraft.MOD_ID,"bloodlines"));
        public static final Registry<IBloodline> BLOODLINE_REGISTRY = new RegistryBuilder<>(BLOODLINE_REGISTRY_KEY)
                .defaultKey(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"human"))
                .create();
    }
    public static class Skills{
        public static final ResourceKey<Registry<ISkill>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,"skills"
        ));
        public static final Registry<ISkill> SKILL_REGISTRY = new RegistryBuilder<>(SKILL_REGISTRY_KEY)
                .defaultKey(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"empty"))
                .create();
    }
    public static class Paths {
        public static final ResourceKey<Registry<IPath>> PATHS_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation
                .fromNamespaceAndPath(AscensionCraft.MOD_ID,"paths"));
        public static final Registry<IPath> PATHS_REGISTRY = new RegistryBuilder<>(PATHS_REGISTRY_KEY)
                .defaultKey(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"none"))
                .create();

    }
    @SubscribeEvent // on the mod event bus
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(Stats.STATS_REGISTRY);
        event.register(EntityForms.ENTITY_FORMS_REGISTRY);
        event.register(Techniques.TECHNIQUES_REGISTRY);
        event.register(Physiques.PHSIQUES_REGISTRY);
        event.register(Bloodlines.BLOODLINE_REGISTRY);
        event.register(Skills.SKILL_REGISTRY);
        event.register(Paths.PATHS_REGISTRY);

    }
}
