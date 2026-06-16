package elixe.ui;

/**
 * Shared contract for any Elixe GUI control that a module option can drive,
 * regardless of which button hierarchy it belongs to (legacy {@code ui.base}
 * or the modern {@code clickgui.controls.base}). Lets {@link elixe.modules.AModuleOption}
 * hold a reference to either generation while the GUI migration is in progress.
 */
public interface IElixeButton {
	void setValue(Object value);
}
