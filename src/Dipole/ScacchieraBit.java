package Dipole;

import java.util.ArrayList;
import java.util.HashMap;

import util.ByteMap;;

public class ScacchieraBit {
	// Implementazione della scacchiera tramite bitboard.
	// La scacchiera � 8x8 => 64 celle
	// Due bitboard da 64 bit una per i neri e l'altra per i bianchi
	// private int scacchiere;

	private ByteMap scacchiera; //
	private boolean turnoGiocatore; //
	private int scacchieraBianchi, scacchieraNeri; //
	public int[] numeroStackGiocatore; //
	public byte[] listaPedineBianche; //
	public byte[] listaPedineNere; //
	public int[] MAX_SPOSTAMENTO; // Per ogni direzione -> max_spost in quella direzione
	private ArrayList<Mossa> moves; // lista delle mosse generate
	private int mosseMaxBianco;
	private int mosseMaxNero;
	private int[] pedineI = { 12, 12 };
	public static final int SIZE = 8;

	private static HashMap<String, Integer> riga; //
	private static String[] RIGHE = {"A","B","C","D","E","F","G","H"};
	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int NORTHEAST = 2;
	public static final int SOUTHWEST = 3;
	public static final int SOUTHEAST = 4;
	public static final int NORTHWEST = 5;
	public static final int EAST = 6;
	public static final int WEST = 7;
	private static final int NESSUNA_VITTORIA = 0;
	private static final int VITTORIA_BIANCO = 1;
	private static final int VITTORIA_NERO = 2;
	public final int[] posInteressantiBianchi = { NORTHEAST, NORTHWEST };
	public final int[] posInteressantiNero = { SOUTHWEST, SOUTHEAST };
	public static int[] DIRECTIONS = { -16, 16, -7, 7, 9, -9, 2, -2 };
	private static int[] OUT_DIRECTIONS = { -2, 0, 2, 0, -1, 1, 1, -1, 1, 1, -1, -1, 0, 2, 0, -2 };
	private static int[] OUT_DIRECTIONS2 = { -1, 0, 1, 0, -1, 1, 1, -1, 1, 1, -1, -1, 0, 1, 0, -1 };

	public int getMosseMaxBianco() {
		return mosseMaxBianco;
	}

	public int getMosseMaxNero() {
		return mosseMaxNero;
	}

	public int getNumeroPedineTot(int colore) {
		return pedineI[colore];
	}

	public void debugStatus(boolean stampaMosse, String nome) {
		System.out.println("======================DEBUG INFO: " + nome + "=========================");
		System.out.println("-Deve muovere : " + (turnoGiocatore ? "Bianco" : "Nero"));
		scacchiera.printValues();
		stampaScacchiera();
		String dx = Integer.toBinaryString(scacchieraBianchi);
		String sx = "";
		System.out.println("ScacchieraBianchi: ");
		for (int i = 0; i < 32 - dx.length(); i++) {
			sx += "0";
		}
		dx = sx + dx;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(dx.charAt(i * 4 + j) + " ");
			}
			System.out.println("");
		}
		System.out.println("ScacchieraNeri: ");
		dx = Integer.toBinaryString(scacchieraNeri);
		sx = "";
		for (int i = 0; i < 32 - dx.length(); i++) {
			sx += "0";
		}
		dx = sx + dx;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(dx.charAt(i * 4 + j) + " ");
			}
			System.out.println("");
		}
