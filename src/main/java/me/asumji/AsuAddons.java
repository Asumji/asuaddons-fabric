package me.asumji;

import me.asumji.gui.config.ConfigManager;
import me.asumji.features.*;
import me.asumji.util.Variables;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsuAddons implements ModInitializer {
	public static final String MOD_ID = "asuaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String MOD_VERSION = "1.2.0";
    public static final String NAMESPACE = "au";
    public static final String API_PROXY = "http://asumji.duckdns.org/";
    public static final String MOD_PREFIX = "§6AU > §r";

	@Override
	public void onInitialize() {
		LOGGER.info("AsuAddons initialized!");
        ConfigManager.init();
        DPU.init();
        AUCommand.init();
        SimulateCommand.init();
        Variables.init();
        Autopet.init();
        LividTimer.init();
        BloodGiant.init();
        MelodyDisplay.init();
        PurplePadTimer.init();
        WitherHitbox.init();
	}
}