package elixe.ui.newclickgui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import elixe.Elixe;
import elixe.modules.AModuleOption;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.modules.option.ModuleArray;
import elixe.modules.option.ModuleArrayMultiple;
import elixe.modules.option.ModuleBoolean;
import elixe.modules.option.ModuleColor;
import elixe.modules.option.ModuleFloat;
import elixe.modules.option.ModuleInteger;
import elixe.modules.option.ModuleKey;
import elixe.modules.option.ModuleLabel;
import elixe.modules.render.ClickGUI;
import elixe.ui.ElixeTheme;
import elixe.ui.IElixeMenu;
import elixe.ui.IElixeOverlay;
import elixe.ui.clickgui.controls.ElixeArrayButton;
import elixe.ui.clickgui.controls.ElixeArrayMultipleButton;
import elixe.ui.clickgui.controls.ElixeBooleanButton;
import elixe.ui.clickgui.controls.ElixeCategoryButton;
import elixe.ui.clickgui.controls.ElixeColorButton;
import elixe.ui.clickgui.controls.ElixeFloatButton;
import elixe.ui.clickgui.controls.ElixeIntegerButton;
import elixe.ui.clickgui.controls.ElixeKeyButton;
import elixe.ui.clickgui.controls.ElixeLabelButton;
import elixe.ui.clickgui.controls.ElixeModuleButton;
import elixe.ui.clickgui.controls.base.ElixeButtonBase;
import elixe.utils.misc.LoggingUtils;
import elixe.utils.render.GUIUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Modern Elixe ClickGUI (dark + cyan), laid out like a SaaS dashboard: a left
 * sidebar (brand, vertical category nav, account chip), a content header showing
 * the current category/module, then the module list + option panel. Hosts the
 * {@code clickgui.controls.*} buttons via {@link IElixeMenu} / {@link IElixeOverlay}.
 */
public class ElixeMenu extends GuiScreen implements IElixeMenu {

	// window
	private int GUI_X = 0, GUI_Y = 0;
	private final int GUI_WIDTH = 400, GUI_HEIGHT = 220;

	// sidebar
	private final int SIDEBAR_WIDTH = 92;
	private final int NAV_TOP = 42;
	private final int CAT_ROW = 22;
	private final int CAT_ITEM_H = 20;

	// content
	private final int CONTENT_TOP = 28; // header band height inside the content area
	private final int MODULE_PANE_WIDTH = 120;
	private final int MODULE_X_PAD = 10;
	private final int MODULE_WIDTH = 100;
	private final int MODULE_HEIGHT = 18;
	private final int MODULE_ROW = 21;

	private final int OPTION_X = SIDEBAR_WIDTH + MODULE_PANE_WIDTH + 8; // offset from GUI_X
	private final int OPTION_WIDTH = GUI_WIDTH - OPTION_X - 10;

	// per-type option sizing (height passed to the control, then row advance)
	private final int SIMPLE_H = 18, SIMPLE_ROW = 22;
	private final int SLIDER_H = 40, SLIDER_ROW = 48;
	// combo: title sits above the closed box (def offset), box reserves the row
	private final int COMBO_H = 16, COMBO_DEF = 18, COMBO_ROW = 36;

	private final ClickGUI CLICKGUI;

	private boolean FIRST = true;
	private int SCALE_FACTOR;

	private ModuleCategory SELECTED_CATEGORY = ModuleCategory.values()[0];
	private Module CURRENT_MODULE;

	private final ElixeCategoryButton[] catButtons = new ElixeCategoryButton[ModuleCategory.values().length];
	private final ArrayList<ElixeModuleButton> modButtons = new ArrayList<ElixeModuleButton>();
	private final ArrayList<ElixeButtonBase> modOptions = new ArrayList<ElixeButtonBase>();
	private IElixeOverlay modOptionOverlay;

	// config view (CONFIG category)
	private boolean configView;
	private String configNameInput = "";
	private final ArrayList<String> configList = new ArrayList<String>();
	private int caretTick;