//		System.out.println("ScacchieraBianchi: " + ());

		System.out.format("# stack bianco: %d \n# stack nero: %d \n", numeroStackGiocatore[PEDINA_BIANCA],
				numeroStackGiocatore[PEDINA_NERA]);

		System.out.print("Pedine del giocatore bianco: [");
		for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
			System.out.print(RIGHE[(listaPedineBianche[i] / 8)] + ((listaPedineBianche[i] % 8)+1));
			if (i < numeroStackGiocatore[PEDINA_BIANCA] - 1)
				System.out.print(", ");
		}
		System.out.println("]");
		System.out.print("Pedine del giocatore nero: [");
		for (int i = 0; i < numeroStackGiocatore[PEDINA_NERA]; i++) {
			System.out.print(RIGHE[(listaPedineNere[i] / 8)] + ((listaPedineNere[i] % 8)+1));
			if (i < numeroStackGiocatore[PEDINA_NERA] - 1)
				System.out.print(", ");
		}
		System.out.println("]");
		System.out.println("MosseMaxBianco: " + mosseMaxBianco);
		System.out.println("MosseMaxNero: " + mosseMaxNero);
		if (stampaMosse) {
			ArrayList<Mossa> moves = getAllMoves();
			System.out.println("Mosse disponibili per chi gioca:");
			if (moves.size() > 0) {
				for (Mossa m : moves) {
					System.out.println("\t" + m);
				}
			} else
				System.out.println("\tNessuna mossa disponibile in questa configurazione per "
						+ (turnoGiocatore ? "Bianco" : "Nero"));
		}
		System.out.println("======================DEBUG INFO=========================");
	}

	public ScacchieraBit() {
		turnoGiocatore = true;
		scacchieraBianchi = 0;
		scacchieraNeri = 0;
		mosseMaxBianco = 60;
		mosseMaxNero = 60;
		MAX_SPOSTAMENTO = new int[8];
		scacchiera = new ByteMap(8 * 4);
		moves = new ArrayList<>();
		listaPedineBianche = new byte[12];
		listaPedineNere = new byte[12];
		numeroStackGiocatore = new int[2];
		posizionaPedine(0, 3, PEDINA_NERA);
		posizionaPedine(7, 4, PEDINA_BIANCA);
		riga = new HashMap<>();
		riga.put("A", 0);
		riga.put("B", 1);
		riga.put("C", 2);
		riga.put("D", 3);
		riga.put("E", 4);
		riga.put("F", 5);
		riga.put("G", 6);
		riga.put("H", 7);
	}

	public ScacchieraBit(ScacchieraBit oldBoard) {
		this();
		mosseMaxBianco = 60;
		mosseMaxNero = 60;
		java.lang.System.arraycopy(oldBoard.listaPedineBianche, 0, this.listaPedineBianche, 0,
				oldBoard.listaPedineBianche.length);
		java.lang.System.arraycopy(oldBoard.listaPedineNere, 0, this.listaPedineNere, 0,
				oldBoard.listaPedineNere.length);
		java.lang.System.arraycopy(oldBoard.numeroStackGiocatore, 0, this.numeroStackGiocatore, 0,
				oldBoard.numeroStackGiocatore.length);
		this.scacchiera = new ByteMap(oldBoard.scacchiera);
		this.scacchieraBianchi = oldBoard.scacchieraBianchi;
		this.scacchieraNeri = oldBoard.scacchieraNeri;
		this.turnoGiocatore = oldBoard.turnoGiocatore;
		this.mosseMaxBianco = oldBoard.mosseMaxBianco;
		this.mosseMaxNero = oldBoard.mosseMaxNero;
	}

	private int modifyBit(int numero, int posizione, int valBinario) {
		int mask = 1 << (31 - posizione);
		return (valBinario & ~mask) | ((numero << (31 - posizione)) & mask);
	}

	public ByteMap getScacchiera() {
		return scacchiera;
	}

	public void setScacchiera(ByteMap scacchiera) {
		this.scacchiera = scacchiera;
	}

	public boolean checkPosOut(int i, int j) {
		if (i > 7 || j > 7 || i < 0 || j < 0)
			return true;
		return false;
	}

	public int getNumeroStackGiocatore(int colore) {
		return numeroStackGiocatore[colore];
	}

	public void posizionaPedine(int i, int j, int color) {
		int indiceVettore = i * 4 + j / 2;
		int indiceVettoreEsteso = i * 8 + j;

		if (color == PEDINA_BIANCA) {
			scacchieraBianchi = modifyBit(1, indiceVettore, scacchieraBianchi);
			scacchiera.setValue(pedineI[color], indiceVettoreEsteso);
			listaPedineBianche[numeroStackGiocatore[color]] = (new Integer(indiceVettoreEsteso)).byteValue();
		} else {
			scacchieraNeri = modifyBit(1, indiceVettore, scacchieraNeri);
			scacchiera.setValue(pedineI[color], indiceVettoreEsteso);
			listaPedineNere[numeroStackGiocatore[color]] = (new Integer(indiceVettoreEsteso)).byteValue();
		}
		numeroStackGiocatore[color]++;
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

//	private int cercaPedina(byte posizione, int colorePedina) {
//		byte[] v = colorePedina == PEDINA_BIANCA ? listaPedineBianche : listaPedineNere;
//		for (int i = 0; i < numeroStackGiocatore[colorePedina]; i++) {
//			if (v[i] == posizione)
//				return posizione;
//		}
//		return -1;
//	}

	public static ScacchieraBit muovi(Mossa m, ScacchieraBit confI, int c) {
//		confI.debugStatus(false,"xx");
		ScacchieraBit confF = new ScacchieraBit(confI);
//		confF.debugStatus(false,"xy");

		confF.muovi(m, c);

		return confF;
	}

	public int nPedine(int c) {
		return pedineI[c];
	}

	public int muovi(Mossa m, int c) {
		int tipo = -1;
		// PRE-CONDIZIONE: m e' una mossa ammissibile.
//		if(!checkMosse(m, c)) {
//			System.out.println("Mossa invalida " + m);
//			debugStatus(true, "mossa invalida in muovi");
//			throw new RuntimeException("mOSSA INVALIDA ");
		//}
		if (m == null) {
			this.debugStatus(true, "m � null");
			throw new RuntimeException("mossa null");
			
			//E' null quando c'� una situazione di stallo!
		}
		int x = m.getiStart();
		int y = m.getjStart();
		int xF = m.getiEnd();
		int yF = m.getjEnd();
	
//				debugStatus(true, "prima");
		
		int oldPositionOnBoard = x * 8 + y;
		int newPositionOnBoard = xF * 8 + yF;
		int nPedineOld = scacchiera.getValue(oldPositionOnBoard);
		int nPedineNew;
		int spostamento;
		int dir = calcolaDirezione(x, y, xF, yF);
		byte[] listaPedine = c == PEDINA_BIANCA ? listaPedineBianche : listaPedineNere;
		if (x == xF && y == yF) {
			turnoGiocatore = !turnoGiocatore;
			if (c == PEDINA_BIANCA)
				mosseMaxBianco--; // TODO: Da controllare se necessario decrementare.
			else
				mosseMaxNero--;
			return -1;
		}
		if (checkPosOut(xF, yF)) {
			// TODO: GESTIRE PEDINE FUORI DOPO AVER SCELTO FORMATO MOSSA PER MOSSE FUORI

			int pedineDaEliminare = Mossa.calcolaCelleFuori(x, y, xF, yF, dir);
			pedineI[c] -= pedineDaEliminare;
			if (pedineDaEliminare == nPedineOld) { // se le elimina tutte in una volta
				for (int l = 0; l < 12; l++) {
					if (listaPedine[l] == oldPositionOnBoard) {
						for (int k = l + 1; k < 12; k++) {
							listaPedine[k - 1] = listaPedine[k];
						}
						break;
					}

				}
				if (c == PEDINA_BIANCA) {
					numeroStackGiocatore[PEDINA_BIANCA]--;
					scacchieraBianchi = modifyBit(0, x * 4 + y / 2, scacchieraBianchi);
				} else {
					numeroStackGiocatore[PEDINA_NERA]--;
					scacchieraNeri = modifyBit(0, x * 4 + y / 2, scacchieraNeri);
				}

			}
			scacchiera.setValue(nPedineOld - pedineDaEliminare, oldPositionOnBoard);

		} else {
			int cF = getColorePedina(xF, yF);
			// Cella di destinazione all'interno della scacchiera
			nPedineNew = scacchiera.getValue(newPositionOnBoard);
			spostamento = calcolaSpostamento(x, y, xF, yF);
			if (nPedineNew == 0) { // BASE
				tipo = 0;
				m.setTipo(tipo);
				// se le sposta tutte
				if (spostamento == nPedineOld) {
					for (int l = 0; l < 12; l++) {
						if (listaPedine[l] == oldPositionOnBoard) {
							listaPedine[l] = (byte) newPositionOnBoard;
							break;
						}
						

					}

					if (c == PEDINA_BIANCA) {
						scacchieraBianchi = modifyBit(0, x * 4 + y / 2, scacchieraBianchi);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF / 2, scacchieraBianchi);
					} else {
						scacchieraNeri = modifyBit(0, x * 4 + y / 2, scacchieraNeri);
						scacchieraNeri = modifyBit(1, xF * 4 + yF / 2, scacchieraNeri);
					}
				} else {
					// se ne sposta una parte
					if (c == PEDINA_BIANCA) {
						listaPedine[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) newPositionOnBoard;
						scacchieraBianchi = modifyBit(1, xF * 4 + yF / 2, scacchieraBianchi);
					} else {
						listaPedine[numeroStackGiocatore[PEDINA_NERA]++] = (byte) newPositionOnBoard;
						scacchieraNeri = modifyBit(1, xF * 4 + yF / 2, scacchieraNeri);
					}
				}
				scacchiera.setValue(nPedineOld - spostamento, oldPositionOnBoard);
				scacchiera.setValue(spostamento, newPositionOnBoard);
			} else if (c == cF) {
				tipo = 1;// MERGE
				m.setTipo(tipo);
				// se le sposta tutte
				if (spostamento == nPedineOld) {
					for (int l = 0; l < 12; l++) {
						if (c == PEDINA_BIANCA) {
							if (listaPedineBianche[l] == oldPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineBianche[k - 1] = listaPedineBianche[k];
								}
								break;

								
							}
						} else {

							if (listaPedineNere[l] == oldPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];
								}
								break;

							}
						}
					}
					if (c == PEDINA_BIANCA) {
						scacchieraBianchi = modifyBit(0, x * 4 + y / 2, scacchieraBianchi);
						numeroStackGiocatore[PEDINA_BIANCA]--;
					} else {
						scacchieraNeri = modifyBit(0, x * 4 + y / 2, scacchieraNeri);
						numeroStackGiocatore[PEDINA_NERA]--;
					}

				}
				scacchiera.setValue(nPedineOld - spostamento, oldPositionOnBoard);
				scacchiera.setValue(nPedineNew + spostamento, newPositionOnBoard);
			} else if (c != cF && spostamento >= nPedineNew) { // CAPTURE
				tipo = 2;
				m.setTipo(tipo);
				// se le sposta tutte
				if (spostamento == nPedineOld) {
					for (int l = 0; l < 12; l++) {
						if (c == PEDINA_BIANCA) {
							if (listaPedineBianche[l] == oldPositionOnBoard) {
								listaPedineBianche[l] = (byte) newPositionOnBoard;

							} 
							if (listaPedineNere[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];
								}
								
							}
						} else {
							if (listaPedineNere[l] == oldPositionOnBoard) {
								listaPedineNere[l] = (byte) newPositionOnBoard;

							} 
							if (listaPedineBianche[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineBianche[k - 1] = listaPedineBianche[k];

								}

							}

						}
					}
					if (c == PEDINA_BIANCA) {
						numeroStackGiocatore[PEDINA_NERA]--;
						scacchieraNeri = modifyBit(0, xF * 4 + yF / 2, scacchieraNeri);
						pedineI[1 - c] -= spostamento;
						scacchieraBianchi = modifyBit(0, x * 4 + y / 2, scacchieraBianchi);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF / 2, scacchieraBianchi);
					} else {
						numeroStackGiocatore[PEDINA_BIANCA]--;
						scacchieraNeri = modifyBit(0, x * 4 + y / 2, scacchieraNeri);
						scacchieraBianchi = modifyBit(0, xF * 4 + yF / 2, scacchieraBianchi);
						pedineI[1 - c] -= spostamento;
						scacchieraNeri = modifyBit(1, xF * 4 + yF / 2, scacchieraNeri);

					}
				} else {// se non le sposta tutte

					if (c == PEDINA_BIANCA) {
						pedineI[1 - c] -= spostamento;
						scacchieraNeri = modifyBit(0, xF * 4 + yF / 2, scacchieraNeri);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF / 2, scacchieraBianchi);
					} else {
						pedineI[1 - c] -= spostamento;
						scacchieraBianchi = modifyBit(0, xF * 4 + yF / 2, scacchieraBianchi);
						scacchieraNeri = modifyBit(1, xF * 4 + yF / 2, scacchieraNeri);
					}

					for (int l = 0; l < 12; l++) {
						if (c == PEDINA_BIANCA) {
							if (listaPedineNere[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];
								}
								break;

							}
						} else {
							if (listaPedineBianche[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineBianche[k - 1] = listaPedineBianche[k];

								}
								break;
							}

						}
					}
					
					listaPedine[numeroStackGiocatore[c]++] = (byte) newPositionOnBoard;
					numeroStackGiocatore[cF]--;

				}

				scacchiera.setValue(nPedineOld - spostamento, oldPositionOnBoard);
				scacchiera.setValue(spostamento, newPositionOnBoard);

			}

		}
		if(!checkPosOut(xF, yF) && getNumeroPedine(xF, yF)==15) {
			debugStatus(true, "dopo");
//			System.out.println("XXXXXXXXXXX");
		}
			
		if (turnoGiocatore)
			mosseMaxBianco--;
		else
			mosseMaxNero--;
		turnoGiocatore = !turnoGiocatore;
		return tipo;

	}

	public byte[] getListaPosizioni(int colore) {
		if (colore == PEDINA_BIANCA) {
			return listaPedineBianche;
		} else {
			return listaPedineNere;
		}
	}

	public boolean zeroMosse(int c) {
		byte[] listaPedine = c == PEDINA_BIANCA ? listaPedineBianche : listaPedineNere;
		for (int i = 0; i < numeroStackGiocatore[c]; i++) {
			if (esisteMossaFuori(listaPedine[i] / 8, listaPedine[i] % 8, c)) {
				return false;
			}
			if (getAllMoves(c)!=null) {
				return false;
			}

		}
		return true;
	}

	public boolean checkFin(ScacchieraBit s) {
		// TODO caso in cui non può più cacciare fuori ma ha ancora pedine
		if (s.mosseMaxBianco == 0 || s.mosseMaxNero == 0) {
//			System.out.println(s.mosseMaxBianco + " " + s.mosseMaxNero);
			return true;
		}
		if ((s.numeroStackGiocatore[PEDINA_BIANCA] == 0 || s.numeroStackGiocatore[PEDINA_NERA] == 0)
				|| (s.zeroMosse(PEDINA_BIANCA) && s.zeroMosse(PEDINA_NERA))) {
			return true;
		}

		return false;
	}

	public void addAllMoves(ArrayList<Mossa> mosse) {
		moves.addAll(mosse);
	}

	public void addMove(Mossa m) {
		moves.add(m);
	}

	public void cleanMoves() {
		moves.clear();
	}

	public ArrayList<Mossa> generaListaMosse(int x, int y, int c) {
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		int pos, curr_pos, numeroCelleSpostamento = 0;
		if (checkPosOut(x, y))
			throw new RuntimeException("Indici non consentiti");
		if (scacchiera.getIndex(x, y) == 0) {
			return listaMosse;
		}
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);
		for (int dir = 0; dir < 8; dir++) {
			pos = x * 8 + y;
			numeroCelleSpostamento = 0;
			curr_pos = pos;

			while (numeroCelleSpostamento++ < MAX_SPOSTAMENTO[dir] && curr_pos > 0 && curr_pos < 64) {
				curr_pos += DIRECTIONS[dir];
				Mossa mossa = new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir);
//				System.out.println(mossa);
				if (checkMosse(mossa, c)) {

					listaMosse.add(mossa);
				}
			}
		}
		if (mosseMaxBianco < 55 || mosseMaxNero < 55)
			if ((c == PEDINA_BIANCA && (x == 0 || x == 1) && (y == 0 || y == 1 || y == 6 || y == 7))
					|| (c == PEDINA_NERA && (x == 6 || x == 7) && (y == 0 || y == 1 || y == 6 || y == 7))) {
				listaMosse.addAll(generaMosseFuori(x, y, c));
			}

		// TODO va aggiustato

		return listaMosse;
	}

	public boolean diagPrinc(Mossa m) {
		int x = m.getiEnd();// dove andrò
		int y = m.getjEnd();
		if (x == y)
			return true;
		return false;
	}

	public boolean possoMangiare(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();// dove andrò
		int y = m.getjEnd();
		int n = board.calcolaSpostamento(m.getiStart(), m.getiEnd(), m.getjStart(), m.getjEnd());
		if (getColorePedina(x, y) == (1 - color) && board.getNumeroPedine(x, y) <= n)
			return true;
		return false;
	}

	public ArrayList<Mossa> generaMosseFuori(int x, int y, int c) {
		int numeroCelleSpostamento = scacchiera.getValue(x * 8 + y);
		ArrayList<Mossa> listaMosse = new ArrayList<>();
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);
//		for(int i:MAX_SPOSTAMENTO)
//			System.out.println("max"+i);
//		
		int[] v = getMinimo(MAX_SPOSTAMENTO, x, y);

