package marumasa.minuma_window;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class MinumaWindow implements ModInitializer {

    public static final String MOD_ID = "minuma_window";

    public static final SoundEvent MinumaIku_Sound = registerSound("minuma.iku");

    @Override
    public void onInitialize() {
    }

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
