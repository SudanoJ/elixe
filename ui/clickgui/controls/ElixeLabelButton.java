package elixe.ui.clickgui.controls;

import elixe.modules.option.ModuleLabel;
import elixe.ui.clickgui.controls.base.ElixeButtonBase;

/**
 * Renders a {@link ModuleLabel} as plain colored text. Non-interactive: it never
 * captures clicks or hover.
 */
public class ElixeLabelButton extends ElixeButtonBase {

	private final ModuleLabel option;

	public ElixeLabelButton(String text, ModuleLabel opt, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);
		this.option = opt;
		opt.setButton(this);
	}

	public void drawButton(int mouseX, int mouseY) {
		// no widget to draw
	}

	public void drawText(int mouseX, int mouseY) {
		fontrenderer.drawString(text, x, controlMiddle - fontrenderer.FONT_HEIGHT / 2, option.getColor());
	}

	public boolean mouseClick(int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	public boolean checkMouseOver(int mouseX, int mouseY) {
		return false;
	}
}
