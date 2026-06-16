package elixe.ui.components;

import java.util.ArrayList;

public class Circle extends Component {
	private int x, y, radius;
	
	public Circle(int radius) {
		this.radius = radius;
	}
	
	public Circle(int x, int y, int radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		refresh();
	}
	
	public int getX() {
		return this.x;
	}

	public void refresh() {
		getCircle();
	}
	
	// get points of polygon
	private void getCircle() {
		ArrayList<double[]> circleQPointsArray = new ArrayList<double[]>();
		ArrayList<double[]> circleLPointsArray = new ArrayList<double[]>();

		//ponto central -> rodeia nele
		double quadRadius = (radius - QUAD_OFFSET), lineRadius = (radius - LINE_OFFSET);
		for (int i = 0; i <= 360; i += precision) {
			double sinI = Math.sin(i * degree), cosI = Math.cos(i * degree);
			double[] pointsQ = { x + sinI * quadRadius, y + cosI * quadRadius };
			double[] pointsL = { x + sinI * lineRadius, y + cosI * lineRadius };
			circleQPointsArray.add(pointsQ);
			circleLPointsArray.add(pointsL);
		}

		double[][] circleQPoints = new double[circleQPointsArray.size()][];
		double[][] circleLPoints = new double[circleLPointsArray.size()][];

		for (int i = 0; i < circleQPointsArray.size(); i++) {
			circleQPoints[i] = circleQPointsArray.get(i).clone();
		}
		
		for (int i = 0; i < circleLPointsArray.size(); i++) {
			circleLPoints[i] = circleLPointsArray.get(i).clone();
		}

		setQuads(circleQPoints);
		setLines(circleLPoints);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		refresh();
	}
	
	protected void updateOtherOffset(int x, int y) {
		this.x += x;
		this.y += y;
	}
}
