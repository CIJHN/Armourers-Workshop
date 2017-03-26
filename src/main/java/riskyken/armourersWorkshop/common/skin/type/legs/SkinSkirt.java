package riskyken.armourersWorkshop.common.skin.type.legs;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.skin43d.skin3d.SkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinSkirt extends AbstractSkinTypeBase {

    private ArrayList<SkinPartType> skinParts;
    
    public SkinSkirt() {
        skinParts = new ArrayList<SkinPartType>();
        skinParts.add(new SkinSkirtPartBase(this));
    }
    
    @Override
    public List<SkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:skirt";
    }
    
    @Override
    public String getName() {
        return "Skirt";
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_SKIRT);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_SKIRT);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
}
