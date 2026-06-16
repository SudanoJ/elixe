package elixe.ui.clickgui.controls.base;

import org.lwjgl.opengl.GL11;

import elixe.ui.ElixeTheme;
import elixe.ui.components.Circle;
import elixe.ui.components.RoundedRectangle;
import elixe.utils.render.GUIUtils;

public abstract class ElixeButtonNumberBase extends ElixeButtonBase {
	
	//titulo do slider, linha do lado
	int titleTextY, titleLineY;
	//texto indicando valor atual
	int valueTextY;
	//inicio do slider, top left
	int sliderY;
	//
	int valueMinMaxTextY;
	
	//values
	private float value;
	//value to width
	private int valueSliderWidth;
	//value string
	protected String valueString;
	protected int valueWidth;
		
	//min max
	private float min, max;
	//min max string
	protected String minString, maxString;
	protected int minWidth, maxWidth;
	
	private final float MINMAX_COLOR = 0.30f;
	
	// etc
	
	
	final int MINMAX_SPACING = 5;
	final int SLIDER_HEIGHT = 4;
	final int HALF_FONT = fontrenderer.FONT_HEIGHT / 2;
	
	final int VALUEWRAPPER_HEIGHT = HALF_FONT + 2;
	
	protected boolean DRAGGING = false;

	private RoundedRectangle sliderBackground = new RoundedRectangle(2);
	private RoundedRectangle slider = new RoundedRectangle(2);
	private Circle sliderDot = new Circle(3);
	
	private RoundedRectangle valueWrapper = new RoundedRectangle(5);

	public ElixeButtonNumberBase(String text, int x, int y, int wid, int hei) {
		super(text, x, y, wid, hei);
		
		sliderBackground.setSize(wid, SLIDER_HEIGHT);
		sliderBackground.setColor(ElixeTheme.CONTROL_OFF, 1);
		slider.setColor(ElixeTheme.ACCENT[0], ElixeTheme.ACCENT[1], ElixeTheme.ACCENT[2], 1);
		sliderDot.setColor(ElixeTheme.ACCENT[0], ElixeTheme.ACCENT[1], ElixeTheme.ACCENT[2], 1);
		valueWrapper.setColor(ElixeTheme.ACCENT[0], ElixeTheme.ACCENT[1], ElixeTheme.ACCENT[2], 1f);
		
		refreshY();
		cacheSliderBackground();
	}
	
	public void setValue(Object v) {
		value = (float) v;
		updateSliderValueParameters();
	}
	
	public float getValue() {
		 return value;
	}
	
	//atualiza slider com valores. atualiza tambem valores da gui
	protected void setSliderValues(float actual, float min, float max) {
		this.value = actual;
		this.min = min;
		this.max = max;
		this.minString = String.format("%.1f", min);
		this.maxString = String.format("%.1f", max);
		minWidth = fontrenderer.getStringWidth(minString) + 4;
		maxWidth = fontrenderer.getStringWidth(maxString);
		cacheSlider();
		updateSliderValueParameters();
	}
	
	//atualiza string com o valor atual do slider
	protected void updateSliderValueParameters() {
		this.valueString = String.format("%.2f", value);
		this.valueWidth = fontrenderer.getStringWidth(valueString);
		
		updateSliderWidth();
		
		valueWrapper.setSize(valueWidth + 8, VALUEWRAPPER_HEIGHT * 2);
		int wrapperX = sliderDot.getX();
		int temp = (valueSliderWidth + valueWidth + 8) - width;
		if (temp > 0) {
			wrapperX -= temp;
		}
		valueWrapper.setPosition(wrapperX, controlMiddle - VALUEWRAPPER_HEIGHT);
	}
	
	//atualiza size do slider pra representar valor atual
	protected void updateSliderWidth() {
		valueSliderWidth = Math.round((width * (value - min)) / (max - min));
		
		slider.setSize(valueSliderWidth, SLIDER_HEIGHT);
		sliderDot.setPosition(x + valueSliderWidth, sliderY + 2);
	}

