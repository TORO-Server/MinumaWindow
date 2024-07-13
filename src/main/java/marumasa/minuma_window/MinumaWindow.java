package marumasa.minuma_window;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class MinumaWindow implements ModInitializer {
    // Logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Mod ID
    public static final String MOD_ID = "minuma_window";
    // サウンドイベント登録
    public static final SoundEvent MinumaIku_Sound = registerSound("minuma.iku");

    @Override
    public void onInitialize() {
        LOGGER.info(String.format("Start: %s", MOD_ID));
    }

    // サウンドイベント登録メソッド
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
