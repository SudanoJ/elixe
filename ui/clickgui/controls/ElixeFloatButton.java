package elixe.ui.clickgui.controls;

import elixe.modules.option.ModuleFloat;
import elixe.ui.clickgui.controls.base.ElixeButtonNumberBase;

public class ElixeFloatButton extends ElixeButtonNumberBase {
	// opt
	private ModuleFloat option;

	public ElixeFloatButton(String text, ModuleFloat opt, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);

		this.option = opt;
		option.setButton(this);
		
		// slider floats
		setSliderValues((float)opt.getValue(), opt.getMin(), opt.getMax());
	}

	@Override
	protected void onMouseRelease() {
		option.setValue(getValue());
	}
	
	
}