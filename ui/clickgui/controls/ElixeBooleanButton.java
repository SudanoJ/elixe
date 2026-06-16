package elixe.ui.clickgui.controls;

import elixe.modules.option.ModuleBoolean;
import elixe.ui.IElixeMenu;
import elixe.ui.clickgui.controls.base.ElixeButtonCheckboxBase;

public class ElixeBooleanButton extends ElixeButtonCheckboxBase {

	ModuleBoolean option;
	
	//menu ref
	IElixeMenu menu;

	public ElixeBooleanButton(IElixeMenu menu, String text, ModuleBoolean opt, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);
		opt.setButton(this);
		this.option = opt;
		this.menu = menu;
		
		setEnabled((boolean) option.getValue());
	}

	
	public void setValue(Object v) {
		setEnabled((boolean) v);
	}
	
	
	public boolean mouseClick(int mouseX, int mouseY,  int mouseButton) {
		if (!checkMouseOver(mouseX, mouseY)) {
			return false;
		}
		setEnabled(!isEnabled());
		option.setValue(isEnabled());
		if (option.shouldUpdate()) {
			menu.addOptions(menu.getCurrentModule(), true);
		}
		return true;
	}
}
