package riskyken.armourersWorkshop.common.skin.type.sword;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.skin43d.utils.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import net.skin43d.skin3d.SkinType;
import riskyken.armourersWorkshop.client.render.core.armourer.ModelHand;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinSwordPartBase extends AbstractSkinPartTypeBase {
    
    public SkinSwordPartBase(SkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -20, -28, 20, 62, 56);
        this.guideSpace = new Rectangle3D(-2, -2, 2, 4, 4, 8);
        //Offset -1 to match old skin system.
        this.offset = new Point3D(0, -1, 0);
    }
    
    @Override
    public String getPartName() {
        return "base";
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
}
