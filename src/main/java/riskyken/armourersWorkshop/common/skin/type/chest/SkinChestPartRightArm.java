package riskyken.armourersWorkshop.common.skin.type.chest;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.skin43d.utils.Point3D;
import net.skin43d.utils.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import net.skin43d.skin3d.SkinType;
import riskyken.armourersWorkshop.client.render.core.armourer.ModelChest;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinChestPartRightArm extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured {
    
    public SkinChestPartRightArm(SkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-3, -16, -14, 14, 32, 28);
        this.guideSpace = new Rectangle3D(-1, -10, -2, 4, 12, 4);
        this.offset = new Point3D(-10, -7, 0);
    }
    
    @Override
    public String getPartName() {
        return "rightArm";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelChest.MODEL.renderRightArm(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureLocation() {
        return new Point(40, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public Point3D getTextureModelSize() {
        return new Point3D(4, 12, 4);
    }
}
