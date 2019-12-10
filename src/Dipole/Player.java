package Dipole;

import java.util.ArrayList;

public class Player {
	
	private ScacchieraBit stato;
	
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
