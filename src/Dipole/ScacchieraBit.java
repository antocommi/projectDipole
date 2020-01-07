package Dipole;

import java.util.ArrayList;
import java.util.HashMap;

import cestinoDipole.Cella;
import util.ByteMap;;

public class ScacchieraBit {
	// Implementazione della scacchiera tramite bitboard.
	// La scacchiera � 8x8 => 64 celle
	// Due bitboard da 64 bit una per i neri e l'altra per i bianchi
	// private int scacchiere;

	private ByteMap scacchiera;						// 
	private boolean turnoGiocatore;					// 
	private int scacchieraBianchi, scacchieraNeri;	// 
	private int[] numeroStackGiocatore;				// 
	private byte[] listaPedineBianche;				// 
	private byte[] listaPedineNere; 				// 
	private int[] MAX_SPOSTAMENTO; 					// Per ogni direzione -> max_spost in quella direzione
	private ArrayList<Mossa> moves;					// lista delle mosse generate
	private HashMap<String, Integer> riga;			// 

	public static final int SIZE = 8;

	private static final int DIMENSION = 4;
	private static final int VUOTA = -1;
	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;
	private static final int CELLA_BIANCA = 0;
	private static final int CELLA_NERA = 1;
	private static final int STACK_BIANCO = 12;
	private static final int STACK_NERO = 12;
	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int NORTHEAST = 2;
	private static final int SOUTHWEST = 3;
	private static final int SOUTHEAST = 4;
	private static final int NORTHWEST = 5;
	private static final int EAST = 6;
	private static final int WEST = 7;
	private static final int NESSUNA_VITTORIA = 0;
	private static final int VITTORIA_BIANCO = 1;
	private static final int VITTORIA_NERO = 2;
	private static final int MAX_MOSSE = 60;
	private static int[] DIRECTIONS = { -16, 16, -7, 7, 9, -9, 2, -2 };

	public ScacchieraBit() {
		turnoGiocatore = true;
		scacchieraBianchi = 0;
		scacchieraNeri = 0;
		MAX_SPOSTAMENTO = new int[8];
		scacchiera = new ByteMap(8 * 4);
		posizionaPedine(0, 3, 12, PEDINA_NERA);
		posizionaPedine(7, 4, 12, PEDINA_BIANCA);
		riga = new HashMap<>();
		riga.put("A", 0);
		riga.put("B", 1);
		riga.put("C", 2);
		riga.put("D", 3);
		riga.put("E", 4);
		riga.put("F", 5);
		riga.put("G", 6);
		riga.put("H", 7);
		moves = new ArrayList<>();
		listaPedineBianche = new byte[12];
		listaPedineNere = new byte[12];
	}

	private int modifyBit(int numero, int posizione, int valBinario) {
		int mask = 1 << posizione;
		return (numero & ~mask) | ((valBinario << posizione) & mask);
	}

	public void addOnBoard(int i, int j, int color, int qty) {
		int positionOnBoard = i*4+ (j/2);
		if (color == PEDINA_BIANCA) {
			scacchieraBianchi = modifyBit(1, positionOnBoard, scacchieraBianchi);
		} else if (color == PEDINA_NERA) {
			scacchieraNeri = modifyBit(1, positionOnBoard, scacchieraNeri);
		}
	}

	public ByteMap getScacchiera() {
		return scacchiera;
	}

	public void setScacchiera(ByteMap scacchiera) {
		this.scacchiera = scacchiera;
	}

	private String calcolaLettera(int nLettera) {
		return "ABCDEFGH".substring(nLettera, nLettera + 1);
	}

	public boolean checkPosOut(int i, int j) {
		if (i > 7 || j > 7 || i < 0 || j < 0)
			return true;
		return false;
	}

	// converte stringa ("A4") in pos indici
	private int[] calcola_indici(String posizione) {
		int[] res = new int[2];
		res[0] = riga.get(posizione.charAt(0) + "");// get da il valore della chiave che in questo caso è la lettera
		res[1] = Integer.parseInt(posizione.substring(1)) - 1;
		return res;
	}