//		int curr_pos = (x * 8 + y) + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0]];
		int curr_x = x + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2];
		int curr_y = y + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2 + 1];
		while (numeroCelleSpostamento-- > 0) {
			curr_x += OUT_DIRECTIONS[v[0] * 2];
			curr_y += OUT_DIRECTIONS[v[0] * 2 + 1];
			Mossa m = new Mossa(x, y, curr_x, curr_y, v[0]);
//			System.out.format("%d %d %d %d %d ", x, y, curr_x, curr_y, Mossa.calcolaCelleFuori(x, y, curr_x, curr_y, v[0]));
			if (checkMosse(m, c)) {
//				System.out.println("Aggiunta mossa fuori"+ m);
				listaMosse.add(m);
				break;
			}
//			else System.out.println("");

		}
		
		return listaMosse;
	}

	public boolean esisteMossaFuori(int x, int y, int c) {
		int numeroCelleSpostamento = 0;
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);

		int[] v = getMinimo(MAX_SPOSTAMENTO, x, y);

//		int curr_pos = (x * 8 + y) + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0]];
		int curr_x = x + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2];
		int curr_y = y + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2 + 1];
		while (numeroCelleSpostamento++ < 12) {
			curr_x += OUT_DIRECTIONS[v[0] * 2];
			curr_y += OUT_DIRECTIONS[v[0] * 2 + 1];
			Mossa m = new Mossa(x, y, curr_x, curr_y, v[0]);

			if (checkMosse(m, c)) {
				return true;
			}

		}
		return false;

	}

	public ArrayList<Mossa> getAllMoves(int c) {
		ArrayList<Mossa> listaMosse = new ArrayList<>();
		if (c==PEDINA_BIANCA) {
			for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
				listaMosse
						.addAll(generaListaMosse(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8, PEDINA_BIANCA));
			}

			if (listaMosse.size() == 0) {
				for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
					listaMosse.addAll(
							generaMosseFuori(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8, PEDINA_BIANCA));
					
				}
				
				if (listaMosse.size() == 0) {

					int i = listaPedineBianche[0] / 8;
					int j = listaPedineBianche[0] % 8;
					listaMosse.add(new Mossa(i, j, i, j, 0));
				}
			}
			moves = listaMosse;
