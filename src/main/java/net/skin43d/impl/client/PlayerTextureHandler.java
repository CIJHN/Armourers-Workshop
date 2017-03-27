package net.skin43d.impl.client;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.skin43d.SkinProvider;
import net.skin43d.impl.Context;
import net.skin43d.skin3d.ISkinDye;
import net.skin43d.skin3d.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.EquipmentWardrobeData;
import net.skin43d.impl.skin.Skin;

import java.util.HashMap;

/**
 * Handles replacing the players texture with the painted version.
 *
 * @author RiskyKen
 */
@SideOnly(Side.CLIENT)
public class PlayerTextureHandler {
    private HashMap<PlayerPointer, EntityTextureInfo> playerTextureMap = new HashMap<PlayerPointer, EntityTextureInfo>();
    private final Profiler profiler;
    private boolean disableTexturePainting;

    public PlayerTextureHandler() {
        profiler = Minecraft.getMinecraft().mcProfiler;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderPlayerEvent.Pre event) {
        disableTexturePainting = Context.instance().disableTexturePainting();
        if (disableTexturePainting) {
            return;
        }
        if (!(event.getEntityLiving() instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityLiving();
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        EquipmentWardrobeData ewd = Context.instance().getEquipmentWardrobeProvider().getEquipmentWardrobeData(playerPointer);
        if (ewd == null)
            return;

        profiler.startSection("textureBuild");
        if (playerTextureMap.containsKey(playerPointer)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(playerPointer);
            ResourceLocation def = DefaultPlayerSkin.getDefaultSkin(player.getUniqueID());
            textureInfo.updateTexture(def, player.getLocationSkin());
            textureInfo.updateHairColour(ewd.hairColour);
            textureInfo.updateSkinColour(ewd.skinColour);
            Skin[] skins = new Skin[4 * 5];

            SkinProvider skinProvider = Context.instance().getSkinProvider();
            SkinTypeRegistry reg = Context.instance().getSkinRegistry();
            for (int skinIndex = 0; skinIndex < 5; skinIndex++) {
                skins[skinIndex * 4] = skinProvider.getSkinInfoForEntity(player, reg.getSkinHead());
                skins[1 + skinIndex * 4] = skinProvider.getSkinInfoForEntity(player, reg.getSkinChest());
                skins[2 + skinIndex * 4] = skinProvider.getSkinInfoForEntity(player, reg.getSkinLegs());
                skins[3 + skinIndex * 4] = skinProvider.getSkinInfoForEntity(player, reg.getSkinFeet());
            }
            ISkinDye[] dyes = new ISkinDye[4 * 5];
            for (int skinIndex = 0; skinIndex < 5; skinIndex++) {
                dyes[skinIndex * 4] = skinProvider.getPlayerDyeData(player, reg.getSkinHead());
                dyes[1 + skinIndex * 4] = skinProvider.getPlayerDyeData(player, reg.getSkinChest());
                dyes[2 + skinIndex * 4] = skinProvider.getPlayerDyeData(player, reg.getSkinLegs());
                dyes[3 + skinIndex * 4] = skinProvider.getPlayerDyeData(player, reg.getSkinFeet());
            }

            textureInfo.updateSkins(skins);
            textureInfo.updateDyes(dyes);

//            ResourceLocation replacmentTexture = textureInfo.preRender();
//            player.getPlayerInfo().func_152121_a(Type.SKIN, replacmentTexture);
        }
        profiler.endSection();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRender(RenderPlayerEvent.Post event) {
        if (disableTexturePainting) {
            return;
        }
        if (!(event.getEntityPlayer() instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityLiving();
//        if (player instanceof MannequinFakePlayer) {
//            return;
//        }
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        EquipmentWardrobeData ewd = Context.instance().getEquipmentWardrobeProvider().getEquipmentWardrobeData(playerPointer);
        if (ewd == null) {
            return;
        }

        profiler.startSection("textureReset");
        if (playerTextureMap.containsKey(playerPointer)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(playerPointer);
//            ResourceLocation replacmentTexture = textureInfo.postRender();
//            player.func_152121_a(Type.SKIN, replacmentTexture);
        } else {
            playerTextureMap.put(playerPointer, new EntityTextureInfo());
        }
        profiler.endSection();
    }
}