	public ElixeMenu(ClickGUI ck) {
		CLICKGUI = ck;
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void initGui() {
		if (FIRST) {
			setupMenu();
			FIRST = false;
		} else {
			if (GUI_X + GUI_WIDTH > this.width) {
				updateButtonsPosition(-((GUI_X + GUI_WIDTH) - this.width), 0);
				GUI_X = this.width - GUI_WIDTH;
			}
			if (GUI_Y + GUI_HEIGHT > this.height) {
				updateButtonsPosition(0, -((GUI_Y + GUI_HEIGHT) - this.height));
				GUI_Y = this.height - GUI_HEIGHT;
			}
			for (ElixeModuleButton bt : modButtons) {
				bt.setEnabled(bt.getModule().isToggled());
			}
		}
		ScaledResolution sr = new ScaledResolution(Elixe.INSTANCE.mc);
		SCALE_FACTOR = sr.getScaleFactor();
	}

	private void setupMenu() {
		GUI_X = this.width / 2 - GUI_WIDTH / 2;
		GUI_Y = Math.max(8, this.height / 2 - GUI_HEIGHT / 2);

		// categories as a vertical nav inside the sidebar
		for (int i = 0; i < ModuleCategory.values().length; i++) {
			ModuleCategory cat = ModuleCategory.values()[i];
			catButtons[i] = new ElixeCategoryButton(cat.toString().toLowerCase(), cat,
					GUI_X + 8, GUI_Y + NAV_TOP + i * CAT_ROW, SIDEBAR_WIDTH - 16, CAT_ITEM_H);
		}

		changeCategory(SELECTED_CATEGORY);
	}

	// ------------------------------------------------------------------ areas

	private boolean isInGUIArea(int mouseX, int mouseY) {
		return mouseX >= GUI_X && mouseX <= GUI_X + GUI_WIDTH && mouseY >= GUI_Y && mouseY <= GUI_Y + GUI_HEIGHT;
	}

	private boolean isInModulesArea(int mouseX, int mouseY) {
		return mouseX >= GUI_X + SIDEBAR_WIDTH && mouseX <= GUI_X + SIDEBAR_WIDTH + MODULE_PANE_WIDTH
				&& mouseY >= GUI_Y + CONTENT_TOP && mouseY <= GUI_Y + GUI_HEIGHT;
	}

	private boolean isInOptionsArea(int mouseX, int mouseY) {
		return mouseX >= GUI_X + OPTION_X && mouseX <= GUI_X + GUI_WIDTH && mouseY >= GUI_Y + CONTENT_TOP
				&& mouseY <= GUI_Y + GUI_HEIGHT;
	}

	// strict: an overlay row is inside the scrollable content viewport
	private boolean isInOptionsAreaStrict(int btY) {
		return btY >= GUI_Y + CONTENT_TOP && GUI_Y + GUI_HEIGHT >= btY;
	}

	// cull rows scrolled out of the viewport
	private boolean isInGuiYArea(int btY) {
		return btY >= GUI_Y - 40 && GUI_Y + GUI_HEIGHT >= btY;
	}

	// --------------------------------------------------------------- IElixeMenu

	public void setOverlay(IElixeOverlay overlay) {
		if (modOptionOverlay != null) {
			modOptionOverlay.setOverlayOpen(false);
		}
		modOptionOverlay = overlay;
	}

	public boolean isOverlay(IElixeOverlay overlay) {
		return modOptionOverlay == overlay;
	}

	public Module getCurrentModule() {
		return CURRENT_MODULE;
	}

	// ------------------------------------------------------------------ render

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		dragIfPossible(mouseX, mouseY);
		caretTick++;

		int wheel = Mouse.getDWheel() / 8;
		if (wheel != 0) {
			if (isInOptionsArea(mouseX, mouseY)) {
				updateOptionsPosition(wheel);
			} else if (isInModulesArea(mouseX, mouseY)) {
				updateModulesPosition(wheel);
			}
		}

		// live theme driven by the ClickGUI module options (color + opacity)
		float[] accent = CLICKGUI.getAccentColor();
		if (accent != null) {
			ElixeTheme.setAccent(accent[0], accent[1], accent[2]);
		}
		float opacity = CLICKGUI.getMenuOpacity();

		GL11.glPushMatrix();
		GUIUtils.pre2D();

		// window background
		GUIUtils.drawRect(GUI_X, GUI_Y, GUI_X + GUI_WIDTH, GUI_Y + GUI_HEIGHT,
				ElixeTheme.BACKGROUND[0], ElixeTheme.BACKGROUND[1], ElixeTheme.BACKGROUND[2], opacity);
		// sidebar
		GUIUtils.drawRect(GUI_X, GUI_Y, GUI_X + SIDEBAR_WIDTH, GUI_Y + GUI_HEIGHT,
				ElixeTheme.PANEL[0], ElixeTheme.PANEL[1], ElixeTheme.PANEL[2], opacity);
		// content header band
		GUIUtils.drawRect(GUI_X + SIDEBAR_WIDTH, GUI_Y, GUI_X + GUI_WIDTH, GUI_Y + CONTENT_TOP,
				ElixeTheme.BAR[0], ElixeTheme.BAR[1], ElixeTheme.BAR[2], opacity);
		// thin divider between module list and option panel (only when showing modules)
		if (!configView) {
			GUIUtils.drawRect(GUI_X + OPTION_X - 5, GUI_Y + CONTENT_TOP, GUI_X + OPTION_X - 4, GUI_Y + GUI_HEIGHT,
					ElixeTheme.HOVER[0], ElixeTheme.HOVER[1], ElixeTheme.HOVER[2], opacity);
		}
		// account separator at the sidebar bottom (the head + name are drawn in the text phase)
		GUIUtils.drawRect(GUI_X + 8, GUI_Y + GUI_HEIGHT - 24, GUI_X + SIDEBAR_WIDTH - 8, GUI_Y + GUI_HEIGHT - 23,
				ElixeTheme.HOVER[0], ElixeTheme.HOVER[1], ElixeTheme.HOVER[2], 1f);

		drawButtonsBase(mouseX, mouseY);

		GUIUtils.post2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1f, 1f, 1f, 1f);