	public void posizionaPedine(int i, int j, int qty, int color) {
		int a = i;
		int b = j / 2;
		int indiceVettore = i*4+j/2;
		if (color == 0) {
			scacchieraBianchi = modifyBit(1, indiceVettore, scacchieraBianchi);
			scacchiera.setValue(qty, i * 8 + j);
		} else {
			scacchieraNeri = modifyBit(1, indiceVettore, scacchieraNeri);
			scacchiera.setValue(qty, i * 8 + j);
		}
	}

	public int[] calcola_indici(int i, int j, int dir, int nCelleMove) {
		int[] ris = new int[2];
		switch (dir) {
		case NORTH:
			ris[0] = i - nCelleMove;
			ris[1] = j;
			break;
		case NORTHEAST:
			ris[1] = j + nCelleMove;
			ris[0] = i - nCelleMove;
			break;
		case EAST:
			ris[1] = j + nCelleMove;
			ris[0] = i;
			break;
		case SOUTHEAST:
			ris[1] = j + nCelleMove;
			ris[0] = i + nCelleMove;
			break;
		case SOUTH:
			ris[0] = i + nCelleMove;
			ris[1] = j;
			break;
		case SOUTHWEST:
			ris[1] = j - nCelleMove;
			ris[0] = i + nCelleMove;
			break;
		case WEST:
			ris[1] = j - nCelleMove;
			ris[0] = i;
			break;
		case NORTHWEST:
			ris[1] = j - nCelleMove;
			ris[0] = i - nCelleMove;
			break;
		}
		return ris;
	}

	public int calcolaDirezione(int a, int b, int x, int y) {
		int offsetA, offsetB;
		offsetA = a - x;
		offsetB = b - y;
		if (offsetA < 0 && offsetB == 0)
			return SOUTH;
		if (offsetA < 0 && offsetB < 0)
			return SOUTHEAST;
		if (offsetA < 0 && offsetB > 0)
			return SOUTHWEST;
		if (offsetA == 0 && offsetB < 0)
			return EAST;
		if (offsetA == 0 && offsetB > 0)
			return WEST;
		if (offsetA > 0 && offsetB == 0)
			return NORTH;
		if (offsetA > 0 && offsetB < 0)
			return NORTHEAST;
		if (offsetA > 0 && offsetB > 0)
			return NORTHWEST;
		return -1;
	}

	public boolean verDiagonale(int i, int j, int x, int y) {
		int offsetA, offsetB;
		offsetA = Math.abs(i - x);
		offsetB = Math.abs(j - y);
		if (offsetA == offsetB)
			return true;
		if ((offsetA % 2 == 0 && offsetB == 0) || (offsetA == 0 && offsetB % 2 == 0))
			return true;
		return false;
	}

	public int calcolaSpostamento(int a, int b, int x, int y) {
		int k, m;
		k = Math.abs(a - x);
		m = Math.abs(b - y);
		return k >= m ? k : m;
	}

	public boolean verifacaMossaAmmissibile(Mossa m) {
		// TODO
		return false;
	}

	private int cercaPedina(byte posizione, int colorePedina) {
		byte[] v = colorePedina == PEDINA_BIANCA ? listaPedineBianche : listaPedineNere;
		for (int i = 0; i < numeroStackGiocatore[colorePedina]; i++) {
			if (v[i] == posizione)
				return posizione;
		}
		return -1;
	}

	private int calcolaCelleFuori(int a, int b, int x, int y) {
		int dir = calcolaDirezione(a, b, x, y);
		return -1;
	}

