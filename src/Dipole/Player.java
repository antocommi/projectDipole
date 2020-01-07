package Dipole;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
	
	private ScacchieraBit stato;
	
	private HashMap<TTElement, TTElement> traspositionTable = new HashMap<>();
	
	public Player() {
		super();
		stato = new ScacchieraBit();
		stato.generaMosse(0, 3);
		ArrayList<Mossa> m = stato.getMoves();
		System.out.println(m.size());
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}

	public static void main(String[] args) {
		Player p = new Player();
		System.out.println("done!");
	}
	
}
