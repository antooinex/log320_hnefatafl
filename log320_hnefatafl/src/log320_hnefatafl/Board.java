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
	
	public Move parseMove(String move) {
		//traduire les coups String en coordonnées int
		move = move.trim();
		
		String[] parts = move.split(" - ");
		
		String[] coupDepart = new String[2];
		String[] coupArrivee = new String[2];
		
		coupDepart[0] = parts[0].substring(0,1);
		coupDepart[1] = parts[0].substring(1);
		coupArrivee[0] = parts[1].substring(0,1);
		coupArrivee[1] = parts[1].substring(1);
		
		int xDep = coord(coupDepart[0]);
		int yDep = Integer.parseInt(coupDepart[1]);
		int xArr = coord(coupArrivee[0]);
		int yArr = Integer.parseInt(coupArrivee[1]);
		
		return new Move(xDep, yDep, xArr, yArr);
	}
	
	public void removePiece(String coup, Equipe equipe) {
		//vérifie selon les règles si une pièce autre que le roi doit être retirée du jeu et la retire si c'est le cas
		Move dernierCoup = parseMove(coup);
		Direction[] directions = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
		
		for (Direction direction : directions) {
			int[] voisin = getNeighbor(dernierCoup.getxArr(), dernierCoup.getyArr(), direction);
			int xVoisin = voisin[0];
			int yVoisin = voisin[1];
			if (caseDansPlateau(xVoisin, yVoisin)){
				if(equipe == Equipe.ROUGE) {
					if(this.board[xVoisin-1][yVoisin-1] == 2) {
						int[] voisinDeVoisin = getNeighbor(xVoisin, yVoisin, direction);
						int xVoisinDeVoisin = voisinDeVoisin[0];
						int yVoisinDeVoisin = voisinDeVoisin[1];
						if (caseDansPlateau(xVoisinDeVoisin, yVoisinDeVoisin)){
							if(this.board[xVoisinDeVoisin-1][yVoisinDeVoisin-1] == 4 || estUnCoin(xVoisinDeVoisin, yVoisinDeVoisin)) {
								System.out.println(this.board[xVoisin-1][yVoisin-1]+" ("+xVoisin+","+yVoisin+") retiré.");
								setValue(xVoisin-1, yVoisin-1, 0);
							}
						}
					}
				}
				else if (equipe == Equipe.NOIR) {
					if(this.board[xVoisin-1][yVoisin-1] == 4) {
						int[] voisinDeVoisin = getNeighbor(xVoisin, yVoisin, direction);
						int xVoisinDeVoisin = voisinDeVoisin[0];
						int yVoisinDeVoisin = voisinDeVoisin[1];
						if (caseDansPlateau(xVoisinDeVoisin, yVoisinDeVoisin)){
							if(this.board[xVoisinDeVoisin-1][yVoisinDeVoisin-1] == 2 || this.board[xVoisinDeVoisin-1][yVoisinDeVoisin-1] == 5 || estUnCoin(xVoisinDeVoisin, yVoisinDeVoisin)) {
								System.out.println(this.board[xVoisin-1][yVoisin-1]+" ("+xVoisin+","+yVoisin+") retiré.");
								setValue(xVoisin-1, yVoisin-1, 0);
							}
						}
					}
				}
			}
		}
	}
	
	public boolean estUnCoin(int x, int y) {
		boolean coin = false;
		if((x == 13 && y == 13) || (x == 13 && y == 1) || (x == 1 && y == 13) || (x == 1 && y == 1)) {
			coin = true;
		}
		return coin;
	}
	public boolean caseDansPlateau(int x, int y) {
		boolean dansPlateau = false;
		if(x >= 1 && x <= 13 && y >= 1 && y <= 13) {
			dansPlateau = true;
		}
		return dansPlateau;
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
	
	public boolean update(String coup, Equipe equipe, boolean update) {		
		//mettre à jour le board en vérifiant si le coup est valide
		Move move = parseMove(coup);
		
		int xDep = move.getxDep();
		int yDep = move.getyDep();
		int xArr = move.getxArr();
		int yArr = move.getyArr();
		
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
						if(update) {
							System.out.println("Il y a une pièce sur le chemin demandé.");
						}
					}
				}
			}
			else {
				coupValide = false;
				if(update) {
					System.out.println("Conditions non remplies pour les coordonnées.");
				}
			}
		}
		else {
			coupValide = false;
			if(!coordonneesValides) {
				if(update) {
					System.out.println("Coordonnées invalides.");
				}
			}
			if(!equipeValide) {
				if(update) {
					System.out.println("Équipe invalide.");
				}
			}
		}
		
		if(coupValide && update) {
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
	
	public String antiCoord(int abs) {
		switch(abs) {
		case 1:
			return "A";
		case 2:
			return "B";
		case 3:
			return "C";
		case 4:
			return "D";
		case 5:
			return "E";
		case 6:
			return "F";
		case 7:
			return "G";
		case 8:
			return "H";
		case 9:
			return "I";
		case 10:
			return "J";
		case 11:
			return "K";
		case 12:
			return "L";
		case 13:
			return "M";
		default:
			return "Z";
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
