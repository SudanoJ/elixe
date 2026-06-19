package elixe.modules.render;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import elixe.Elixe;
import elixe.events.OnRender2DEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.modules.ModuleManager;
import elixe.modules.option.ModuleBoolean;
import elixe.ui.ElixeTheme;
import elixe.utils.render.GUIUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class HUD extends Module {

	private ModuleManager moduleManager;
	private ArrayList<Module> modules = new ArrayList<Module>();

	public HUD() {
		super("HUD", ModuleCategory.RENDER);

		moduleOptions.add(watermarkOption);
		moduleOptions.add(moduleListOption);
		moduleOptions.add(sprintingOption);
	}

	boolean watermark;
	ModuleBoolean watermarkOption = new ModuleBoolean("watermark", true) {
		public void valueChanged() {
			watermark = (boolean) this.getValue();
		}
	};

	boolean moduleList;
	ModuleBoolean moduleListOption = new ModuleBoolean("module list", true) {
		public void valueChanged() {
			moduleList = (boolean) this.getValue();
		}
	};

	boolean sprinting;
	ModuleBoolean sprintingOption = new ModuleBoolean("sprinting", false) {
		public void valueChanged() {
			sprinting = (boolean) this.getValue();
		}
	};

	public void setModuleManager(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		modules = (ArrayList<Module>) moduleManager.getModules().clone();
		Collections.sort(modules);
	}

	String address = "";

	@EventHandler
	private Listener<OnRender2DEvent> onRender2DEvent = new Listener<>(e -> {
		if (mc.gameSettings.showDebugInfo) {
			return;
		}

		FontRenderer fr = mc.fontRendererObj;
		ScaledResolution sr = new ScaledResolution(mc);
		int sw = sr.getScaledWidth();
		int sh = sr.getScaledHeight();
		int accent = ElixeTheme.accentPacked();

		// ---- watermark (top-left): "elixe  v8.0" ----
		if (watermark) {
			GL11.glPushMatrix();
			GL11.glScalef(1.5f, 1.5f, 1.5f);
			fr.drawStringWithShadow("elixe.lol", 4f, 4f, accent);
			GL11.glPopMatrix();

			int brandWidth = (int) (fr.getStringWidth("elixe.lol") * 1.5f);
			fr.drawStringWithShadow("v" + Elixe.INSTANCE.build, 6 + brandWidth + 4, 6, 0xFFB5B5C0);
			if (!address.isEmpty()) {
				fr.drawStringWithShadow(address, 6, 6 + (int) (fr.FONT_HEIGHT * 1.5f) + 2, 0xFF7A7A85);
			}
		}

		// ---- module list (top-right, longest on top) ----
		if (moduleList) {
			int rowH = fr.FONT_HEIGHT + 3;

			// translucent bar + accent edge per row
			GUIUtils.pre2D();
			int y = 3;
			for (Module m : modules) {
				if (!m.isToggled()) {
					continue;
				}
				int w = fr.getStringWidth(m.getName().toLowerCase());
				int x = sw - w - 6;
				GUIUtils.drawRect(x - 3, y, sw, y + rowH, ElixeTheme.BACKGROUND[0], ElixeTheme.BACKGROUND[1],
						ElixeTheme.BACKGROUND[2], 0.45f);
				GUIUtils.drawRect(sw - 1, y, sw, y + rowH, ElixeTheme.ACCENT[0], ElixeTheme.ACCENT[1],
						ElixeTheme.ACCENT[2], 1f);
				y += rowH;
			}
			GUIUtils.post2D();

			// labels
			y = 3;
			for (Module m : modules) {
				if (!m.isToggled()) {
					continue;
				}
				String name = m.getName().toLowerCase();
				int w = fr.getStringWidth(name);
				int x = sw - w - 6;
				fr.drawStringWithShadow(name, x, y + 2, 0xFFFFFFFF);
				y += rowH;
			}
		}

		// ---- sprint indicator (bottom-left) ----
		if (sprinting && mc.thePlayer != null && mc.thePlayer.isSprinting()) {
			fr.drawStringWithShadow("sprint", 6, sh - 12, accent);
		}
	});

	// handleLoginSuccess(S02PacketLoginSuccess) : void - net.minecraft.client.network.NetHandlerLoginClient
	public void setRemoteAddress(String addr) {
		if (addr.contains("local:")) {
			address = addr;
		} else {
			address = addr.split("/")[1];
		}
	}
}
