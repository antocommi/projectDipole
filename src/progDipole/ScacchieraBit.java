package progDipole;

import util.BitMap;

public class ScacchieraBit {
	// Implementazione della scacchiera tramite bitboard. 
	// La scacchiera è 8x8 => 64 celle
	// Due bitboard da 64 bit una per i neri e l'altra per i bianchi
//	private int scacchiere;
	private BitMap whiteBoard, blackBoard ;
	private boolean turnoGiocatore;
	
	public ScacchieraBit() {
		turnoGiocatore = true;
//		scacchiere = 0b01000000000000000000000000000100;
		pedine = new BitMap(16);
	}
	
	public static void main(String[] args) {
		System.out.println("Done!");
	}
}
