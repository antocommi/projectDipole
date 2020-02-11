package Dipole;

import java.util.Random;

public class Zobrist {

	private final long[][][] zobrist;
	private final long[] zobrist_turn;

	public Zobrist() {
		Random random = new Random();
		zobrist = new long[64][2][13];
		zobrist_turn = new long[2];
		zobrist_turn[0] = random.nextLong();
		zobrist_turn[1] = random.nextLong();
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 13; k++) {
					zobrist[i][j][k] = random.nextLong();
				}
			}
		}
	}

	public long getHashcode(ScacchieraBit scacchiera, int colore) {
		
		byte[] listaPosizioni = scacchiera.getListaPosizioni(colore);
		int numeroStackGiocatore = scacchiera.getNumeroStackGiocatore(colore);
		long zhash = 0;
		int i, j, k;
		
		for (i = 0; i < numeroStackGiocatore; i++) {
			j = listaPosizioni[i];
			k = scacchiera.getNumeroPedine(j / 8, j % 8);
			zhash ^= zobrist[j][colore][k]; 
		}
		
//		listaPosizioni = scacchiera.getListaPosizioni(1-colore);
		numeroStackGiocatore = scacchiera.getNumeroStackGiocatore(1-colore);
		
		for (i = 0; i < numeroStackGiocatore; i++) {
			j = listaPosizioni[i];
			k = scacchiera.getNumeroPedine(j / 8, j % 8);
			zhash ^= zobrist[j][1-colore][k]; 
		}
		
		zhash ^= zobrist_turn[colore];
		
		return zhash ;
	}
	
	
}