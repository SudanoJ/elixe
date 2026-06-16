package elixe.ui;

/**
 * Contract for a GUI control that can host an expandable overlay (color picker,
 * dropdown, etc.). Implemented by both button hierarchies (legacy {@code ui.base}
 * and modern {@code clickgui.controls.base}) so a menu can drive an overlay
 * without depending on a concrete button type.
 */
public interface IElixeOverlay {
	void setOverlayOpen(boolean b);

	boolean overlayClick(int mouseX, int mouseY, int mouseButton);

	void overlayClickReleased(int mouseX, int mouseY, int state);

	void drawOverlay(int mouseX, int mouseY);

	void drawOverlayText(int mouseX, int mouseY);

	int getOverlayY();
}
