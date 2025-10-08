package net.r266.xyz;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XYZcopier implements ModInitializer {
	public static final String MOD_ID = "xyz-copier";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading XYZ...");
	}
}