package elixe.ui.clickgui.controls;

import elixe.modules.option.ModuleFloat;
import elixe.modules.option.ModuleInteger;
import elixe.ui.clickgui.controls.base.ElixeButtonNumberBase;

public class ElixeIntegerButton extends ElixeButtonNumberBase {
	// opt
	private ModuleInteger option;

	public ElixeIntegerButton(String text, ModuleInteger opt, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);

		this.option = opt;
		option.setButton(this);
		
		// slider floats
		int v = (int)opt.getValue();
		setSliderValues((float)v, opt.getMin(), opt.getMax());
	}
	
	@Override
	protected void onMouseRelease() {
		option.setValue((int)getValue());
	}
	
}
