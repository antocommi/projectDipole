package Dipole;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import util.ByteMap;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;

	private int PLAYER;
	private ScacchieraBit scacchiera;
	private int PROFONDITA = 4;
	private TTElement[] transpositionTable;

	private int size = (int) Math.pow(2, 22);

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.scacchiera = scacchiera;
		this.transpositionTable = new TTElement[size];
	}

	public void saveState() {
		scacchiera.generaMosse(0, 3);
		stampaMosse(scacchiera.getMoves());
	}

	public void stampaMosse(ArrayList<Mossa> m) {
		System.out.println(" ");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println(" ");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}

	/**
	 * Funzione utile per debug, stampa la scacchiera. Oss. Sia le pedine bianche
	 * che le nere si trovano su caselle di colore nero.
	 */
	public void stampaScacchiera(ByteMap scacchiera, ScacchieraBit s) {
		System.out.println();
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		for (r = 0; r < 8; r++) {
			for (c = 0; c < 8; c++)
				System.out.print(" - ");
			System.out.println("-");
			for (c = 0; c < 8; c++) {
				if (scacchiera.getNumeroPedine(r, c) == 0) {
					if ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0)) {
						System.out.print(" N ");
					} else {
						System.out.print(" B ");
					}
				}
				if (scacchiera.getNumeroPedine(r, c) > 0) {
					if (s.getColorePedina(r, c) == PEDINA_NERA) {
						System.out.print(scacchiera.getNumeroPedine(r, c) + "N");
					} else if (s.getColorePedina(r, c) == PEDINA_BIANCA) {
						System.out.print(scacchiera.getNumeroPedine(r, c) + "B");
					}
				}

			}
			System.out.println("");
		}
		for (c = 0; c < 8; c++)
			System.out.print(" - ");
		System.out.println(" - ");
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s, 0);
		p.stampaScacchiera(s.getScacchiera(), s);
		p.saveState();
	}

}
