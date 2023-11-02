package log320_hnefatafl;

public class Board {

	private int[][] board;
	
	Board(){
		
		this.board = new int[13][13];
		
		for(int i = 0; i < 13; i += 1) {
			for(int j = 0; j < 13; j += 1) {
				this.board[i][j] = 0;
			}
		}	
	}
	
	public void fullUpdate(String buffer){
		
		String[] parts = buffer.split(" ");
		
		int n = 0;
		
		for(int i = 0; i < 13; i += 1) {
			for(int j = 0; j < 13; j += 1) {
				this.board[i][j] = Integer.parseInt(parts[n]);
				n += 1;
			}
		}		
	}
	
	public boolean update(String coup, Equipe equipe) {
		
		coup = coup.trim();
		
		String[] parts = coup.split(" - ");
		
		String[] coupDepart = new String[2];
		String[] coupArrivee = new String[2];
		
		coupDepart[0] = parts[0].substring(0,1);
		coupDepart[1] = parts[0].substring(1);
		coupArrivee[0] = parts[1].substring(0,1);
		coupArrivee[1] = parts[1].substring(1);
		
		//traduire les coups en coordonnées et mettre à jour le board en vérifiant si le coup est valide
		
		int xDep = coord(coupDepart[0]);
		int yDep = Integer.parseInt(coupDepart[1]);
		int xArr = coord(coupArrivee[0]);
		int yArr = Integer.parseInt(coupArrivee[1]);
		
		boolean coordonneesValides = false;
		boolean equipeValide = false;
		if(xDep <= 13 && xDep >= 1 && yDep <= 13 && yDep >= 1 && xArr <= 13 && xArr >= 1 && yArr <= 13 && yArr >= 1) {
			coordonneesValides = true;
			int piece = this.board[xDep-1][yDep-1];
			if(equipe == Equipe.ROUGE) { 
				//on joue les rouges/blancs
				if(piece == 4) {
					equipeValide = true;
				}
				else {
					equipeValide = false;
				}
			}
			else if(equipe == Equipe.NOIR){
				//on joue les noirs
				if(piece == 2 || piece == 5) {
					equipeValide = true;
				}
				else {
					equipeValide = false;
				}
			}
			else {
				equipeValide = false;
			}
			//teste si les coordonnées d'arrivée sont les coins ou le trône
			if((xArr == 13 && yArr == 13) || (xArr == 13 && yArr == 1) || (xArr == 1 && yArr == 13) || (xArr == 1 && yArr == 1) || (xArr == 7 && yArr == 7)) {
				if(piece != 5) {
					//s'il ne s'agit pas du roi
					coordonneesValides = false;
				}
			}
			
		}
		
		boolean coupValide = true;
		/*
		 * Conditions pour un coup valide :
		 * - Même abscisse ou même ordonnée au départ et à l'arrivée
		 * - La coordonnée de départ n'est pas vide
		 * - La coordonnée d'arrivée est vide
		 * - Les coordonnées sont entre 1 et 13 (vérifié avant)
		 * - Il n'y a pas de pièce entre le départ et l'arrivée
		 * - Les coordonnées d'arrivée ne sont pas les coins ni le trône (sauf pour le roi) (vérifié avant)
		 * - La pièce de départ est bien une pièce du joueur (vérifié avant)
		 */
		if(coordonneesValides && equipeValide) {
			if((xDep == xArr || yDep == yArr) && this.getBoard()[xDep-1][yDep-1] != 0 && this.getBoard()[xArr-1][yArr-1] == 0) {
				Direction dir = directionTo(xDep, yDep, xArr, yArr);
				
				int xTemp = xDep;
				int yTemp = yDep;
				
				while((xTemp != xArr || yTemp != yArr) && coupValide == true) {
					int[] neighbor = getNeighbor(xTemp, yTemp, dir);
					xTemp = neighbor[0];
					yTemp = neighbor[1];
				
					if(this.getBoard()[xTemp-1][yTemp-1] != 0) {
						coupValide = false;
						System.out.println("Il y a une pièce sur le chemin demandé.");
					}
				}
			}
			else {
				coupValide = false;
				System.out.println("Conditions non remplies pour les coordonnées.");
			}
		}
		else {
			coupValide = false;
			if(!coordonneesValides) {
				System.out.println("Coordonnées invalides.");
			}
			if(!equipeValide) {
				System.out.println("Équipe invalide.");
			}
		}
		
		if(coupValide) {
			int piece = getBoard()[xDep-1][yDep-1];
			setValue(xArr-1, yArr-1, piece);
			setValue(xDep-1, yDep-1, 0);
			//System.out.println("Plateau mis à jour.");
		}
		
		return coupValide;
	}
	
	public void setValue(int x, int y, int value) {
		board[x][y] = value;
		//System.out.println(x + ";"+ y + " devient "+value);
	}
	
	public int[] getNeighbor(int x, int y, Direction dir) {
		int[] result = new int[2];
		switch (dir) {
	    case UP:
	    	result[0] = x;
	    	result[1] = y+1;
	        return result;
	    case DOWN:
	    	result[0] = x;
	    	result[1] = y-1;
	        return result;
	    case LEFT:
	    	result[0] = x-1;
	    	result[1] = y;
	        return result;
	    case RIGHT:
	    	result[0] = x+1;
	    	result[1] = y;
	        return result;
	    default:
	    	result[0] = 0;
	    	result[1] = 0;
	        return result;
		}
	}
	
    public Direction directionTo(int xDep, int yDep, int xArr, int yArr) {
        if (xDep == xArr) {
            if (yArr > yDep) return Direction.UP;
            else return Direction.DOWN;
        } else if (yDep == yArr) {
            if (xArr > xDep) return Direction.RIGHT;
            else return Direction.LEFT;
        }
        else return Direction.DOWN; //ne devrait jamais être retourné
    }
	
	public int coord(String lettre) {
		switch(lettre) {
		case "A":
			return 1;
		case "B":
			return 2;
		case "C":
			return 3;
		case "D":
			return 4;
		case "E":
			return 5;
		case "F":
			return 6;
		case "G":
			return 7;
		case "H":
			return 8;
		case "I":
			return 9;
		case "J":
			return 10;
		case "K":
			return 11;
		case "L":
			return 12;
		case "M":
			return 13;
		default:
			return -1;
		}
	}
	
	public void draw() {
		StringBuilder sb = new StringBuilder();
		for (int j = 12; j >= 0; j -= 1) {
			for (int i = 0; i < 13; i += 1) {			
				sb.append(Integer.toString(this.board[i][j])+" ");
			}
			sb.append("\n");
		}
		System.out.println(sb);
	}
	
	public int[][] getBoard(){
		return this.board;
	}
	
	public void reset() {
		for(int i = 0; i < 13; i += 1) {
			for(int j = 0; j < 13; j += 1) {
				this.board[i][j] = 0;
			}
		}
	}
	
}
