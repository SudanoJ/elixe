package elixe.modules.option;

import elixe.modules.AModuleOption;

/**
 * Non-interactive option that just shows a line of (optionally colored) text in
 * the option panel — useful for warnings/headers. Holds no persisted value, so
 * it is skipped by the config serializer.
 */
public class ModuleLabel extends AModuleOption {
	// packed ARGB color for the text
	private final int color;

	public ModuleLabel(String text) {
		this(text, 0xFFFF4040); // red by default
	}

	public ModuleLabel(String text, int color) {
		this.name = text;
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public Object getValue() {
		return null;
	}

	public void setValue(Object v) {
		// nothing to set on a label
	}
}
