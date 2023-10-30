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
	
	public void update(String coup) {
		
		coup = coup.trim();
		
		String[] parts = coup.split(" - ");
		
		String[] coupDepart = new String[2];
		String[] coupArrivee = new String[2];
		
		coupDepart[0] = parts[0].substring(0,1);
		coupDepart[1] = parts[0].substring(1);
		coupArrivee[0] = parts[1].substring(0,1);
		coupArrivee[1] = parts[1].substring(1);
		
		//TODO traduire les coups en coordonnées et mettre à jour le board en vérifiant si le coup est valide
		//1re vérification : coup valide si les 2 lettres sont les mêmes ou si les 2 nombres sont les mêmes
	}
	
	public void draw() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 13; i += 1) {			
			for (int j = 0; j < 13; j += 1) {
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