//			for(Mossa m: listaMosse)
//				System.out.println("getALLB"+m);
			return listaMosse;
		} else {
			for (int i = 0; i < numeroStackGiocatore[PEDINA_NERA]; i++) {
				listaMosse.addAll(generaListaMosse(listaPedineNere[i] / 8, listaPedineNere[i] % 8, PEDINA_NERA));
			}
			if (listaMosse.size() == 0) {
				for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
					listaMosse.addAll(generaMosseFuori(listaPedineNere[i] / 8, listaPedineNere[i] % 8, PEDINA_NERA));
				}
				

				if (listaMosse.size() == 0) {
					int i = listaPedineNere[0] / 8;
					int j = listaPedineNere[0] % 8;
					listaMosse.add(new Mossa(i, j, i, j, 0));
				}
				
			}
			moves = listaMosse;
//			for(Mossa m: listaMosse)
//				System.out.println("getALLn"+m);
			return listaMosse;
		}
	}
	public ArrayList<Mossa> getAllMoves() {
		ArrayList<Mossa> listaMosse = new ArrayList<>();
		if (turnoGiocatore) {
			for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
				listaMosse
						.addAll(generaListaMosse(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8, PEDINA_BIANCA));
			}

			if (listaMosse.size() == 0) {
				for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
					listaMosse.addAll(
							generaMosseFuori(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8, PEDINA_BIANCA));
				}
				
				if (listaMosse.size() == 0) {

					int i = listaPedineBianche[0] / 8;
					int j = listaPedineBianche[0] % 8;
					listaMosse.add(new Mossa(i, j, i, j, 0));
				}
			}
			moves = listaMosse;
