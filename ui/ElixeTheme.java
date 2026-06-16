package elixe.ui;

/**
 * Central palette for the modern Elixe GUI. Single source of truth so the whole
 * interface stays consistent and can be retinted from one place.
 *
 * Direction: dark + cyan. Colors are stored as GL floats (0..1).
 */
public class ElixeTheme {
	// #0E0E12 - main window background
	public static final float[] BACKGROUND = { 0.055f, 0.055f, 0.071f };
	// slightly lifted surface used for the category bar / panels
	public static final float[] PANEL = { 0.086f, 0.086f, 0.106f };
	// category bar / header
	public static final float[] BAR = { 0.106f, 0.106f, 0.129f };
	// hovered surface
	public static final float[] HOVER = { 0.149f, 0.149f, 0.180f };

	// #22D3EE - accent (active toggles, slider fill, selected category)
	public static final float[] ACCENT = { 0.133f, 0.827f, 0.933f };
	// muted accent for hover / secondary emphasis
	public static final float[] ACCENT_DIM = { 0.090f, 0.420f, 0.482f };

	// text
	public static final float TEXT = 0.86f;
	public static final float TEXT_DIM = 0.45f;

	// inactive control element (off toggle, idle slider track)
	public static final float CONTROL_OFF = 0.30f;

	/** Retint the accent (and derive its dim variant) at runtime, e.g. from the ClickGUI color option. */
	public static void setAccent(float r, float g, float b) {
		ACCENT[0] = r;
		ACCENT[1] = g;
		ACCENT[2] = b;
		ACCENT_DIM[0] = r * 0.55f;
		ACCENT_DIM[1] = g * 0.55f;
		ACCENT_DIM[2] = b * 0.55f;
	}

	/** Accent as a packed ARGB int, for FontRenderer.drawString. */
	public static int accentPacked() {
		int r = (int) (ACCENT[0] * 255f);
		int g = (int) (ACCENT[1] * 255f);
		int b = (int) (ACCENT[2] * 255f);
		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}

	private ElixeTheme() {
	}
}
