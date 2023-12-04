package log320_hnefatafl;

public class Board {

	private int[][] board;
	private Board parent;
	private Winner winner = Winner.UNDETERMINED;
	private boolean gameOver = false;
	private int nbPiecesNoir = 13;
	private int nbPiecesRouge = 24;
	private int xRoi = 6;
	private int yRoi = 6;
	private int compteurRoi = 0;
	
	Board(){
		
		this.parent = null;			
		this.board = new int[13][13];
		
		for(int i = 0; i < 13; i += 1) {
			for(int j = 0; j < 13; j += 1) {
				this.board[i][j] = 0;
			}
		}	
	}
	
	Board(Board parent, Move coupFromParent, Equipe equipe){
		this.parent = parent;
		this.board = new int[13][13];
		int[][] b = parent.getBoard();
		this.nbPiecesNoir = parent.getnbPiecesNoir();
		this.nbPiecesRouge = parent.getnbPiecesRouge();
		this.xRoi = parent.getxRoi();
		this.yRoi = parent.getyRoi();
		
		for(int i = 0; i < 13; i += 1) {
			for(int j = 0; j < 13; j += 1) {
				this.board[i][j] = b[i][j];
			}
		}
		
		this.removePiece(coupFromParent, equipe);
	}
	
	public void removePiece(Move coup, Equipe equipe) {
		Move dernierCoup = coup;
		int xDep = dernierCoup.getxDep();
		int yDep = dernierCoup.getyDep();
		int xArr = dernierCoup.getxArr();
		int yArr = dernierCoup.getyArr();
	
		int piece = this.getBoard()[xDep][yDep];
		setValue(xArr, yArr, piece);
		setValue(xDep, yDep, 0);
		if(getBoard()[xArr][yArr] == 5){
		    this.xRoi=xArr;
		    this.yRoi=yArr;
		}
		
		//vérifie selon les règles si une pièce autre que le roi doit être retirée du jeu et la retire si c'est le cas
		Direction[] directions = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
		this.compteurRoi = 0;
		
		for (Direction direction : directions) {
			
			//vérification si le roi est entouré
			//TODO : vérifier si ça marche + faire les tests avec les coins et les murs
			int[] voisinRoi = getNeighbor(xRoi, yRoi, direction);
			int xVoisinRoi = voisinRoi[0];
			int yVoisinRoi = voisinRoi[1];
			if (caseDansPlateau(xVoisinRoi, yVoisinRoi)){
				if(this.board[xVoisinRoi][yVoisinRoi] == 4 || estLeTrone(xVoisinRoi, yVoisinRoi) || estUnCoin(xVoisinRoi, yVoisinRoi)) {
					this.compteurRoi++;
				}
			}
			else {
				this.compteurRoi++;
			}
			
			int[] voisin = getNeighbor(dernierCoup.getxArr(), dernierCoup.getyArr(), direction);
			int xVoisin = voisin[0];
			int yVoisin = voisin[1];
			if (caseDansPlateau(xVoisin, yVoisin)){
				if(equipe == Equipe.ROUGE) {					
					if(this.board[xVoisin][yVoisin] == 2) { //si le voisin est une pièce noire mais pas roi
						int[] voisinDeVoisin = getNeighbor(xVoisin, yVoisin, direction);
						int xVoisinDeVoisin = voisinDeVoisin[0];
						int yVoisinDeVoisin = voisinDeVoisin[1];
						if (caseDansPlateau(xVoisinDeVoisin, yVoisinDeVoisin)){
							if(this.board[xVoisinDeVoisin][yVoisinDeVoisin] == 4 || estUnCoin(xVoisinDeVoisin, yVoisinDeVoisin) || estLeTrone(xVoisinDeVoisin, yVoisinDeVoisin)) {
								//System.out.println(this.board[xVoisin-1][yVoisin-1]+" ("+xVoisin+","+yVoisin+") retiré.");
								setValue(xVoisin, yVoisin, 0);
								nbPiecesNoir -= 1;
							}
						}
					}
				}
				else if (equipe == Equipe.NOIR) {
					//le roi est dans un coin : victoire des noirs
					if(this.board[xArr][yArr] == 5) {
						if (estUnCoin(xArr, yArr)) {
							this.gameOver = true;
							this.winner = Winner.DEFENDER;
						}
					}
					if(this.board[xVoisin][yVoisin] == 4) {
						int[] voisinDeVoisin = getNeighbor(xVoisin, yVoisin, direction);
						int xVoisinDeVoisin = voisinDeVoisin[0];
						int yVoisinDeVoisin = voisinDeVoisin[1];
						if (caseDansPlateau(xVoisinDeVoisin, yVoisinDeVoisin)){
							if(this.board[xVoisinDeVoisin][yVoisinDeVoisin] == 2 || this.board[xVoisinDeVoisin][yVoisinDeVoisin] == 5 || estUnCoin(xVoisinDeVoisin, yVoisinDeVoisin) || estLeTrone(xVoisinDeVoisin, yVoisinDeVoisin)) {
								//System.out.println(this.board[xVoisin-1][yVoisin-1]+" ("+xVoisin+","+yVoisin+") retiré.");
								setValue(xVoisin, yVoisin, 0);
								nbPiecesRouge -= 1;
							}
						}
					}
				}
			}	
		}
		if(compteurRoi == 4) {
			System.out.println("ROI ENTOURÉ");
			this.gameOver = true;
			this.winner = Winner.ATTACKER;
		}
	}
	
