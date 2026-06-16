package elixe.ui.clickgui.controls.base;

public abstract class ElixeButtonOverlay {
	///overlay - optional
	//upper left location
	public int overlayY, overlayX;
	//size
	public int overlayWidth, overlayHeight;
	
	//contais overlay
	public boolean checkOverlayMouseOver(int mouseX, int mouseY) {
		return (mouseX > this.overlayX && mouseX < this.overlayX + overlayWidth && mouseY > this.overlayY && mouseY < this.overlayY + overlayHeight);
	}
	
}
