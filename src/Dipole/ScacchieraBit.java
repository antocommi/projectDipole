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

	private ByteMap scacchiera; //
	private boolean turnoGiocatore; //
	private int scacchieraBianchi, scacchieraNeri; //
	private int[] numeroStackGiocatore; //
	private byte[] listaPedineBianche; //
	private byte[] listaPedineNere; //
	private int[] MAX_SPOSTAMENTO; // Per ogni direzione -> max_spost in quella direzione
	private ArrayList<Mossa> moves; // lista delle mosse generate
	private HashMap<String, Integer> riga; //

	public static final int SIZE = 8;

	private final int[] posInteresantiBianchi = { 0, 5, 2 };
	private final int[] posInteresantiNero = { 3, 1, 4 };

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
		numeroStackGiocatore = new int[2];
	}

	private int modifyBit(int numero, int posizione, int valBinario) {
		int mask = 1 << (31 - posizione);
		return (valBinario & ~mask) | ((numero << (31 - posizione)) & mask);
	}

//	public void addOnBoard(int i, int j, int color, int qty) {
//		int positionOnBoard = i * 4 + (j / 2);
//		if (color == PEDINA_BIANCA) {
//			scacchieraBianchi = modifyBit(1, positionOnBoard, scacchieraBianchi);
//		} else if (color == PEDINA_NERA) {
//			scacchieraNeri = modifyBit(1, positionOnBoard, scacchieraNeri);
//		}
//	}

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
//
//	private ByteMap scacchiera;						// 
//	private boolean turnoGiocatore;					// 
//	private int scacchieraBianchi, scacchieraNeri;	// 
//	private int[] numeroStackGiocatore;				// 
//	private byte[] listaPedineBianche;				// 
//	private byte[] listaPedineNere; 				// 
//	private int[] MAX_SPOSTAMENTO; 					// Per ogni direzione -> max_spost in quella direzione
//	private ArrayList<Mossa> moves;					// lista delle mosse generate
//	private HashMap<String, Integer> riga;			//

	public void posizionaPedine(int i, int j, int qty, int color) {
		int a = i;
		int b = j / 2;
		int indiceVettore = i * 4 + j / 2;
		int indiceVettoreEsteso = i * 8 + j;
		if (color == PEDINA_BIANCA) {
			scacchieraBianchi = modifyBit(1, indiceVettore, scacchieraBianchi);
			scacchiera.setValue(qty, indiceVettoreEsteso);
//			listaPedineBianche[numeroStackGiocatore[color]] = (new Integer(indiceVettoreEsteso)).byteValue();
		} else {
			scacchieraNeri = modifyBit(1, indiceVettore, scacchieraNeri);
			scacchiera.setValue(qty, indiceVettoreEsteso);
//			listaPedineNere[numeroStackGiocatore[color]] = (new Integer(indiceVettoreEsteso)).byteValue();
		}
//		numeroStackGiocatore[color]++;
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

//	private int cercaPedina(byte posizione, int colorePedina) {
//		byte[] v = colorePedina == PEDINA_BIANCA ? listaPedineBianche : listaPedineNere;
//		for (int i = 0; i < numeroStackGiocatore[colorePedina]; i++) {
//			if (v[i] == posizione)
//				return posizione;
//		}
//		return -1;
//	}

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
		int pos, curr_pos, numeroCelleSpostamento = 0;
		System.out.println("x: " + x + " y: " + y);
		if (checkPosOut(x, y))
			throw new RuntimeException("Indici non consentiti");
		if (scacchiera.getIndex(x, y) == 0)
			throw new RuntimeException("Nessuna pedina disponibile");
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y); // Funziona Bene ma vengono salvate anche le mosse di capture
															// anche se non esistono!
		for (int dir = 0; dir < 8; dir++) {
			pos = x * 8 + y;
			numeroCelleSpostamento = 0;
			curr_pos = pos;
			while (numeroCelleSpostamento++ < MAX_SPOSTAMENTO[dir] && curr_pos > 0 && curr_pos < 64) {
				curr_pos += DIRECTIONS[dir];
				Mossa mossa = new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir);
				if (checkMosse(mossa,x,y)) {
					System.out.println("dir: " + dir);
					moves.add(mossa);
				}

			}
		}
		generaMosseFuori(x, y);
	}

	public void generaMosseFuori(int x, int y) {
		int numeroCelleSpostamento = 0;
		int[] v = getMinimo(MAX_SPOSTAMENTO, x, y);

		System.out.println("minimo" + v[0] + " " + v[1]);
		int curr_pos = (x * 8 + y) + MAX_SPOSTAMENTO[v[0]] * DIRECTIONS[v[0]];
		while (numeroCelleSpostamento++ < 12) {
			curr_pos += DIRECTIONS[v[0]];
			moves.add(new Mossa(x, y, curr_pos / 8, curr_pos % 8, v[0]));
		}
	}

	public boolean checkMosse(Mossa m,int x, int y) {
		//BASE
		int c = getColorePedina(x, y);
		if(c==PEDINA_BIANCA){
			
		}
		else if(c==PEDINA_NERA) {}
		//MERGE
		//CAPTURE
		
		// TODO 
		// P.s. Vengono valutate solo le mosse valide interne alla scacchiera.

	}

	public int[] getMinimo(int[] v, int x, int y) {
		int min = 10, i;
		int[] ret = new int[2];
		int c = getColorePedina(x, y);
		System.out.println(c);
		if (c == PEDINA_BIANCA) {
			for (i = 0; i < posInteresantiBianchi.length; i++) {
				if (v[posInteresantiBianchi[i]] < min) {
					ret[0] = posInteresantiBianchi[i]; // direzione
					ret[1] = v[posInteresantiBianchi[i]]; // elemento
					min = v[posInteresantiBianchi[i]];
				}
			}
		} else {
			for (i = 0; i < posInteresantiNero.length; i++) {
				if (v[posInteresantiNero[i]] < min) {
					ret[0] = posInteresantiNero[i];
					ret[1] = v[posInteresantiNero[i]];
					min = v[posInteresantiNero[i]];
				}
			}
		}

		return ret;
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
		int pos = x * 4 + y / 2;
		int mask = 1;
		// System.out.println("\n Pedine nella posizione "+x+","+y+":
		// "+scacchiera.getValue(pos)+"\n");
		if (scacchiera.getValue(x * 8 + y) == 0)
			return -1;
		int eBianco = ((scacchieraBianchi & (mask << (31 - pos))) >>> (31 - pos));
		int eNero = ((scacchieraNeri & (mask << (31 - pos))) >>> (31 - pos));
		if (eBianco == 1)
			return PEDINA_BIANCA;
		return eNero == 1 ? PEDINA_NERA : 0;
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
//		private int scacchieraBianchi, scacchieraNeri; //
		System.out.println("bit bianchi: " + scacchieraBianchi);
		System.out.println("bit neri: " + scacchieraNeri);
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		scacchiera.printValues();
		for (r = 0; r < SIZE; r++) {
			for (c = 0; c < SIZE; c++)
				System.out.print(" - ");
			System.out.println("-");
			for (c = 0; c < SIZE; c++) {
				if (scacchiera.getNumeroPedine(r, c) == 0) {
					if ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0)) {
						System.out.print(" N ");
					} else {
						System.out.print(" B ");
					}
				}
				if (scacchiera.getNumeroPedine(r, c) > 0) {
					if (getColorePedina(r, c) == PEDINA_NERA) {
						System.out.print(scacchiera.getNumeroPedine(r, c) + "N");
					} else if (getColorePedina(r, c) == PEDINA_BIANCA) {
						System.out.print(scacchiera.getNumeroPedine(r, c) + "B");
					}
				}

			}
			System.out.println("");
		}
		for (c = 0; c < SIZE; c++)
			System.out.print(" - ");
		System.out.println(" - ");
		System.out.print("0,3" + "numero" + scacchiera.getNumeroPedine(0, 3) + " colore" + getColorePedina(0, 3));
		System.out.println("");
		System.out.print("7,4" + "numero" + scacchiera.getNumeroPedine(7, 4) + " colore" + getColorePedina(7, 4));
		System.out.println("");
	}

	public static void main(String[] args) {
		ScacchieraBit scacchiera = new ScacchieraBit();
		scacchiera.stampaScacchiera();
		scacchiera.generaMosse(0, 3);
		ArrayList<Mossa> m = scacchiera.getMoves();
		System.out.println("___________________");
		System.out.println("");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println("___________________");
		System.out.println("");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}
}
