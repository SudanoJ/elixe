package elixe.ui.clickgui.controls.base;

import org.lwjgl.opengl.GL11;

import elixe.ui.ElixeTheme;
import elixe.ui.components.Circle;
import elixe.ui.components.RoundedRectangle;
import elixe.utils.render.GUIUtils;

public class ElixeButtonCheckboxBase extends ElixeButtonBase {
	
	private boolean enabled = false;

	protected final int checkBoxWidth = 19;
	protected final int checkBoxHeight = 5;
	private final int circleSpacing = 5;
	
	private Circle dot = new Circle(3);
	private RoundedRectangle background = new RoundedRectangle(checkBoxWidth, checkBoxHeight * 2, 5);

	public ElixeButtonCheckboxBase(String text, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);
		
		dot.setColor(0.10f, 1f);
		// cache polygons
		cacheBackground();
		cacheCircle(enabled);
	}

	// cache polygons
	public void setEnabled(boolean b) {
		enabled = b;
		cacheCircle(b);
	}

	private void cacheCircle(boolean b) {
		dot.setPosition(enabled ? x + width - circleSpacing : x + width - checkBoxWidth + circleSpacing, controlMiddle);
	}

	private void cacheBackground() {
		background.setPosition(x + width - checkBoxWidth, controlMiddle - checkBoxHeight);
	}

	protected boolean isEnabled() {
		return enabled;
	}

	// update all values
	
	public void updatePosition(int xDif, int yDif) {
		dot.updateOffset(xDif, yDif);
		background.updateOffset(xDif, yDif);
	}

	
	public void drawText(int mouseX, int mouseY) {
		fontrenderer.drawStringWithShadow(text, x, controlMiddle - fontrenderer.FONT_HEIGHT / 2, 0.86f, 1f); // 220
	}

	public void drawButton(int mouseX, int mouseY) {
		if (enabled) {
			background.setColor(ElixeTheme.ACCENT[0], ElixeTheme.ACCENT[1], ElixeTheme.ACCENT[2], 1f);
		} else {
			background.setColor(ElixeTheme.CONTROL_OFF, 1f);
		}
		background.draw();
		dot.draw();

		// connecting line: accent when on, dim when off
		float lr = enabled ? ElixeTheme.ACCENT[0] : ElixeTheme.CONTROL_OFF;
		float lg = enabled ? ElixeTheme.ACCENT[1] : ElixeTheme.CONTROL_OFF;
		float lb = enabled ? ElixeTheme.ACCENT[2] : ElixeTheme.CONTROL_OFF;
		GL11.glColor4f(lr, lg, lb, 1f);

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glLineWidth(2f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(x + 2 + textWidth, controlMiddle);
		GL11.glColor4f(lr, lg, lb, 0f);
		GL11.glVertex2f(x + width - checkBoxWidth - 3, controlMiddle);
		GL11.glEnd();
	}

	
	public boolean checkMouseOver(int mouseX, int mouseY) {
		return (mouseX >= this.x + width - checkBoxWidth && mouseX <= this.x + width
				&& mouseY >= controlMiddle - checkBoxHeight && mouseY <= controlMiddle + checkBoxHeight);
	}
}
