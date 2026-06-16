package elixe.ui.clickgui.controls;

import elixe.modules.ModuleCategory;
import elixe.ui.ElixeTheme;
import elixe.ui.clickgui.controls.base.ElixeButtonBase;
import elixe.utils.render.GUIUtils;

public class ElixeCategoryButton extends ElixeButtonBase {

	private ModuleCategory cat;
	private boolean selected;

	public ModuleCategory getCategory() {
		return cat;
	}

	public void setSelected(boolean b) {
		this.selected = b;
	}

	public boolean isSelected() {
		return selected;
	}

	public ElixeCategoryButton(String text, ModuleCategory cat, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);
		this.cat = cat;
	}


	public void drawButton(int mouseX, int mouseY) {
		boolean hover = checkMouseOver(mouseX, mouseY);

		if (selected) {
			// highlighted nav row + accent bar on the left
			GUIUtils.drawRect(x, y, x + width, y + height, ElixeTheme.HOVER[0], ElixeTheme.HOVER[1], ElixeTheme.HOVER[2], 1f);
			GUIUtils.drawRect(x, y + 3, x + 2, y + height - 3, ElixeTheme.ACCENT[0], ElixeTheme.ACCENT[1], ElixeTheme.ACCENT[2], 1f);
		} else if (hover) {
			GUIUtils.drawRect(x, y, x + width, y + height, ElixeTheme.PANEL[0], ElixeTheme.PANEL[1], ElixeTheme.PANEL[2], 1f);
		}
	}

	public void drawText(int mouseX, int mouseY) {
		boolean hover = checkMouseOver(mouseX, mouseY);
		float c = (selected || hover) ? ElixeTheme.TEXT : ElixeTheme.TEXT_DIM;
		// left-aligned nav label
		fontrenderer.drawStringWithShadow(text, x + 8, controlMiddle - fontrenderer.FONT_HEIGHT / 2, c, 1f);
	}
}
