package cestinoDipole;

import util.ByteMap;;

public class ScacchieraBit {
	// Implementazione della scacchiera tramite bitboard. 
	// La scacchiera è 8x8 => 64 celle
	// Due bitboard da 64 bit una per i neri e l'altra per i bianchi
//	private int scacchiere;
	
	private final int DIMENSION = 8;
	private ByteMap map;
	private boolean turnoGiocatore;
	private int scacchieraBianchi, scacchieraNeri;
	
	// isBlack(i,j) -> (((1 << i*8+j) & scacchieraBianchi) >> i*8+j) == 1
	
	
	private int modifyBit(int numero, int posizione, int valBinario) {
		int mask = 1 << posizione;
		return (numero & ~mask) | ((valBinario << posizione) & mask);
	}
	
	private int getPositionOnBoard(int i, int j) {
		return i*8+j;
	}
	
	private void addOnBoard(int i, int j, int color, int qty) {
		if(color==1) {
			scacchieraBianchi =modifyBit(1, getPositionOnBoard(i,j), scacchieraBianchi);
		}
		else if(color==0) {
			scacchieraNeri = modifyBit(1, getPositionOnBoard(i,j), scacchieraNeri);
		}
	}
	
	public ScacchieraBit() {
		turnoGiocatore = true;
//		scacchiere = 0b01000000000000000000000000000100;
		scacchieraBianchi = 0;
		scacchieraNeri = 0;
		scacchieraBianchi = modifyBit(1, getPositionOnBoard(0, 3), scacchieraBianchi);
		scacchieraNeri = modifyBit(1, getPositionOnBoard(7, 4), scacchieraNeri);
		whiteMap = new ByteMap(DIMENSION*DIMENSION);
		blackMap = new ByteMap(DIMENSION*DIMENSION); 
	}
	
	
	
	public static void main(String[] args) {
		System.out.println("Done!");
	}
}
