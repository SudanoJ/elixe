package elixe.ui.newclickgui.components;

import java.util.ArrayList;

public class RoundedRectangle extends Component {
	private int width, height, radius;
	
	public RoundedRectangle(int radius) {
		this.radius = radius;
	}
	
	public RoundedRectangle(int width, int height, int radius) {
		this.width = width;
		this.height = height;
		this.radius = radius;
		refresh();
	}
	

	public void refresh() {
		getRoundedRect();
	}
	
	private void getRoundedRect() {
		ArrayList<double[]> rectQPointsArray = new ArrayList<double[]>();
		ArrayList<double[]> rectLPointsArray = new ArrayList<double[]>();

		int x1 = radius;
		int y1 = radius;
		int x2 = width - radius;
		int y2 = height - radius;

		double quadRadius = (radius - QUAD_OFFSET), lineRadius = (radius - LINE_OFFSET);
		
		for (int i = 0; i <= 90; i += precision) {
			double sinI = Math.sin(i * degree), cosI = Math.cos(i * degree);
			double pX = x2 + sinI * radius;
			if ((x1 + Math.sin((360 - i) * degree) * radius) <= pX) {
				double[] pointsQ = { x2 + sinI * quadRadius, y2 + cosI * quadRadius };
				double[] pointsL = { x2 + sinI * lineRadius, y2 + cosI * lineRadius };
				rectQPointsArray.add(pointsQ);
				rectLPointsArray.add(pointsL);
			}
		}
		for (int i = 90; i <= 180; i += precision) {
			double sinI = Math.sin(i * degree), cosI = Math.cos(i * degree);
			double pX = x2 + sinI * radius;
			if ((x1 + Math.sin((270 - (i - 90)) * degree) * radius) <= pX) {
				
				double[] pointsQ = { x2 + sinI * quadRadius, y1 + cosI * quadRadius };
				double[] pointsL = { x2 + sinI * lineRadius, y1 + cosI * lineRadius };
				rectQPointsArray.add(pointsQ);
				rectLPointsArray.add(pointsL);
			}
		}
		for (int i = 180; i <= 270; i += precision) {
			double sinI = Math.sin(i * degree), cosI = Math.cos(i * degree);
			double pX = x1 + sinI * radius;
			if ((x2 + Math.sin((i - 180) * degree) * radius) >= pX) {
				
				double[] pointsQ = { x1 + sinI * quadRadius, y1 + cosI * quadRadius };
				double[] pointsL = { x1 + sinI * lineRadius, y1 + cosI * lineRadius };
				rectQPointsArray.add(pointsQ);
				rectLPointsArray.add(pointsL);
			}

		}
		for (int i = 270; i <= 360; i += precision) {
			double sinI = Math.sin(i * degree), cosI = Math.cos(i * degree);
			double pX = x1 + sinI * radius;
			if ((x2 + Math.sin((90 + (i - 270)) * degree) * radius) >= pX) {
				
				double[] pointsQ = { x1 + sinI * quadRadius, y2 + cosI * quadRadius };
				double[] pointsL = { x1 + sinI * lineRadius, y2 + cosI * lineRadius };
				rectQPointsArray.add(pointsQ);
				rectLPointsArray.add(pointsL);
			}
		}

		double[][] rectQPoints = new double[rectQPointsArray.size()][];
		double[][] rectLPoints = new double[rectLPointsArray.size()][];
		
		for (int i = 0; i < rectQPointsArray.size(); i++) {
			rectQPoints[i] = rectQPointsArray.get(i).clone();
		}
		
		for (int i = 0; i < rectLPointsArray.size(); i++) {
			rectLPoints[i] = rectLPointsArray.get(i).clone();
		}
		
		setQuads(rectQPoints);
		setAntialias(rectLPoints);
	}
	
	public void setSize(int wid, int hei) {
		this.width = wid;
		this.height = hei;
		refresh();
	}

}
