package elixe.ui;

import elixe.modules.Module;

/**
 * Contract a ClickGUI menu exposes to the option controls it hosts, so controls
 * (color picker, dropdowns, toggles that rebuild the panel) don't depend on a
 * concrete menu class. Both the legacy {@code clickgui.ElixeMenu} and the modern
 * {@code newclickgui.ElixeMenu} implement it.
 */
public interface IElixeMenu {
	void setOverlay(IElixeOverlay overlay);

	boolean isOverlay(IElixeOverlay overlay);

	void addOptions(Module mod, boolean keepScroll);

	Module getCurrentModule();
}
