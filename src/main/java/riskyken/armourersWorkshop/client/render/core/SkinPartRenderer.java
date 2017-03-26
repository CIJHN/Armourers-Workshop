package riskyken.armourersWorkshop.client.render.core;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import net.skin43d.impl.client.render.BakedFace;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.client.ClientProxy;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class SkinPartRenderer extends ModelBase {
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    public static final SkinPartRenderer INSTANCE = new SkinPartRenderer();
    private int skinRendersThisTick = 0;
    private float renderTickTime;
    private int skinRenderLastTick = 0;
    private final Minecraft mc;

    @SubscribeEvent
    public void onRenderTickEvent(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            renderTickTime = event.renderTickTime;
            skinRenderLastTick = skinRendersThisTick;
            skinRendersThisTick = 0;
        }
    }

    public SkinPartRenderer() {
        mc = Minecraft.getMinecraft();
    }

    public void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour, double distance, boolean doLodLoading) {
        int lod = MathHelper.floor_double(distance / ConfigHandlerClient.lodDistance);
        lod = MathHelper.clamp_int(lod, 0, ConfigHandlerClient.maxLodLevels);
        renderPart(skinPart, scale, skinDye, extraColour, lod, doLodLoading);
    }

    private void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour, int lod, boolean doLodLoading) {
        //mc.mcProfiler.startSection(skinPart.getPartType().getPartName());
        skinRendersThisTick++;
        //GL11.glColor3f(1F, 1F, 1F);

        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        if (cspd == null) return;
        SkinModel skinModel = cspd.getModelForDye(skinDye, extraColour);
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();

        for (int i = 0; i < skinModel.displayList.length; i++) {
            if (skinModel.haveList[i]) {
                if (!skinModel.displayList[i].isCompiled()) {
                    skinModel.displayList[i].begin();
                    renderVertexList(cspd.vertexLists[i], scale, skinDye, extraColour, cspd);
                    skinModel.displayList[i].end();
                    skinModel.setLoaded();
                }
            }
        }

        if (ClientProxy.useSafeTextureRender())
            mc.renderEngine.bindTexture(texture);
         else
            GL11.glDisable(GL11.GL_TEXTURE_2D);

        int startIndex = 0;
        int endIndex;

        int loadingLod = skinModel.getLoadingLod();
        if (!doLodLoading)
            loadingLod = 0;
        if (loadingLod > lod)
            lod = loadingLod;

        if (lod != 0)
            if (multipassSkinRendering)
                startIndex = lod * 4;
             else
                startIndex = lod * 2;

        if (multipassSkinRendering)
            endIndex = startIndex + 4;
         else
            endIndex = startIndex + 2;

        int listCount = skinModel.displayList.length;
        for (int i = startIndex; i < endIndex; i++) {
            if (i >= startIndex & i < endIndex) {
                boolean glowing = false;
                if (i % 2 == 1) {
                    glowing = true;
                }
                if (i >= 0 & i < skinModel.displayList.length) {
                    if (skinModel.haveList[i]) {
                        if (skinModel.displayList[i].isCompiled()) {
                            if (glowing) {
                                GL11.glDisable(GL11.GL_LIGHTING);
                                ModRenderHelper.disableLighting();
                            }
                            if (ConfigHandlerClient.wireframeRender) {
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                            }
                            skinModel.displayList[i].render();
                            if (ConfigHandlerClient.wireframeRender) {
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                            }
                            if (glowing) {
                                ModRenderHelper.enableLighting();
                                GL11.glEnable(GL11.GL_LIGHTING);
                            }
                        }
                    }
                }
            }
        }

        if (!ClientProxy.useSafeTextureRender()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glColor3f(1F, 1F, 1F);
        //mc.mcProfiler.endSection();
    }

    private void renderVertexList(List<BakedFace> vertexList, float scale, ISkinDye skinDye, byte[] extraColour, ClientSkinPartData cspd) {
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        renderBuffer.startDrawingQuads();
        for (int i = 0; i < vertexList.size(); i++)
            vertexList.get(i).renderVertex(skinDye, extraColour, cspd, ClientProxy.useSafeTextureRender());
        renderBuffer.draw();
    }
}
