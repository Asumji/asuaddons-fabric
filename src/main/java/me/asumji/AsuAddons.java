package me.asumji;

import me.asumji.gui.config.ConfigManager;
import me.asumji.features.*;
import me.asumji.util.Constants;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsuAddons implements ModInitializer {
	public static final String MOD_ID = "asuaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String modVersion = "1.0.0";
    public static final String NAMESPACE = "au";
    public static final String APIPROXY = "http://asumji.duckdns.org/";

	@Override
	public void onInitialize() {
		LOGGER.info("AsuAddons initialized!");
        ConfigManager.init();
        DPU.init();
        AUCommand.init();
        SimulateCommand.init();
        Constants.init();
        Autopet.init();
        LividTimer.init();
	}
}