	public void muovi(Mossa m) {
		// PRE-CONDIZIONE: m � una mossa ammissibile.
		int x = m.getiStart();
		int y = m.getjStart();
		int a = m.getiEnd();
		int b = m.getjEnd();
		int oldPositionOnBoard = m.getiStart() * 8 + m.getjStart();
		int newPositionOnBoard = m.getiEnd() * 8 + m.getjEnd();
		if (checkPosOut(m.getiEnd(), m.getjEnd())) {
			int pedineDaEliminare = calcolaCelleFuori(x, y, a, b);
			if (numeroStackGiocatore[PEDINA_BIANCA] < 12) {
				listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) newPositionOnBoard;
				// scacchiera.getValue(scacchiera.getIndex());

			} else {
				for (int l = 0; l < 12; l++) {
					if (listaPedineBianche[l] == oldPositionOnBoard) {

					}
					// TODO
				}
			}
		}
	}

	public void generaMosse(int x, int y) {

		int pos = x * 8 + y, i, curr_pos;

		if (checkPosOut(x, y))
			throw new RuntimeException("Indici non consentiti");

		if (scacchiera.getIndex(x, y) == 0)
			throw new RuntimeException("Nessuna pedina disponibile");

		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);
		for (int j : MAX_SPOSTAMENTO) {
			System.out.print(j + " ");
		}
		System.out.println(" ");
		for (int dir = 0; dir < 8; dir++) {
			System.out.println(dir);
			pos = x * 8 + y;
			i = 0;
			curr_pos = pos;
			while (i++ < MAX_SPOSTAMENTO[dir] & curr_pos > 0 & curr_pos < 64) {
				System.out.println("\t" + i);
				curr_pos += DIRECTIONS[dir];
				moves.add(new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir));
			}
		}

		// TODO
		// - Aggiungere generazione mosse di eliminazione pedine dallo stack

	}

	private void calcolaMassimoSpostamento(int[] v, int x, int y) {
		v[NORTH] = x / 2;
		v[SOUTH] = (7 - x) / 2;
		v[EAST] = (7 - y) / 2;
		v[WEST] = y / 2;
		v[NORTHWEST] = Math.min(x, y);
		v[NORTHEAST] = Math.min(x, 7 - y);
		v[SOUTHEAST] = Math.min(7 - x, 7 - y);
		v[SOUTHWEST] = Math.min(7 - x, y);
	}

	public int getColorePedina(int x, int y) {
		int pos = x * 4 + y;
		int mask = 1;
		if (scacchiera.getValue(pos) == 0)
			return -1;
		int bianco = (scacchieraBianchi & (mask << 16 - pos) >>> 16 - pos);
		int nero = (scacchieraNeri & (mask << 16 - pos) >>> 16 - pos);
		return bianco == 1 ? PEDINA_BIANCA : PEDINA_NERA;
	}

	public int[] calcolaIndiciEstesi(int x, int y) {
		int[] res = { x, y * 2 };
		if (x % 2 == 0)
			res[1]++;
		return res;
	}

	public int[] calcolaIndiciRidotti(int x, int y) {
		int[] res = { x, y / 2 };
		return res;
	}

	public ArrayList<Mossa> getMoves() {
		return moves;
	}

	public void setMoves(ArrayList<Mossa> moves) {
		this.moves = moves;
	}

	/**
	 * Funzione utile per debug, stampa la scacchiera. Oss. Sia le pedine bianche
	 * che le nere si trovano su caselle di colore nero.
	 */
	public void stampaScacchiera() {
		System.out.println();
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		for (r = 0; r < SIZE; r++) {
			for (c = 0; c < SIZE; c++)
				System.out.print(" - ");
			System.out.println("-");
			for (c = 0; c < SIZE; c++) {
				if (scacchiera.getNumeroPedine(r, c) == 0)
					if ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0)) {
						System.out.print(" N ");
					} else {
						System.out.print(" B ");
					}
				if (scacchiera.getNumeroPedine(r, c) > 0) {
					if ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0))
						System.out.print(scacchiera.getNumeroPedine(r, c) + "B ");
					else
						System.out.print(scacchiera.getNumeroPedine(r, c) + "N ");
				}
			}
			System.out.println("");
		}
		for (c = 0; c < SIZE; c++)
			System.out.print(" - ");
		System.out.println(" - ");
	}

	public static void main(String[] args) {
		ScacchieraBit scacchiera = new ScacchieraBit();
		scacchiera.stampaScacchiera();
		scacchiera.generaMosse(0, 3);
		// ArrayList<Mossa> m = stato.getMoves();
		// System.out.println(m.size());
		// for (Mossa mossa : m) {
		// System.out.println(mossa);
		// }
	}
}
