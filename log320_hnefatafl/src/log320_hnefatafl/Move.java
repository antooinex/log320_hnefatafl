package log320_hnefatafl;

public class Move {
	
	private int xDep, yDep, xArr, yArr;
	private String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M"};

	Move(int xDep, int yDep, int xArr, int yArr){
		
		this.setxDep(xDep);
		this.setyDep(yDep);
		this.setxArr(xArr);
		this.setyArr(yArr);
		
	}
	
	Move(String move) {
		//traduire les coups String en coordonnées int
		move = move.trim();
		
		String[] parts = move.split(" - ");
		
		String[] coupDepart = new String[2];
		String[] coupArrivee = new String[2];
		
		coupDepart[0] = parts[0].substring(0,1);
		coupDepart[1] = parts[0].substring(1);
		coupArrivee[0] = parts[1].substring(0,1);
		coupArrivee[1] = parts[1].substring(1);
		
		this.setxDep(coord(coupDepart[0]));
		this.setyDep(Integer.parseInt(coupDepart[1])-1);
		this.setxArr(coord(coupArrivee[0]));
		this.setyArr(Integer.parseInt(coupArrivee[1])-1);
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
	
	public String toString(){
		StringBuilder sb = new StringBuilder();	
		sb.append(alphabet[xDep]);
		sb.append(Integer.toString(yDep+1));
		sb.append(" - ");
		sb.append(alphabet[xArr]);
		sb.append(Integer.toString(yArr+1));
		return sb.toString();
	}
	
	public int coord(String lettre) {
		switch(lettre) {
		case "A":
			return 0;
		case "B":
			return 1;
		case "C":
			return 2;
		case "D":
			return 3;
		case "E":
			return 4;
		case "F":
			return 5;
		case "G":
			return 6;
		case "H":
			return 7;
		case "I":
			return 8;
		case "J":
			return 9;
		case "K":
			return 10;
		case "L":
			return 11;
		case "M":
			return 12;
		default:
			return -1;
		}
	}
	
	public boolean equals(Move other) {
		if(this.xDep == other.xDep && this.yDep == other.yDep && this.xArr == other.xArr && this.yArr == other.yArr) {
			return true;
		}
		else {
			return false;
		}
	}
}
