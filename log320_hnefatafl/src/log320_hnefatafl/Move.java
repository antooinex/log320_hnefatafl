package log320_hnefatafl;

public class Move {
	
	private int xDep, yDep, xArr, yArr;

	Move(int xDep, int yDep, int xArr, int yArr){
		
		this.setxDep(xDep);
		this.setyDep(yDep);
		this.setxArr(xArr);
		this.setyArr(yArr);
		
	}

	public int getxDep() {
		return xDep;
	}

	public void setxDep(int xDep) {
		this.xDep = xDep;
	}

	public int getyArr() {
		return yArr;
	}

	public void setyArr(int yArr) {
		this.yArr = yArr;
	}

	public int getyDep() {
		return yDep;
	}

	public void setyDep(int yDep) {
		this.yDep = yDep;
	}

	public int getxArr() {
		return xArr;
	}

	public void setxArr(int xArr) {
		this.xArr = xArr;
	}
}