		drawButtonsText(mouseX, mouseY);
		drawChrome();

		if (configView) {
			drawConfigPanel(mouseX, mouseY);
		}

		if (modOptionOverlay != null && isInOptionsAreaStrict(modOptionOverlay.getOverlayY())) {
			GlStateManager.enableBlend();
			GUIUtils.pre2D();

			modOptionOverlay.drawOverlay(mouseX, mouseY);

			GUIUtils.post2D();
			GlStateManager.disableBlend();
			GlStateManager.color(1f, 1f, 1f, 1f);

			modOptionOverlay.drawOverlayText(mouseX, mouseY);
		}

		GL11.glPopMatrix();
	}

	private void drawButtonsBase(int mouseX, int mouseY) {
		for (ElixeCategoryButton cat : catButtons) {
			cat.drawButton(mouseX, mouseY);
		}

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissorContent();
		for (ElixeModuleButton btm : modButtons) {
			if (isInGuiYArea(btm.y)) {
				btm.drawButton(mouseX, mouseY);
			}
		}
		for (ElixeButtonBase bti : modOptions) {
			if (isInGuiYArea(bti.y)) {
				bti.drawButton(mouseX, mouseY);
			}
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	private void drawButtonsText(int mouseX, int mouseY) {
		for (ElixeCategoryButton cat : catButtons) {
			cat.drawText(mouseX, mouseY);
		}

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissorContent();
		for (ElixeModuleButton btm : modButtons) {
			if (isInGuiYArea(btm.y)) {
				btm.drawText(mouseX, mouseY);
			}
		}
		for (ElixeButtonBase bti : modOptions) {
			if (isInGuiYArea(bti.y)) {
				bti.drawText(mouseX, mouseY);
			}
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	// brand + version + content header + account name (text phase)
	private void drawChrome() {
		// brand
		GL11.glPushMatrix();
		GL11.glScalef(1.4f, 1.4f, 1.4f);
		mc.fontRendererObj.drawString("elixe", Math.round((GUI_X + 10) / 1.4f), Math.round((GUI_Y + 11) / 1.4f),
				ElixeTheme.accentPacked());
		GL11.glPopMatrix();
		mc.fontRendererObj.drawString("v" + Elixe.INSTANCE.build, GUI_X + 11, GUI_Y + 25, 0xFF6A6A75);

		// content header: category (+ current module)
		String header = SELECTED_CATEGORY.toString().toLowerCase();
		if (CURRENT_MODULE != null) {
			header += " §8/ §7" + CURRENT_MODULE.getName().toLowerCase();
		}
		mc.fontRendererObj.drawStringWithShadow(header, GUI_X + SIDEBAR_WIDTH + 12,
				GUI_Y + (CONTENT_TOP - mc.fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFFFF);

		// account: the player's own skin head (already loaded by the game) + name
		int headSize = 12;
		int hx = GUI_X + 8;
		int hy = GUI_Y + GUI_HEIGHT - 19;
		if (mc.thePlayer != null) {
			mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
			GlStateManager.color(1f, 1f, 1f, 1f);
			GlStateManager.enableBlend();
			drawScaledCustomSizeModalRect(hx, hy, 8f, 8f, 8, 8, headSize, headSize, 64f, 64f); // face
			drawScaledCustomSizeModalRect(hx, hy, 40f, 8f, 8, 8, headSize, headSize, 64f, 64f); // hat overlay
			GlStateManager.disableBlend();
		}
		mc.fontRendererObj.drawString(mc.getSession().getUsername(), hx + headSize + 5,
				hy + (headSize - mc.fontRendererObj.FONT_HEIGHT) / 2 + 1, 0xFFFFFFFF);
	}

	private void scissorContent() {
		glScissor(GUI_X + SIDEBAR_WIDTH, GUI_Y + CONTENT_TOP, GUI_X + GUI_WIDTH, GUI_Y + GUI_HEIGHT);
	}

	private void glScissor(int x1, int y1, int x2, int y2) {
		int w = Math.abs(x1 - x2) * SCALE_FACTOR;
		int h = Math.abs(y1 - y2) * SCALE_FACTOR;
		int xStart = Math.min(x1, x2) * SCALE_FACTOR;
		int yStart = (this.height - Math.max(y1, y2)) * SCALE_FACTOR;
		GL11.glScissor(xStart, yStart, w, h);
	}

	// ------------------------------------------------------------------ input

	private boolean dragging = false;
	private int clickX, clickY;

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		// overlay first (it draws on top, so it gets clicks first)
		if (modOptionOverlay != null && isInOptionsAreaStrict(modOptionOverlay.getOverlayY())) {
			if (modOptionOverlay.overlayClick(mouseX, mouseY, mouseButton)) {
				return;
			}
		}

		// category switch
		for (ElixeCategoryButton cat : catButtons) {
			if (cat.checkMouseOver(mouseX, mouseY)) {
				changeCategory(cat.getCategory());
				return;
			}
		}

		if (configView) {
			configMouseClicked(mouseX, mouseY);
			if (isInGUIArea(mouseX, mouseY) && !dragging) {
				dragging = true;
				clickX = mouseX;
				clickY = mouseY;
			}
			return;
		}

		// module toggle / open options
		for (ElixeModuleButton bt : modButtons) {
			if (!isInGuiYArea(bt.y)) {
				continue;
			}
			if (bt.mouseClick(mouseX, mouseY, mouseButton)) {
				return;
			}
			if (bt.containsArrow(mouseX, mouseY)) {
				addOptions(bt.getModule(), false);
				return;
			}
		}

		// options
		for (ElixeButtonBase opt : modOptions) {
			if (!isInGuiYArea(opt.y)) {
				continue;
			}
			if (opt.mouseClick(mouseX, mouseY, mouseButton)) {
				return;
			}
		}

		// start dragging the window
		if (isInGUIArea(mouseX, mouseY) && !dragging) {
			dragging = true;
			clickX = mouseX;
			clickY = mouseY;
		}
	}

	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (dragging) {
			dragging = false;
			return;
		}
		if (modOptionOverlay != null) {
			modOptionOverlay.overlayClickReleased(mouseX, mouseY, state);
		}
		for (ElixeButtonBase opt : modOptions) {
			opt.mouseReleased(mouseX, mouseY, state);
		}
	}

	private void dragIfPossible(int mouseX, int mouseY) {
		if (dragging) {
			int xDif = mouseX - clickX, yDif = mouseY - clickY;

			int newX = GUI_X + xDif;
			int newY = GUI_Y + yDif;

			if (newX >= 0 && newY >= 0 && this.width >= newX + GUI_WIDTH && this.height >= newY + GUI_HEIGHT) {
				GUI_X = newX;
				GUI_Y = newY;
				updateButtonsPosition(xDif, yDif);
			}

			clickX = mouseX;
			clickY = mouseY;
		}

		for (ElixeButtonBase opt : modOptions) {
			opt.mouseClickMove(mouseX, mouseY);
		}
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (configView) {
			if (keyCode == Keyboard.KEY_ESCAPE) {
				super.keyTyped(typedChar, keyCode);
			} else if (keyCode == Keyboard.KEY_BACK) {
				if (configNameInput.length() > 0) {
					configNameInput = configNameInput.substring(0, configNameInput.length() - 1);
				}
			} else if (keyCode == Keyboard.KEY_RETURN) {
				saveCurrentConfig();
			} else if (configNameInput.length() < 32 && isValidConfigChar(typedChar)) {
				configNameInput += typedChar;
			}
			return;
		}
		super.keyTyped(typedChar, keyCode);
		for (ElixeButtonBase opt : modOptions) {
			opt.keyClick(keyCode);
		}
	}

	public void onGuiClosed() {
		for (ElixeButtonBase opt : modOptions) {
			opt.guiClosed();
		}
		if (CLICKGUI.isToggled()) {
			try {
				dragging = false;
				Elixe.INSTANCE.FILE_MANAGER.MODULE_CONFIG.saveConfig();
				Elixe.INSTANCE.FILE_MANAGER.MODULE_PERSONAL.saveConfig();
				LoggingUtils.out("saving module configurations...");
			} catch (IOException e) {
				LoggingUtils.out("error trying to save module configs");
			}
			CLICKGUI.toggle();
		}
	}

	// ---------------------------------------------------------------- building

	private void changeCategory(ModuleCategory cat) {
		SELECTED_CATEGORY = cat;

		for (ElixeCategoryButton cb : catButtons) {
			cb.setSelected(cb.getCategory() == cat);
		}

		modButtons.clear();
		modOptions.clear();
		modOptionOverlay = null;
		CURRENT_MODULE = null; // header shows just the category until a module is opened

		configView = (cat == ModuleCategory.CONFIG);
		if (configView) {
			refreshConfigList();
			return;
		}

		int moduleX = GUI_X + SIDEBAR_WIDTH + MODULE_X_PAD;
		int offset = 0;
		for (Module m : Elixe.INSTANCE.MODULE_MANAGER.getModulesByCategory(SELECTED_CATEGORY)) {
			int y = GUI_Y + CONTENT_TOP + 4 + offset;
			modButtons.add(new ElixeModuleButton(m.getName().toLowerCase(), m, moduleX, y, MODULE_WIDTH, MODULE_HEIGHT));
			offset += MODULE_ROW;
		}

		modulesContentHeight = offset;
		modulesScrollMax = (GUI_HEIGHT - CONTENT_TOP - 10) - modulesContentHeight;
		if (modulesScrollMax > 0) {
			modulesScrollMax = 0;
		}
		modulesScroll = 0;
	}

	public void addOptions(Module mod, boolean keepScroll) {
		modOptions.clear();
		modOptionOverlay = null;
		CURRENT_MODULE = mod;

		int ox = GUI_X + OPTION_X;
		int oyBase = GUI_Y + CONTENT_TOP + 4;
		int offset = 0;

		for (AModuleOption opt : mod.getOptions()) {
			if (!opt.shouldShow()) {
				continue;
			}
			int y = oyBase + offset;

			if (opt instanceof ModuleLabel) {
				modOptions.add(new ElixeLabelButton(opt.getName(), (ModuleLabel) opt, ox, y, OPTION_WIDTH, SIMPLE_H));
				offset += SIMPLE_ROW;
			} else if (opt instanceof ModuleKey) {
				modOptions.add(new ElixeKeyButton(opt.getName(), (ModuleKey) opt, ox, y, OPTION_WIDTH, SIMPLE_H));
				offset += SIMPLE_ROW;
			} else if (opt instanceof ModuleBoolean) {
				modOptions.add(new ElixeBooleanButton(this, opt.getName(), (ModuleBoolean) opt, ox, y, OPTION_WIDTH, SIMPLE_H));
				offset += SIMPLE_ROW;
			} else if (opt instanceof ModuleFloat) {
				modOptions.add(new ElixeFloatButton(opt.getName(), (ModuleFloat) opt, ox, y, OPTION_WIDTH, SLIDER_H));
				offset += SLIDER_ROW;
			} else if (opt instanceof ModuleInteger) {
				modOptions.add(new ElixeIntegerButton(opt.getName(), (ModuleInteger) opt, ox, y, OPTION_WIDTH, SLIDER_H));
				offset += SLIDER_ROW;
			} else if (opt instanceof ModuleArrayMultiple) {
				modOptions.add(new ElixeArrayMultipleButton(this, opt.getName(), (ModuleArrayMultiple) opt, ox, y, OPTION_WIDTH, COMBO_H, COMBO_DEF));
				offset += COMBO_ROW;
			} else if (opt instanceof ModuleArray) {
				modOptions.add(new ElixeArrayButton(this, opt.getName(), (ModuleArray) opt, ox, y, OPTION_WIDTH, COMBO_H, COMBO_DEF));
				offset += COMBO_ROW;
			} else if (opt instanceof ModuleColor) {
				modOptions.add(new ElixeColorButton(this, opt.getName(), (ModuleColor) opt, ox, y, OPTION_WIDTH, SIMPLE_H));
				offset += SIMPLE_ROW;
			}
		}

		optionsContentHeight = offset;
		refreshOptionsScroll(keepScroll);
	}

	// ------------------------------------------------------------------ config

	private void refreshConfigList() {
		configList.clear();
		configList.addAll(Elixe.INSTANCE.FILE_MANAGER.CONFIG_MANAGER.list());
	}

	private void saveCurrentConfig() {
		String name = configNameInput.trim();
		if (name.isEmpty()) {
			return;
		}
		Elixe.INSTANCE.FILE_MANAGER.CONFIG_MANAGER.save(name);
		configNameInput = "";
		refreshConfigList();
	}

	private boolean isValidConfigChar(char c) {
		return Character.isLetterOrDigit(c) || c == '_' || c == '-';
	}

	private boolean inBox(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx <= x + w && my >= y && my <= y + h;
	}

	// vanilla drawRect manages its own GL state, so the panel can mix rects + text freely
	private void drawConfigPanel(int mouseX, int mouseY) {
		int px = GUI_X + SIDEBAR_WIDTH + 10;
		int top = GUI_Y + CONTENT_TOP + 8;
		int right = GUI_X + GUI_WIDTH - 10;
		int accent = ElixeTheme.accentPacked();

		// name input (with a blinking caret so it's clearly editable)
		int inW = right - px - 96;
		drawRect(px, top, px + inW, top + 16, 0xFF15151B);
		int tx = px + 5;
		mc.fontRendererObj.drawString(configNameInput, tx, top + 4, 0xFFFFFFFF);
		int caretX = tx + mc.fontRendererObj.getStringWidth(configNameInput);
		if (configNameInput.isEmpty()) {
			mc.fontRendererObj.drawString("config name...", tx + 4, top + 4, 0xFF45454F);
		}
		if ((caretTick / 6) % 2 == 0) {
			drawRect(caretX + 1, top + 3, caretX + 2, top + 13, accent);
		}

		// save + open-folder buttons
		int sx = px + inW + 4, sw = 44;
		drawRect(sx, top, sx + sw, top + 16, 0xFF24242C);
		mc.fontRendererObj.drawString("save", sx + (sw - mc.fontRendererObj.getStringWidth("save")) / 2, top + 4, accent);

		int fx = sx + sw + 4, fw = 44;
		drawRect(fx, top, fx + fw, top + 16, 0xFF24242C);
		mc.fontRendererObj.drawString("folder", fx + (fw - mc.fontRendererObj.getStringWidth("folder")) / 2, top + 4, 0xFFB5B5C0);

		// config cards
		int cy = top + 24;
		int cardH = 22;
		if (configList.isEmpty()) {
			mc.fontRendererObj.drawString("no configs yet - type a name and save", px, cy + 6, 0xFF55555F);
			return;
		}
		for (int i = 0; i < configList.size(); i++) {
			int y = cy + i * (cardH + 4);
			if (y + cardH > GUI_Y + GUI_HEIGHT) {
				break; // simple cap (no scroll yet)
			}
			drawRect(px, y, right, y + cardH, 0xFF131319);
			drawRect(px, y, px + 2, y + cardH, accent);
			mc.fontRendererObj.drawString(configList.get(i), px + 9, y + (cardH - 8) / 2, 0xFFE8E8EE);

			int delW = 32, loadW = 40, gap = 5;
			int delX = right - delW - 6;
			int loadX = delX - gap - loadW;
			drawRect(loadX, y + 4, loadX + loadW, y + cardH - 4, 0xFF24242C);
			mc.fontRendererObj.drawString("load", loadX + (loadW - mc.fontRendererObj.getStringWidth("load")) / 2,
					y + (cardH - 8) / 2, accent);
			drawRect(delX, y + 4, delX + delW, y + cardH - 4, 0xFF24242C);
			mc.fontRendererObj.drawString("del", delX + (delW - mc.fontRendererObj.getStringWidth("del")) / 2,
					y + (cardH - 8) / 2, 0xFFE05566);
		}
	}

	private void configMouseClicked(int mouseX, int mouseY) {
		int px = GUI_X + SIDEBAR_WIDTH + 10;
		int top = GUI_Y + CONTENT_TOP + 8;
		int right = GUI_X + GUI_WIDTH - 10;

		int inW = right - px - 96;
		int sx = px + inW + 4, sw = 44;
		int fx = sx + sw + 4, fw = 44;

		if (inBox(mouseX, mouseY, sx, top, sw, 16)) {
			saveCurrentConfig();
			return;
		}
		if (inBox(mouseX, mouseY, fx, top, fw, 16)) {
			Elixe.INSTANCE.FILE_MANAGER.CONFIG_MANAGER.openFolder();
			return;
		}

		int cy = top + 24, cardH = 22;
		for (int i = 0; i < configList.size(); i++) {
			int y = cy + i * (cardH + 4);
			int delW = 32, loadW = 40, gap = 5;
			int delX = right - delW - 6;
			int loadX = delX - gap - loadW;
			if (inBox(mouseX, mouseY, loadX, y + 4, loadW, cardH - 8)) {
				Elixe.INSTANCE.FILE_MANAGER.CONFIG_MANAGER.load(configList.get(i));
				return;
			}
			if (inBox(mouseX, mouseY, delX, y + 4, delW, cardH - 8)) {
				Elixe.INSTANCE.FILE_MANAGER.CONFIG_MANAGER.delete(configList.get(i));
				refreshConfigList();
				return;
			}
		}
	}

	// ------------------------------------------------------------------ scroll

	private int modulesContentHeight, modulesScroll, modulesScrollMax;
	private int optionsContentHeight, optionsScroll, optionsScrollMax;

	private void refreshOptionsScroll(boolean keepScroll) {
		optionsScrollMax = (GUI_HEIGHT - CONTENT_TOP - 10) - optionsContentHeight;
		if (optionsScrollMax > 0) {
			optionsScrollMax = 0;
		}

		if (keepScroll) {
			if (optionsScrollMax > optionsScroll) {
				optionsScroll = optionsScrollMax;
			}
			for (ElixeButtonBase bti : modOptions) {
				bti.setPositionDifference(0, optionsScroll);
			}
		} else {
			optionsScroll = 0;
		}
	}

	private void updateOptionsPosition(int yDif) {
		int newScroll = optionsScroll + yDif;
		if (newScroll > 0) {
			yDif = -optionsScroll;
			optionsScroll = 0;
		} else if (optionsScrollMax > newScroll) {
			yDif = optionsScrollMax - optionsScroll;
			optionsScroll = optionsScrollMax;
		} else {
			optionsScroll += yDif;
		}

		for (ElixeButtonBase bti : modOptions) {
			bti.setPositionDifference(0, yDif);
		}
	}

	private void updateModulesPosition(int yDif) {
		int newScroll = modulesScroll + yDif;
		if (newScroll > 0) {
			yDif = -modulesScroll;
			modulesScroll = 0;
		} else if (modulesScrollMax > newScroll) {
			yDif = modulesScrollMax - modulesScroll;
			modulesScroll = modulesScrollMax;
		} else {
			modulesScroll += yDif;
		}

		for (ElixeModuleButton btm : modButtons) {
			btm.setPositionDifference(0, yDif);
		}
	}

	private void updateButtonsPosition(int xDif, int yDif) {
		for (ElixeCategoryButton cat : catButtons) {
			cat.setPositionDifference(xDif, yDif);
		}
		for (ElixeModuleButton btm : modButtons) {
			btm.setPositionDifference(xDif, yDif);
		}
		for (ElixeButtonBase bti : modOptions) {
			bti.setPositionDifference(xDif, yDif);
		}
	}
}
