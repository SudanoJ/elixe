package elixe.modules.render;

import elixe.Elixe;
import elixe.events.OnKeyEvent;
import elixe.events.OnRender3DEvent;
import elixe.modules.Module;
import elixe.modules.ModuleCategory;
import elixe.modules.option.ModuleColor;
import elixe.modules.option.ModuleFloat;
import elixe.modules.option.ModuleKey;
import elixe.ui.newclickgui.ElixeMenu;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;

public class ClickGUI extends Module {

	private float[] accentColor;
	private float opacity = 1f;

	private ModuleColor colorOption = new ModuleColor("color", 34, 211, 238) {
		public void valueChanged() {
			accentColor = this.getGLRGB();
			// keep the shared theme accent in sync so the HUD/menu always match
			elixe.ui.ElixeTheme.setAccent(accentColor[0], accentColor[1], accentColor[2]);
		}
	};

	private ModuleFloat opacityOption = new ModuleFloat("opacity", 1f, 0.2f, 1f) {
		public void valueChanged() {
			opacity = (float) this.getValue();
		}
	};

	public ClickGUI() {
		super("ClickGUI", ModuleCategory.RENDER, 45); //x
		moduleOptions.add(colorOption);
		moduleOptions.add(opacityOption);
	}

	public ElixeMenu menu = new ElixeMenu(this);

	public float[] getAccentColor() {
		return accentColor;
	}

	public float getMenuOpacity() {
		return opacity;
	}

	public void onEnable() {
		super.onEnable();
		mc.displayGuiScreen(menu);
	}
	
	public void onDisable() {
		super.onDisable();
		mc.displayGuiScreen(null);
	}
}