//			for(Mossa m: listaMosse)
//				System.out.println("getALLB"+m);
			return listaMosse;
		} else {
			for (int i = 0; i < numeroStackGiocatore[PEDINA_NERA]; i++) {
				listaMosse.addAll(generaListaMosse(listaPedineNere[i] / 8, listaPedineNere[i] % 8, PEDINA_NERA));
			}
			if (listaMosse.size() == 0) {
				for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
					listaMosse.addAll(generaMosseFuori(listaPedineNere[i] / 8, listaPedineNere[i] % 8, PEDINA_NERA));
				}
				
				if (listaMosse.size() == 0) {
					int i = listaPedineNere[0] / 8;
					int j = listaPedineNere[0] % 8;
					listaMosse.add(new Mossa(i, j, i, j, 0));
				}
				
			}
			moves = listaMosse;
//			for(Mossa m: listaMosse)
//				System.out.println("getALLn"+m);
			return listaMosse;
		}
	}

	public boolean getTurnoGiocatore() {
		return turnoGiocatore;
	}

	public void setTurnoGiocatore(boolean turnoGiocatore) {
		this.turnoGiocatore = turnoGiocatore;
	}

	public boolean checkMosseInAvanti(Mossa m, int c) {
		// Valido sia per BASE che MERGE ossia solo mosse in avanti

		if (c == PEDINA_BIANCA
				&& (m.getDirection() != NORTH && m.getDirection() != NORTHEAST && m.getDirection() != NORTHWEST))
			return false;
		else if (c == PEDINA_NERA
				&& (m.getDirection() != SOUTH && m.getDirection() != SOUTHEAST && m.getDirection() != SOUTHWEST))
			return false;
		return true;
	}

	public int getNumeroPedine(int x, int y) {
		return scacchiera.getValue(x * 8 + y);
	}

	public boolean checkMosse(Mossa m, int c) {
		int x = m.getiStart();
		int y = m.getjStart();
		int xF = m.getiEnd();
		int yF = m.getjEnd();
		int spostamento = calcolaSpostamento(x, y, xF, yF);
		int dir = calcolaDirezione(x, y, xF, yF);
		int spostamentoFuori = Mossa.calcolaCelleFuori(x, y, xF, yF, dir);

//		if(!turnoGiocatore) {
//			System.out.println("(!turnoGiocatore && c == PEDINA_BIANCA)"+(!turnoGiocatore && c == PEDINA_BIANCA));
//			System.out.println("scacchiera.getNumeroPedine(x, y) < spostamento:"+ (scacchiera.getNumeroPedine(x, y) < spostamento));
//		}
		if (c != getColorePedina(x, y))
			return false;

		if (turnoGiocatore && c != PEDINA_BIANCA) {
			return false;
		}

		if (!turnoGiocatore && c != PEDINA_NERA) {
			return false;
		}
		// TODO: controllo mosse fuori

		if (checkPosOut(xF, yF) && scacchiera.getNumeroPedine(x, y) >= spostamentoFuori) {
			return true;
		} else if (checkPosOut(xF, yF)) {
			return false;
		}

		// se lo spostamento richiede un numero di pedine maggiore di quello disponibile
		if (scacchiera.getNumeroPedine(x, y) < spostamento) {
			return false;
		}

		// mossa indietro e uguale a 0 ==> non può mangiare
		if (!checkMosseInAvanti(m, c) && ((scacchiera.getValue(xF * 8 + yF) == 0) || (getColorePedina(xF, yF) == c))) {
//			System.out.println("d");
			return false;
		}

		// CAPTURE
		if (scacchiera.getNumeroPedine(x, y) < scacchiera.getNumeroPedine(xF, yF)) {
//			System.out.println("e");
			return false;
		}

		if (scacchiera.getNumeroPedine(xF, yF) > spostamento) {

//			System.out.println("f");
			return false;
		}

		// P.s. Vengono valutate solo le mosse valide interne alla scacchiera.
		return true;
	}

	public int[] getMinimo(int[] v, int x, int y) {
		int min = 10;
		int i;
		int[] ret = new int[2];
		int c = getColorePedina(x, y);
//		System.out.println(c);
		if (c == PEDINA_BIANCA) {
			for (i = 0; i < posInteressantiBianchi.length; i++) {
				if (v[posInteressantiBianchi[i]] < min) {
					if (posInteressantiBianchi[i] == 0) {
						v[posInteressantiBianchi[i]] = x;
					}
					ret[0] = posInteressantiBianchi[i]; // direzione
					ret[1] = v[posInteressantiBianchi[i]]; // elemento

					min = v[posInteressantiBianchi[i]];

				}
			}
		} else {
			for (i = 0; i < posInteressantiNero.length; i++) {
				if (v[posInteressantiNero[i]] < min) {
					if (posInteressantiNero[i] == 1) {
						v[posInteressantiNero[i]] = 7 - x;
					}
					ret[0] = posInteressantiNero[i];
					ret[1] = v[posInteressantiNero[i]];
					min = v[posInteressantiNero[i]];
				}
			}
		}

		return ret;
	}

	public void calcolaMassimoSpostamento(int[] v, int x, int y) {
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
		//System.out.println("\n Pedine nella posizione " + x + "," + y + " " + scacchiera.getValue(pos) + "\n");
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

	public int getNumberPedinaMetaScacchiera(int giocatore) {
		int nPedineMetaCampoB = 0;
		int nPedineMetaCampoN = 0;
		for (int i = 0; i < numeroStackGiocatore[giocatore]; i++) {
			if (giocatore == 0) {
				if (listaPedineBianche[i] < 8 * 4) {
					nPedineMetaCampoB++;
				}
			} else {
				if (listaPedineNere[i] > 8 * 4) {
					nPedineMetaCampoN++;
				}
			}
		}
		if (giocatore == 0)
			return nPedineMetaCampoB;
		else
			return nPedineMetaCampoN;
	}

	/**
	 * Funzione utile per debug, stampa la scacchiera. Oss. Sia le pedine bianche
	 * che le nere si trovano su caselle di colore nero.
	 */
	public void stampaScacchiera() {
//		scacchiera.printValues();
//		private int scacchieraBianchi, scacchieraNeri; //
//		System.out.println("bit bianchi: " + scacchieraBianchi);
//		System.out.println("bit neri: " + scacchieraNeri);
//		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
//		scacchiera.printValues();
		for (r = 0; r < SIZE; r++) {
			for (c = 0; c < SIZE; c++) {
				System.out.print(" - ");
			}
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
//		System.out.print("0,3" + "numero" + scacchiera.getNumeroPedine(0, 3) + " colore" + getColorePedina(0, 3));
//		System.out.println("");
//		System.out.print("7,4" + "numero" + scacchiera.getNumeroPedine(7, 4) + " colore" + getColorePedina(7, 4));
//		System.out.println("");
	}

}