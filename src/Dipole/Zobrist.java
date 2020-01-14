package Dipole;

import java.util.Random;

public class Zobrist {

	private final long[][][] zobrist;

	public Zobrist() {
		Random random = new Random();
		zobrist = new long[64][2][12];
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 12; k++) {
					zobrist[i][j][k] = random.nextLong();
				}
			}
		}
	}

	public long getHashcode(ScacchieraBit scacchiera, int colore) {
		
		byte[] listaPosizioni = scacchiera.getListaPosizioni(colore);
		int numeroStackGiocatore = scacchiera.getNumeroStackGiocatore(colore);
		long zhash = 0;
		int i, j;

		for (i = 0; i < numeroStackGiocatore; i++) {
			j = listaPosizioni[i];
			zhash ^= zobrist[j][colore][scacchiera.getNumeroPedine(j / 8, j % 8)]; // TODO: Da ricontrollare
		}

		return zhash;
	}
	
	
}