	public boolean estLeTrone(int x, int y) {
		boolean trone = false;
		if(x == 6 && y == 6) {
			trone = true;
		}
		return trone;
	}
	
	public boolean estUnCoin(int x, int y) {
		boolean coin = false;
		if((x == 12 && y == 12) || (x == 12 && y == 0) || (x == 0 && y == 12) || (x == 0 && y == 0)) {
			coin = true;
		}
		return coin;
	}
	public boolean caseDansPlateau(int x, int y) {
		boolean dansPlateau = false;
		if(x >= 0 && x <= 12 && y >= 0 && y <= 12) {
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
	
	public boolean update(Move move, Equipe equipe, boolean update) {		
		//mettre à jour le board en vérifiant si le coup est valide		
		int xDep = move.getxDep();
		int yDep = move.getyDep();
		int xArr = move.getxArr();
		int yArr = move.getyArr();
		
		boolean coordonneesValides = false;
		boolean equipeValide = false;
		if(xDep <= 12 && xDep >= 0 && yDep <= 12 && yDep >= 0 && xArr <= 12 && xArr >= 0 && yArr <= 12 && yArr >= 0) {
			coordonneesValides = true;
			int piece = this.board[xDep][yDep];
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
			if(estLeTrone(xArr, yArr) || estUnCoin(xArr, yArr)) {
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
		 * - Les coordonnées sont entre 0 et 12 (vérifié avant)
		 * - Il n'y a pas de pièce entre le départ et l'arrivée
		 * - Les coordonnées d'arrivée ne sont pas les coins ni le trône (sauf pour le roi) (vérifié avant)
		 * - La pièce de départ est bien une pièce du joueur (vérifié avant)
		 */
		if(coordonneesValides && equipeValide) {
			if((xDep == xArr || yDep == yArr) && this.getBoard()[xDep][yDep] != 0 && this.getBoard()[xArr][yArr] == 0) {
				Direction dir = directionTo(xDep, yDep, xArr, yArr);
				
				int xTemp = xDep;
				int yTemp = yDep;
				
				while((xTemp != xArr || yTemp != yArr) && coupValide == true) {
					int[] neighbor = getNeighbor(xTemp, yTemp, dir);
					xTemp = neighbor[0];
					yTemp = neighbor[1];
				
					if(this.getBoard()[xTemp][yTemp] != 0) {
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
			this.removePiece(move, equipe);
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
	
	public void setOver(boolean over) {
		this.gameOver = over;
	}
	
	public void setWinner(Winner winner) {
		this.winner = winner;
	}
	
	public boolean isOver() {
		return this.gameOver;
	}
	
	public Winner getWinner() {
		return this.winner;
	}
	
	public Board getParent() {
		return this.parent;
	}
	
	public int getnbPiecesNoir() {
		return this.nbPiecesNoir;
	}
	
	public int getnbPiecesRouge() {
		return this.nbPiecesRouge;
	}
	
	public int getxRoi() {
		return this.xRoi;
	}
	
	public int getyRoi() {
		return this.yRoi;
	}
	
	public int getCompteurRoi() {
		return this.compteurRoi;
	}
	
}
