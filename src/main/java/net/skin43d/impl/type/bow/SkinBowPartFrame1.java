package net.skin43d.impl.type.bow;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.skin43d.utils.Point3D;
import net.skin43d.utils.Rectangle3D;
import net.skin43d.skin3d.SkinType;
import net.skin43d.impl.client.model.ModelHand;
import net.skin43d.impl.type.AbstractSkinPartTypeBase;

public class SkinBowPartFrame1 extends AbstractSkinPartTypeBase {
    
    public SkinBowPartFrame1(SkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -20, -46, 20, 62, 64);
        this.guideSpace = new Rectangle3D(-2, -2, 2, 4, 4, 8);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public String getPartName() {
        return "frame2";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelHand.MODEL.render(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
    
    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }
    
    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }
    
    @Override
    public boolean isPartRequired() {
        return true;
    }
}