	//quads
	protected void cacheSlider() {
		slider.setPosition(x, sliderY);
	}

	protected void cacheSliderBackground() {
		sliderBackground.setPosition(x, sliderY);
	}

	
	private void refreshY() {
		titleTextY = controlMiddle + 2 - fontrenderer.FONT_HEIGHT * 2;
		titleLineY = titleTextY + HALF_FONT;
		valueTextY = controlMiddle - HALF_FONT;
		sliderY = controlMiddle + 5 + HALF_FONT;
		valueMinMaxTextY = sliderY + SLIDER_HEIGHT + 4;
	}

	
	public void updatePosition(int xDif, int yDif) {
		sliderBackground.updateOffset(xDif, yDif);
		slider.updateOffset(xDif, yDif);
		sliderDot.updateOffset(xDif, yDif);
		
		valueWrapper.updateOffset(xDif, yDif);
		
		refreshY();
	}

	
	
	public boolean mouseClick(int mouseX, int mouseY, int mouseButton) {
		if (!containsSlider(mouseX, mouseY)) {
			return false;
		}
		DRAGGING = true;
		mouseClickMove(mouseX, mouseY);
		return true;
	}
	
	protected abstract void onMouseRelease();
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (DRAGGING) {
			onMouseRelease();
		}
		DRAGGING = false;
	}
	
	

	
	public void mouseClickMove(int mouseX, int mouseY) {
		if (DRAGGING) {
			if (mouseX > x + width) {
				value = max;
			} else if (x > mouseX) {
				value = min;
			} else {
				//regra de 3
				value = min + (((max - min) * (mouseX - x)) / width);
			}
			updateSliderValueParameters();
		}
	}
 
	public void drawButton(int mouseX, int mouseY) {
		// line
		float c = DRAGGING ? ENABLED_COLOR : DISABLED_COLOR;
		GL11.glColor4f(c, c, c, 1f);
		
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glLineWidth(2f);
		
		GL11.glBegin(GL11.GL_LINES);
		
		GL11.glVertex2f(x + 2 + textWidth, titleLineY);
		GL11.glColor4f(c, c, c, 0f);
		GL11.glVertex2f(x + width, titleLineY);
		
		GL11.glColor4f(MINMAX_COLOR,MINMAX_COLOR,MINMAX_COLOR, 1f);
		GL11.glVertex2f(x + 1, valueMinMaxTextY - 2);
		GL11.glVertex2f(x + 1, valueMinMaxTextY + fontrenderer.FONT_HEIGHT - 2);
		GL11.glVertex2f(x + width - 1, valueMinMaxTextY - 2);
		GL11.glVertex2f(x + width - 1, valueMinMaxTextY + fontrenderer.FONT_HEIGHT - 2);
		
		GL11.glEnd();

		// slider
		sliderBackground.draw();
		slider.draw();
		sliderDot.draw();
		valueWrapper.draw();
	}

	
	public void drawText(int mouseX, int mouseY) {
		fontrenderer.drawStringWithShadow(text, x, titleTextY, 0.86f, 1f); 
		
		fontrenderer.drawStringWithShadow(minString, x + MINMAX_SPACING, valueMinMaxTextY, MINMAX_COLOR, 1f); 
		fontrenderer.drawStringWithShadow(maxString, x + width - maxWidth - MINMAX_SPACING, valueMinMaxTextY, MINMAX_COLOR, 1f); 
		
		fontrenderer.drawString(valueString, valueWrapper.getX() + 4, valueTextY, 0.15f, 1f); 
	}

	private boolean containsSlider(int mouseX, int mouseY) {
		return (mouseX >= x && mouseX <= x + width && mouseY >= sliderY && mouseY <= sliderY + SLIDER_HEIGHT);
	}
}
