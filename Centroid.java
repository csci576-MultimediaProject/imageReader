public class Centroid {
	private int[] pos;

	public Centroid() {
	}

	public Centroid(int[] pos) {
		this.pos = pos;
		return;
	}

	public void pos(int[] pos) {
		this.pos = pos;
	}

	public int[] pos() {
		return this.pos;
	}

	// public void X(double newX) {
	// this.mX = newX;
	// return;
	// }
	//
	// public double X() {
	// return this.mX;
	// }
	//
	// public void Y(double newY) {
	// this.mY = newY;
	// return;
	// }
	//
	// public double Y() {
	// return this.mY;
	// }
}