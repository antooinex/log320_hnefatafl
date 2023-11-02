package log320_hnefatafl;

public class IA {
	
	private Strategie strategie;
	
	public IA(){
		this.strategie = new StrategieAleatoire();
	}
	
	public String jouer(Board board, Equipe equipe) {
		String coup = strategie.coupAJouer(board, equipe);
		System.out.println("IA veut jouer "+coup);
		return coup;
	}
}
