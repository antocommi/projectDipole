package Dipole;

import java.util.ArrayList;

public class Player {
	
	private ScacchieraBit scacchiera;
	private int PROFONDITA=3;
	private TTElement[] transpositionTable;
	private boolean giocatoreWhite = true;
	
	private int size = (int)Math.pow(2, 22);
	public Player(ScacchieraBit scacchiera) {
		this.scacchiera = scacchiera;
		this.transpositionTable = new TTElement[size];
	}
	
	public void saveState() {
		scacchiera.generaMosse(0, 3);
		stampaMosse(scacchiera.getMoves());
	}
	
	public void stampaMosse(ArrayList<Mossa> m) {
		System.out.println("Numero mosse disponibili: "+m.size());
		System.out.println("_________________");
		System.out.println(" ");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s);
		p.saveState();
	}
	
	public void visita() {
		
	}
	
	
}
