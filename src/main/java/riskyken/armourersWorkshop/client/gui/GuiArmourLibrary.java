package riskyken.armourersWorkshop.client.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiFileListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiList;
import riskyken.armourersWorkshop.client.gui.controls.GuiScrollbar;
import riskyken.armourersWorkshop.client.gui.controls.IGuiListItem;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.items.ItemSkinTemplate;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.SkinUploadHelper;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour.LibraryPacketType;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class GuiArmourLibrary extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    private static final int BUTTON_ID_LOAD_SAVE = 0;
    
    private static final int TITLE_HEIGHT = 15;
    private static final int PADDING = 5;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;
    
    private static int scrollAmount = 0;
    private static ISkinType lastSkinType;
    private static String lastSearchText = "";
    
    private TileEntitySkinLibrary armourLibrary;
    private GuiIconButton fileSwitchlocal;
    private GuiIconButton fileSwitchRemotePublic;
    private GuiIconButton fileSwitchRemotePrivate;
    private LibraryFileType fileSwitchType;
    private GuiList fileList;
    private GuiButtonExt loadSaveButton;
    private GuiIconButton openFolderButton;
    private GuiIconButton deleteButton;
    private GuiIconButton reloadButton;
    private GuiScrollbar scrollbar;
    private GuiLabeledTextField filenameTextbox;
    private GuiLabeledTextField searchTextbox;
    private GuiDropDownList dropDownList;
    private int neiBump = 18;
    
    public GuiArmourLibrary(InventoryPlayer invPlayer, TileEntitySkinLibrary armourLibrary) {
        super(new ContainerArmourLibrary(invPlayer, armourLibrary));
        this.armourLibrary = armourLibrary;
    }
    
    @Override
    public void initGui() {
        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.xSize = reso.getScaledWidth();
        this.ySize = reso.getScaledHeight();
        super.initGui();
        String guiName = armourLibrary.getInventoryName();
        
        int slotSize = 18;
        
        if (!Loader.isModLoaded("NotEnoughItems")) {
            neiBump = 0;
        }
        
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(x);
            slot.yDisplayPosition = this.height + 1 - PADDING - slotSize - neiBump;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.yDisplayPosition = this.height + 1 - INVENTORY_HEIGHT - PADDING + y * slotSize - neiBump;
            }
        }
        
        
        //Move library inventory slots.
        if (armourLibrary.isCreativeLibrary()) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(36);
            slot.yDisplayPosition = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
            slot.xDisplayPosition = PADDING + INVENTORY_WIDTH - slotSize - 3;
        } else {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(36);
            slot.yDisplayPosition = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
            slot.xDisplayPosition = PADDING + 1;
            slot = (Slot) inventorySlots.inventorySlots.get(37);
            slot.yDisplayPosition = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
            slot.xDisplayPosition = PADDING + INVENTORY_WIDTH - slotSize - 3;
        }
        
        
        buttonList.clear();
        
        fileSwitchlocal = new GuiIconButton(this, -1, PADDING, TITLE_HEIGHT + PADDING, 50, 30, GuiHelper.getLocalizedControlName(guiName, "rollover.localFiles"), texture);
        fileSwitchRemotePublic = new GuiIconButton(this, -1, PADDING + 51 + PADDING, TITLE_HEIGHT + PADDING, 50, 30, GuiHelper.getLocalizedControlName(guiName, "rollover.remotePublicFiles"), texture);
        fileSwitchRemotePrivate = new GuiIconButton(this, -1, PADDING + 102 + PADDING * 2, TITLE_HEIGHT + PADDING, 50, 30, GuiHelper.getLocalizedControlName(guiName, "rollover.remotePrivateFiles"), texture);
        fileSwitchlocal.setIconLocation(0, 0, 50, 30);
        fileSwitchRemotePublic.setIconLocation(0, 31, 50, 30);
        fileSwitchRemotePrivate.setIconLocation(0, 62, 50, 30);
        
        reloadButton = new GuiIconButton(this, -1, PADDING * 2 + 20, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.refresh"), texture);
        reloadButton.setIconLocation(50, 93, 24, 24);
        buttonList.add(reloadButton);
        
        if (mc.isSingleplayer()) {
            fileSwitchRemotePublic.enabled = false;
            fileSwitchRemotePrivate.enabled = false;
            fileSwitchRemotePublic.setDisableText(GuiHelper.getLocalizedControlName(guiName, "rollover.notOnServer"));
            fileSwitchRemotePrivate.setDisableText(GuiHelper.getLocalizedControlName(guiName, "rollover.notOnServer"));
            setFileSwitchType(LibraryFileType.LOCAL);
        } else {
            setFileSwitchType(LibraryFileType.SERVER_PUBLIC);
        }
        
        buttonList.add(fileSwitchlocal);
        buttonList.add(fileSwitchRemotePublic);
        buttonList.add(fileSwitchRemotePrivate);
        
        loadSaveButton = new GuiButtonExt(BUTTON_ID_LOAD_SAVE, PADDING * 2 + 18, this.height - INVENTORY_HEIGHT - PADDING * 2 - 2 - 20 - neiBump, 108, 20, "----LS--->");
        buttonList.add(loadSaveButton);
        
        
        openFolderButton = new GuiIconButton(this, 4, PADDING, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.openLibraryFolder"), texture);
        openFolderButton.setIconLocation(0, 93, 24, 24);
        buttonList.add(openFolderButton);
        
        deleteButton = new GuiIconButton(this, -1, PADDING * 2 + 40, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.deleteSkin"), texture);
        deleteButton.setIconLocation(0, 118, 24, 24);
        //buttonList.add(deleteButton);
        
        int listWidth = this.width - INVENTORY_WIDTH - 10 - PADDING * 3;
        int listHeight = this.height - TITLE_HEIGHT - 14 - PADDING * 3;
        int typeSwitchWidth = 80;
        
        filenameTextbox = new GuiLabeledTextField(fontRendererObj, PADDING, TITLE_HEIGHT + 30 + PADDING * 2, INVENTORY_WIDTH, 12);
        filenameTextbox.setMaxStringLength(30);
        filenameTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "label.enterFileName"));
        
        searchTextbox = new GuiLabeledTextField(fontRendererObj, INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 1 + PADDING, listWidth - typeSwitchWidth - PADDING + 10, 12);
        searchTextbox.setMaxStringLength(30);
        searchTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "label.typeToSearch"));
        searchTextbox.setText(lastSearchText);

        fileList = new GuiList(INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 14 + PADDING * 2, listWidth, listHeight, 12);
        
        scrollbar = new GuiScrollbar(2, INVENTORY_WIDTH + 10 + listWidth, TITLE_HEIGHT + 14 + PADDING * 2, 10, listHeight, "", false);
        scrollbar.setValue(scrollAmount);
        buttonList.add(scrollbar);
        
        dropDownList = new GuiDropDownList(5, this.width - typeSwitchWidth - PADDING, TITLE_HEIGHT + PADDING, typeSwitchWidth, "", null);
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        dropDownList.addListItem("*");
        dropDownList.setListSelectedIndex(0);
        int addCount = 0;
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            if (!skinType.isHidden()) {
                dropDownList.addListItem(SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType),
                        skinType.getRegistryName(), true);
                addCount++;
                if (skinType == lastSkinType) {
                    dropDownList.setListSelectedIndex(addCount);
                }
            }
        }
        buttonList.add(dropDownList);
    }
    
    /**
     * Returns true if the player is trying to load and item
     * or false if they are trying to save.
     * @return true = loading, false = saving
     */
    private boolean isLoading() {
        if (!armourLibrary.isCreativeLibrary()) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(36);
            ItemStack stack = slot.getStack();
            if (stack != null && !(stack.getItem() instanceof ItemSkinTemplate)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isPlayerOp(EntityPlayer player) {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        return minecraftServer.getConfigurationManager().func_152596_g(player.getGameProfile());
    }
    
    private void setFileSwitchType(LibraryFileType type) {
        fileSwitchlocal.setPressed(false);
        fileSwitchRemotePublic.setPressed(false);
        fileSwitchRemotePrivate.setPressed(false);
        switch (type) {
        case LOCAL:
            fileSwitchlocal.setPressed(true);
            reloadButton.visible = true;
            break;
        case SERVER_PUBLIC:
            fileSwitchRemotePublic.setPressed(true);
            reloadButton.visible = false;
            break;
        case SERVER_PRIVATE:
            fileSwitchRemotePrivate.setPressed(true);
            reloadButton.visible = false;
            break;
        }
        fileSwitchType = type;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        String filename = filenameTextbox.getText().trim();
        
        if (button == fileSwitchlocal | button == fileSwitchRemotePublic | button == fileSwitchRemotePrivate) {
            if (button == fileSwitchlocal) {
                setFileSwitchType(LibraryFileType.LOCAL);
            }
            if (button == fileSwitchRemotePublic) {
                setFileSwitchType(LibraryFileType.SERVER_PUBLIC);
            }
            if (button == fileSwitchRemotePrivate) {
                setFileSwitchType(LibraryFileType.SERVER_PRIVATE);
            }
        }
        
        if (button.id == 4) {
            openEquipmentFolder();
        }
        
        if (button == reloadButton) {
            if (fileSwitchType == LibraryFileType.LOCAL) {
                ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
                libraryManager.reloadLibrary();
            }
        }
        
        if (!filename.equals("")) {
            MessageClientGuiLoadSaveArmour message;
            switch (button.id) {
            case BUTTON_ID_LOAD_SAVE:
                boolean clientLoad = false;
                boolean publicList = true;
                
                if (fileSwitchType == LibraryFileType.LOCAL && !mc.isIntegratedServerRunning()) {
                    //Is playing on a server.
                    clientLoad = true;
                }
                
                if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
                    publicList = false;
                }
                
                if (isLoading()) {
                    if (clientLoad) {
                        Skin itemData = SkinIOUtils.loadSkinFromFileName(filename + ".armour");
                        if (itemData != null) {
                            SkinUploadHelper.uploadSkinToServer(itemData);
                        }
                    } else {
                        message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.SERVER_LOAD, publicList);
                        PacketHandler.networkWrapper.sendToServer(message);
                    }
                    filenameTextbox.setText("");
                } else {
                    if (clientLoad) {
                        message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.CLIENT_SAVE, false);
                        PacketHandler.networkWrapper.sendToServer(message);
                    } else {
                        message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.SERVER_SAVE, publicList);
                        PacketHandler.networkWrapper.sendToServer(message);
                    }
                    
                    filenameTextbox.setText("");
                }
                break;
            }
        }
    }
    
    public void setFileName(String text) {
        filenameTextbox.setText(text);
    }
    
    private void openEquipmentFolder() {
        File armourDir = new File(System.getProperty("user.dir"));
        File file = new File(armourDir, LibModInfo.ID);
        String filePath = file.getAbsolutePath();

        if (Util.getOSType() == Util.EnumOS.OSX) {
            try {
                Runtime.getRuntime().exec(new String[] {"/usr/bin/open", filePath});
                return;
            } catch (IOException ioexception1) {
                ModLogger.log(Level.ERROR, "Couldn\'t open file: " + ioexception1);
            }
        } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] {filePath});
            try {
                Runtime.getRuntime().exec(s1);
                return;
            } catch (IOException ioexception) {
                ModLogger.log(Level.ERROR, "Couldn\'t open file: " + ioexception);
            }
        }

        boolean openedFailed = false;

        try {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {file.toURI()});
        } catch (Throwable throwable) {
            ModLogger.log(Level.ERROR, "Couldn\'t open link: " + throwable);
            openedFailed = true;
        }

        if (openedFailed) {
            ModLogger.log("Opening via system class!");
            Sys.openURL("file://" + filePath);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float tickTime) {
        super.drawScreen(mouseX, mouseY, tickTime);
        
        ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
        ArrayList<LibraryFile> files = libraryManager.getServerPublicFileList().getFileList();
        
        loadSaveButton.enabled = true;
        
        if (isLoading()) {
            loadSaveButton.displayString = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "load");
        } else {
            loadSaveButton.displayString = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "save");
        }
        if (!((Slot) inventorySlots.inventorySlots.get(36)).getHasStack() & !armourLibrary.isCreativeLibrary()) {
            loadSaveButton.displayString = "";
            loadSaveButton.enabled = false;
        }
        
        if (fileSwitchType == LibraryFileType.LOCAL) {
            files = libraryManager.getClientPublicFileList().getFileList();
            if (!mc.isIntegratedServerRunning()) {
                loadSaveButton.enabled = false;
                if (isLoading()) {
                    loadSaveButton.enabled = ConfigHandler.allowClientsToUploadSkins;
                } else {
                    loadSaveButton.enabled = ConfigHandler.allowClientsToDownloadSkins;
                }
            }
        } else {
            loadSaveButton.enabled = true;
        }
        
        if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
            files = libraryManager.getServerPrivateFileList(mc.thePlayer).getFileList();
        }
        
        String typeFilter = dropDownList.getListSelectedItem().tag;
        ISkinType skinTypeFilter = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(typeFilter);
        lastSkinType = skinTypeFilter;
        lastSearchText = searchTextbox.getText();
        IGuiListItem selectedItem =  fileList.getSelectedListEntry();
        
        if (selectedItem != null) {
            //deleteButton.enabled = !((GuiFileListItem)selectedItem).getFile().readOnly;
        } else {
            deleteButton.enabled = false;
        }
        
        fileList.setSelectedIndex(-1);
        
        fileList.clearList();
        if (files!= null) {
            for (int i = 0; i < files.size(); i++) {
                LibraryFile file = files.get(i);
                if (skinTypeFilter == null | skinTypeFilter == file.skinType) {
                    if (!searchTextbox.getText().equals("")) {
                        if (file.fileName.toLowerCase().contains(searchTextbox.getText().toLowerCase())) {
                            fileList.addListItem(new GuiFileListItem(file));
                            if (selectedItem != null && ((GuiFileListItem)selectedItem).getFile() == file) {
                                fileList.setSelectedIndex(fileList.getSize() - 1);
                            }
                        }
                    } else {
                        fileList.addListItem(new GuiFileListItem(file));
                        if (selectedItem != null && ((GuiFileListItem)selectedItem).getFile() == file) {
                            fileList.setSelectedIndex(fileList.getSize() - 1);
                        }
                    }
                }
            }
        }
        
        scrollAmount = scrollbar.getValue();
        fileList.setScrollPercentage(scrollbar.getPercentageValue());
        
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        
        if (ConfigHandler.libraryShowsModelPreviews) {
            IGuiListItem item = fileList.getSelectedListEntry();
            if (item != null) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(item.getDisplayName(), true);
                if (skin != null) {
                    SkinPointer skinPointer = new SkinPointer(skin.getSkinType(), skin.lightHash());
                    float x = 80;
                    float y = 90;
                    float scale = 30F;
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)x, (float)y, 50.0F);
                    GL11.glScalef((float)(-scale), (float)scale, (float)scale);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                    float rotation = (float)((double)System.currentTimeMillis() / 10 % 360);
                    GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                    RenderHelper.enableStandardItemLighting();
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glEnable(GL11.GL_NORMALIZE);
                    GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                    ModRenderHelper.enableAlphaBlend();
                    ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true, false);
                    GL11.glPopMatrix();
                }
            }
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        
        
        searchTextbox.mouseClicked(mouseX, mouseY, button);
        filenameTextbox.mouseClicked(mouseX, mouseY, button);
        
        if (button == 1) {
            if (searchTextbox.isFocused()) {
                searchTextbox.setText("");
            }
            if (filenameTextbox.isFocused()) {
                filenameTextbox.setText("");
            }
        }

        if (!dropDownList.getIsDroppedDown()) {
            if (fileList.mouseClicked(mouseX, mouseY, button)) {
                filenameTextbox.setText(fileList.getSelectedListEntry().getDisplayName());
            }
        }
        scrollbar.mousePressed(mc, mouseX, mouseY);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        if (!dropDownList.getIsDroppedDown()) {
            fileList.mouseMovedOrUp(mouseX, mouseY, button);
        }
        scrollbar.mouseReleased(mouseX, mouseY);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        if (!(searchTextbox.textboxKeyTyped(key, keyCode) | filenameTextbox.textboxKeyTyped(key, keyCode))) {
            if (keyCode == 200) {
                //Up
                fileList.setSelectedIndex(fileList.getSelectedIndex() - 1);
            }
            if (keyCode == 208) {
                //Down
                fileList.setSelectedIndex(fileList.getSelectedIndex() + 1);
            }
            super.keyTyped(key, keyCode);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float someFloat,int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        
        ModRenderHelper.enableAlphaBlend();
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - PADDING - neiBump, 0, 180, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        //Input slot
        if (!armourLibrary.isCreativeLibrary()) {
            drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - 18 - PADDING * 2 - 4 - neiBump, 0, 162, 18, 18);
        }
        //Output slot
        drawTexturedModalRect(PADDING + INVENTORY_WIDTH - 26, this.height - INVENTORY_HEIGHT - 26 - PADDING * 2 - neiBump, 18, 154, 26, 26);
        
        ModRenderHelper.disableAlphaBlend();
        
        searchTextbox.drawTextBox();
        filenameTextbox.drawTextBox();
        fileList.drawList(mouseX, mouseY, 0);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (armourLibrary.isCreativeLibrary()) {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName() + "1", 0xCCCCCC);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName() + "0", 0xCCCCCC);
        }
        
        String filesLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.files");
        String filenameLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.filename");
        String searchLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.search");
        /*
        this.fontRendererObj.drawString(filesLabel, 7, 55, 4210752);
        this.fontRendererObj.drawString(filenameLabel, 152, 27, 4210752);
        this.fontRendererObj.drawString(searchLabel, 7, 27, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 48, this.ySize - 96 + 2, 4210752);
        */
    }
}
