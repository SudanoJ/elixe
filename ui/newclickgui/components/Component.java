package elixe.ui.newclickgui.components;

import org.lwjgl.opengl.GL11;

public abstract class Component {
	final int precision = 4;
	final double LINE_OFFSET = 0.6D;
	final double QUAD_OFFSET = 0.5D;
	
	final float degree = (float) (Math.PI / 180f);
	
	//quads are quads shrug
	//lines are use for antialising
	private double[][] quadPoints, antialiasPoints;
	
	private float[] color = new float[4];

	private void drawPolygon() {
		GL11.glBegin(GL11.GL_POLYGON);
		for (double[] point : quadPoints) {
			GL11.glVertex3d(point[0], point[1], 0d);
		}
		GL11.glEnd();
	}
	
	private void drawLines() {
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for (double[] point : antialiasPoints) {
			GL11.glVertex2d(point[0], point[1]);
		}
		GL11.glEnd();
	}

	public void draw() {
		GL11.glColor4f(color[0], color[1], color[2], color[3]);
		drawPolygon();
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glLineWidth(2f);
		//drawLines();
	}
	
	protected void setQuads(double[][] q) {
		quadPoints = q;
	}
	
	protected void setAntialias(double[][] l) {
		antialiasPoints = l;
	}
	
	
	public void setColor(float c, float a) {
		for (int i = 0; i < 3; i++) {
			color[i] = c;
		}
		color[3] = a;
	}
	
	public void setColor(float r, float g, float b, float a) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
	}
}
