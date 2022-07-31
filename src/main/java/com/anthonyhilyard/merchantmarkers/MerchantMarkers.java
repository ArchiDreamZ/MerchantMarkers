package com.anthonyhilyard.merchantmarkers;

import com.anthonyhilyard.merchantmarkers.compat.OptifineHandler;
import com.anthonyhilyard.merchantmarkers.render.Markers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class MerchantMarkers
{
	private static final KeyMapping showMarkers = new KeyMapping("merchantmarkers.key.showMarkers", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_ALT), "merchantmarkers.key.categories.merchantMarkers");

	public void onClientSetup(FMLClientSetupEvent event)
	{
		try
		{
			// If Xaero's minimap is loaded, add a resource manager listener for dynamically-sized map icons.
			if (ModList.get().isLoaded("xaerominimap"))
			{
				Class.forName("com.anthonyhilyard.merchantmarkers.compat.XaeroMinimapHandler").getMethod("setupDynamicIcons").invoke(null);
			}
			// Same thing for FTB Chunks.
			if (ModList.get().isLoaded("ftbchunks"))
			{
				Class.forName("com.anthonyhilyard.merchantmarkers.compat.FTBChunksHandler").getMethod("setupDynamicIcons").invoke(null);
			}
			// If optifine is installed, we have to do some hacks to ensure it doesn't break markers.
			if (FMLEnvironment.dist == Dist.CLIENT && OptifineHandler.optifineInstalled())
			{
				MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent serverStartedEvent) -> { OptifineHandler.init(); });
			}
		}
		catch (Exception e)
		{
			Loader.LOGGER.error(ExceptionUtils.getStackTrace(e.getCause() == null ? e : e.getCause()));
		}
	}

	public void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
	{
		event.register(showMarkers);
	}

	@SubscribeEvent
	public static void onRenderNameplate(RenderNameTagEvent event)
	{
		if (showMarkers.isDown() || MerchantMarkersConfig.getInstance().alwaysShow.get())
		{
			Markers.renderMarker(event.getEntityRenderer(), event.getEntity(), event.getContent(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
		}
	}
}
