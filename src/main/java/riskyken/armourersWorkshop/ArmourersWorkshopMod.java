package riskyken.armourersWorkshop;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import net.skin43d.EquipmentWardrobeProvider;
import net.skin43d.SkinInfoProvider;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinTypeRegistry;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.CommonProxy;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import net.skin43d.utils.ModLogger;

import java.io.File;
import java.net.URISyntaxException;

@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS)
public class ArmourersWorkshopMod extends ArmourersWorkshop {
    
    /* 
     * Hello and welcome to the Armourer's Workshop source code.
     * 
     * Please read this to familiarise yourself with the different terms used in the code.
     * 
     * Important - Any time the texture that is used on the player model is referred to,
     * (normal called the players skin) it will be called the player texture or entity
     * texture to prevent confusion with AW skins.
     * 
     * Skin - A custom 3D model that can be created by a player. Skins are stored in SkinDataCache
     * server side and ClientSkinCache on the client side.
     * 
     * SkinType - Each skin has a skin type, examples; head, chest, bow and block. All
     * skin types can be found in the SkinTypeRegistry.
     * 
     * SkinPart - Each skin type will have at least 1 skin part. For example the chest skin type has
     * base, left arm and right arm skin parts.
     * 
     * SkinPartType - Each skin part has a part type examples; left arm, left leg and right arm.
     * 
     * TODO Finish this!
     * 
     * SkinTexture
     * EntityTexture
     * SkinPartModel
     * 
     * SkinPointer
     * SkinDye
     * 
     * SkinTextureKey
     * SkinPartModelKey
     */

    @Mod.Instance(LibModInfo.ID)
    public static ArmourersWorkshopMod instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    public static CommonProxy proxy;

    private SkinTypeRegistry registry;

    @Mod.EventHandler
    public void perInit(FMLPreInitializationEvent event) throws URISyntaxException {
        ModLogger.log("Loading " + LibModInfo.NAME + " " + LibModInfo.VERSION);

        File configDir = event.getSuggestedConfigurationFile().getParentFile();
        configDir = new File(configDir, LibModInfo.ID);
        if (!configDir.exists()) configDir.mkdirs();
        registry = new SkinTypeRegistry();
        proxy.preInit(configDir);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        TestEnvSetup.setup();
        Skin skin1 = proxy.getSkinProvider().getSkinInfoForEntity(null).getSkin(ArmourersWorkshop.instance().getSkinRegistry().getSkinWings());
        System.out.println(proxy.getSkinProvider().getSkinInfoForEntity(null));
        System.out.println(skin1);
    }

    @Override
    public ISkinTypeRegistry getSkinRegistry() {
        return registry;
    }

    @Override
    public int getTextureSize() {
        return SkinTexture.TEXTURE_SIZE;
    }

    @Override
    public int getFileVersion() {
        return Skin.FILE_VERSION;
    }

    @Override
    public EquipmentWardrobeProvider getEquipmentWardrobeProvider() {
        return proxy.getEquipmentWardrobeProvider();
    }

    @Override
    public SkinInfoProvider getSkinProvider() {
        return proxy.getSkinProvider();
    }
}
