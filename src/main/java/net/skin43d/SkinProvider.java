package net.skin43d;

import net.minecraft.entity.Entity;
import net.skin43d.skin3d.SkinType;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.skin.data.Skin;

import javax.annotation.Nullable;


/**
 * @author ci010
 */
public interface SkinProvider {
    @Nullable
    Skin getSkinInfoForEntity(Entity entity, SkinType skinType);

    @Nullable
    ISkinDye getPlayerDyeData(Entity entity, SkinType skinType);
}