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
	private int mosseMaxBianco;
	private int mosseMaxNero;

	public static final int SIZE = 8;
	private static final int[] posInteressantiBianchi = { 0, 5, 2 };
	private static final int[] posInteressantiNero = { 3, 1, 4 };
	private static HashMap<String, Integer> riga; //
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
	private static int[] OUT_DIRECTIONS = { -2, 0, 2, 0, -1, 1, 1, -1, 1, 1, -1, -1, 0, 2, 0, -2 };

	public void debugStatus(boolean stampaMosse, String nome) {
//		private ByteMap scacchiera; //
//		private boolean turnoGiocatore; //
//		private int scacchieraBianchi, scacchieraNeri; //
//		private int[] numeroStackGiocatore; //
//		private byte[] listaPedineBianche; //
//		private byte[] listaPedineNere; //
//		private int[] MAX_SPOSTAMENTO; // Per ogni direzione -> max_spost in quella direzione
//		private ArrayList<Mossa> moves; // lista delle mosse generate
//		private int mosseMaxBianco;
//		private int mosseMaxNero;
		System.out.println("======================DEBUG INFO: " + nome + "=========================");
		scacchiera.printValues();
		System.out.println("TurnoGiocatore: " + turnoGiocatore);
		System.out.println("ScacchieraBianchi: " + (Integer.toBinaryString(scacchieraBianchi)));
		System.out.println("ScacchieraNeri: " + (Integer.toBinaryString(scacchieraNeri)));
		System.out.format("# stack bianco: %d \n# stack nero: %d \n", numeroStackGiocatore[PEDINA_BIANCA],
				numeroStackGiocatore[PEDINA_NERA]);
		
		System.out.print("Pedine del giocatore bianco: [");
		for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
			System.out.print(listaPedineBianche[i]);
			if (i < numeroStackGiocatore[PEDINA_BIANCA] - 1)
				System.out.print(", ");
		}
		System.out.println("]");
		System.out.print("Pedine del giocatore nero: [");
		for (int i = 0; i < numeroStackGiocatore[PEDINA_NERA]; i++) {
			System.out.print(listaPedineNere[i]);
			if (i < numeroStackGiocatore[PEDINA_NERA] - 1)
				System.out.print(", ");
		}
		System.out.println("]");
		System.out.println("MosseMaxBianco: " + mosseMaxBianco);
		System.out.println("MosseMaxNero: " + mosseMaxNero);
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

	public int getNumeroStackGiocatore(int colore) {
		return numeroStackGiocatore[colore];
	}

	private int[] calcola_indici(String posizione) {
		int[] res = new int[2];
		res[0] = riga.get(posizione.charAt(0) + "");// get da il valore della chiave che in questo caso è la lettera
		res[1] = Integer.parseInt(posizione.substring(1)) - 1;
		return res;
	}

	public void posizionaPedine(int i, int j, int qty, int color) {
		int a = i;
		int b = j / 2;
		int indiceVettore = i * 4 + j / 2;
		int indiceVettoreEsteso = i * 8 + j;
		if (color == PEDINA_BIANCA) {
			scacchieraBianchi = modifyBit(1, indiceVettore, scacchieraBianchi);
			scacchiera.setValue(qty, indiceVettoreEsteso);
			listaPedineBianche[numeroStackGiocatore[color]] = (new Integer(indiceVettoreEsteso)).byteValue();
		} else {
			scacchieraNeri = modifyBit(1, indiceVettore, scacchieraNeri);
			scacchiera.setValue(qty, indiceVettoreEsteso);
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

	private int calcolaCelleFuori(int a, int b, int x, int y) {
		int dir = calcolaDirezione(a, b, x, y);
		if (dir == NORTH)
			return a + Math.abs(x);
		if (dir == NORTHEAST)
			return (y - b);
		if (dir == NORTHWEST)
			return (b + Math.abs(y));
		if (dir == SOUTH)
			return x - a;
		if (dir == SOUTHEAST)
			return (y - b);
		if (dir == SOUTHWEST) 
			
			return (b - Math.abs(y) );
		return -1;
	}

	public static ScacchieraBit muovi(Mossa m, ScacchieraBit confI) {
//		confI.debugStatus(false,"xx");
		ScacchieraBit confF = new ScacchieraBit(confI);
//		confF.debugStatus(false,"xy");
		if (confF.turnoGiocatore)
			confF.mosseMaxBianco--;
		else
			confF.mosseMaxNero--;

		confF.muovi(m);

		return confF;
	}

	public int muovi(Mossa m) {
		int tipo = 0;
		// PRE-CONDIZIONE: m e' una mossa ammissibile.
		int x = m.getiStart();
		int y = m.getjStart(), c = getColorePedina(x, y);
		int xF = m.getiEnd();
		int yF = m.getjEnd();
		int oldPositionOnBoard = x * 8 + y;
		int newPositionOnBoard = xF * 8 + yF;
		int nPedineOld = scacchiera.getValue(oldPositionOnBoard);
		int nPedineNew;
		int spostamento;
		byte[] listaPedine = c == PEDINA_BIANCA ? listaPedineBianche : listaPedineNere;
		System.out.println("\n");
		System.out.println(m);
		System.out.format("colore:%d, Numero stack giocatore bianco: %d\n", c, numeroStackGiocatore[PEDINA_BIANCA]);
		System.out.format("colore:%d, Numero stack giocatore bianco: %d\n", c, numeroStackGiocatore[PEDINA_NERA]);
//		System.out.format("mossa ricevuta in muovi, mossa: %s \n", m.toString());
		if (checkPosOut(xF, yF)) {
			// TODO: GESTIRE PEDINE FUORI DOPO AVER SCELTO FORMATO MOSSA PER MOSSE FUORI

			int pedineDaEliminare = calcolaCelleFuori(x, y, xF, yF);
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
			} else if (c != cF) { // CAPTURE
				tipo = 2;
				// se le sposta tutte
				if (spostamento == nPedineOld) {
					for (int l = 0; l < 12; l++) {
						if (c == PEDINA_BIANCA) {
							if (listaPedineBianche[l] == oldPositionOnBoard) {
								listaPedineBianche[l] = (byte) newPositionOnBoard;
								
							} else if (listaPedineNere[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];

								}

							}
						} else {
							if (listaPedineNere[l] == oldPositionOnBoard) {
								listaPedineNere[l] = (byte) newPositionOnBoard;
								
							} else if (listaPedineBianche[l] == oldPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineBianche[k - 1] = listaPedineBianche[k];

								}

							}

						}
					}
					if (c == PEDINA_BIANCA) {
						numeroStackGiocatore[PEDINA_NERA]--;
						scacchieraNeri = modifyBit(0, xF * 4 + yF / 2, scacchieraNeri);
						scacchieraBianchi = modifyBit(0, x * 4 + y / 2, scacchieraBianchi);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF / 2, scacchieraBianchi);
					} else {
						numeroStackGiocatore[PEDINA_BIANCA]--;
						scacchieraNeri = modifyBit(0, x * 4 + y / 2, scacchieraNeri);
						scacchieraBianchi = modifyBit(0, xF * 4 + yF / 2, scacchieraBianchi);
						scacchieraNeri = modifyBit(1, xF * 4 + yF / 2, scacchieraNeri);

					}
				} else {// se non le sposta tutte

					if (c == PEDINA_BIANCA) {
						scacchieraNeri = modifyBit(0, xF * 4 + yF / 2, scacchieraNeri);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF / 2, scacchieraBianchi);
					} else {
						scacchieraBianchi = modifyBit(0, xF * 4 + yF / 2, scacchieraBianchi);
						scacchieraNeri = modifyBit(1, xF * 4 + yF / 2, scacchieraNeri);
					}

					for (int l = 0; l < numeroStackGiocatore[c]; l++) {
						if (listaPedine[l] == newPositionOnBoard) {
							for (int k = l + 1; k < numeroStackGiocatore[c]; k++) {
								listaPedine[k - 1] = listaPedine[k];
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
		System.out.println("<----dopo--->");
		
		for(int i=0; i<numeroStackGiocatore[c];i++)
		{
			System.out.println(listaPedine[i]/8 +" "+ listaPedine[i]%8);
		}
		System.out.println("");
		System.out.format("Numero stack giocatore bianco: %d\n", numeroStackGiocatore[PEDINA_BIANCA]);
		System.out.format("Numero stack giocatore nero: %d\n", numeroStackGiocatore[PEDINA_NERA]);
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
			if (esisteMossaFuori(listaPedine[i] / 8, listaPedine[i] % 8)) {
				System.out.println("mah");
				return false;
			}
			if(generaMosse(listaPedine[i] / 8, listaPedine[i] % 8)) {
				System.out.println("boh");
				return false;
			}
				
		}
		return true;
	}

	//TODO checkwin
	
	public boolean checkFin() {
		// TODO caso in cui non può più cacciare fuori ma ha ancora pedine
		if (mosseMaxBianco == 0 || mosseMaxNero == 0) {
			 System.out.println("1");return true;
		}
		if ((numeroStackGiocatore[PEDINA_BIANCA] == 0 || numeroStackGiocatore[PEDINA_NERA] == 0) || 
				(zeroMosse(PEDINA_BIANCA) && zeroMosse(PEDINA_NERA))) {
			System.out.println("2"); return true;
		}
		
		return false;
	}

	public boolean generaMosse(int x, int y) {
		int pos, curr_pos, numeroCelleSpostamento = 0;
		if (checkPosOut(x, y))
			throw new RuntimeException("Indici non consentiti");
		if (scacchiera.getIndex(x, y) == 0)
			throw new RuntimeException("Nessuna pedina disponibile");
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);
		for (int dir = 0; dir < 8; dir++) {
			pos = x * 8 + y;
			numeroCelleSpostamento = 0;
			curr_pos = pos;
			while (numeroCelleSpostamento++ < MAX_SPOSTAMENTO[dir] && curr_pos > 0 && curr_pos < 64) {
				curr_pos += DIRECTIONS[dir];
				Mossa mossa = new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir);
				
				if (checkMosse(mossa)) {
					return true;
				}

			}
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

	public ArrayList<Mossa> generaListaMosse(int x, int y) {
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		int pos, curr_pos, numeroCelleSpostamento = 0;
//		System.out.println("x: " + x + " y: " + y);
		if (checkPosOut(x, y))
			throw new RuntimeException("Indici non consentiti");
		if (scacchiera.getIndex(x, y) == 0) {
			throw new RuntimeException("Nessuna pedina disponibile");
		}
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);
		for (int dir = 0; dir < 8; dir++) {
			pos = x * 8 + y;
			numeroCelleSpostamento = 0;
			curr_pos = pos;
			while (numeroCelleSpostamento++ < MAX_SPOSTAMENTO[dir] && curr_pos > 0 && curr_pos < 64) {
				curr_pos += DIRECTIONS[dir];
				Mossa mossa = new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir);
				if (checkMosse(mossa)) {
					// System.out.println("dir: " + dir);
					listaMosse.add(mossa);
				}
			}
		}
		listaMosse.addAll(generaMosseFuori(x, y));
		// TODO va aggiustato

		return listaMosse;
	}

	public boolean miMangia(int x, int y, int color) {
		int n = scacchiera.getNumeroPedine(x, y);
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < numeroStackGiocatore[color]; i++) {
				listaMosse = generaListaMosse(listaPedineNere[i] / 8, listaPedineNere[i] % 8);
				nPedStack = scacchiera.getNumeroPedine(listaPedineNere[i] / 8, listaPedineNere[i] % 8);

				for (Mossa mossa : listaMosse) {
					if ((mossa.getiStart() == x || mossa.getjStart() == y) && nPedStack >= n)
						return true;
				}
			}
		} else if (color == PEDINA_NERA) {
			int nPedStack = 0;
			for (int i = 0; i < numeroStackGiocatore[color]; i++) {
				listaMosse = generaListaMosse(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8);
				nPedStack = scacchiera.getNumeroPedine(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8);

				for (Mossa mossa : listaMosse) {
					if ((mossa.getiStart() == x || mossa.getjStart() == y) && nPedStack >= n)
						return true;
				}
			}
		}

		return false;
	}

	public ArrayList<Mossa> generaMosseFuori(int x, int y) {
		int numeroCelleSpostamento = 0;
		ArrayList<Mossa> listaMosse = new ArrayList<>();
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);

		int[] v = getMinimo(MAX_SPOSTAMENTO, x, y);
		
	
		//System.out.println("dir" +v[0]+" "+ v[1]);
//		System.out.println("minimo" + v[0] + " " + v[1]);
//		int curr_pos = (x * 8 + y) + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0]];
		int curr_x = x + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2];
		int curr_y = y + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2 + 1];
		while (numeroCelleSpostamento++ < 12) {
			curr_x += OUT_DIRECTIONS[v[0] * 2];
			curr_y += OUT_DIRECTIONS[v[0] * 2 + 1];
			Mossa m = new Mossa(x, y, curr_x, curr_y, v[0]);
			if (checkMosse(m)) {
				
				listaMosse.add(m);
			}

		}
		
		return listaMosse;
	}

	public boolean esisteMossaFuori(int x, int y) {
		int numeroCelleSpostamento = 0;
		calcolaMassimoSpostamento(MAX_SPOSTAMENTO, x, y);

		int[] v = getMinimo(MAX_SPOSTAMENTO, x, y);
		
		System.out.println(" ");
		System.out.println("dir" +v[0]+" "+ v[1]);
//		System.out.println("minimo" + v[0] + " " + v[1]);
//		int curr_pos = (x * 8 + y) + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0]];
		int curr_x = x + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2];
		int curr_y = y + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2 + 1];
		while (numeroCelleSpostamento++ < 12) {
			curr_x += OUT_DIRECTIONS[v[0] * 2];
			curr_y += OUT_DIRECTIONS[v[0] * 2 + 1];
			Mossa m = new Mossa(x, y, curr_x, curr_y, v[0]);
			
			if (checkMosse(m)) {
				return true;
			}

		}
		System.out.format("Fine generazione per %d %d\n",x,y);
		return false;

	}

	public ArrayList<Mossa> getAllMoves() {
		ArrayList<Mossa> listaMosse = new ArrayList<>();
		if (turnoGiocatore) {
			for (int i = 0; i < numeroStackGiocatore[PEDINA_BIANCA]; i++) {
				listaMosse.addAll(generaListaMosse(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8));
			}
			moves = listaMosse;
			if (moves.size() == 0)
				turnoGiocatore = !turnoGiocatore;
			return listaMosse;
		} else {
			for (int i = 0; i < numeroStackGiocatore[PEDINA_NERA]; i++) {
				listaMosse.addAll(generaListaMosse(listaPedineNere[i] / 8, listaPedineNere[i] % 8));
			}
			moves = listaMosse;
			if (moves.size() == 0)
				turnoGiocatore = !turnoGiocatore;
			return listaMosse;
		}
	}

	public boolean getTurnoGiocatore() {
		return turnoGiocatore;
	}

	public void setTurnoGiocatore(boolean turnoGiocatore) {
		this.turnoGiocatore = turnoGiocatore;
	}

	public boolean checkMosseInAvanti(Mossa m, int x, int y) {
		// Valido sia per BASE che MERGE ossia solo mosse in avanti
		int c = getColorePedina(x, y);
		if (c == PEDINA_BIANCA
				&& (m.getDirection() != NORTH && m.getDirection() != NORTHEAST && m.getDirection() != NORTHWEST))
			return false;
		else if (c == PEDINA_NERA
				&& (m.getDirection() != SOUTH && m.getDirection() != SOUTHEAST && m.getDirection() != SOUTHWEST))
			return false;
		return true;
	}

	public boolean checkMosseIndietro(Mossa m, int x, int y) {
		// Valido sia per BASE che MERGE ossia solo mosse in avanti
		int c = getColorePedina(x, y);
		if (c == PEDINA_NERA
				&& (m.getDirection() != NORTH && m.getDirection() != NORTHEAST && m.getDirection() != NORTHWEST))
			return false;
		else if (c == PEDINA_BIANCA
				&& (m.getDirection() != SOUTH && m.getDirection() != SOUTHEAST && m.getDirection() != SOUTHWEST))
			return false;
		return true;
	}

	public int getNumeroPedine(int x, int y) {
		return scacchiera.getValue(x * 8 + y);
	}

	public boolean checkMosse(Mossa m) {
		int x = m.getiStart();
		int y = m.getjStart();
		int xF = m.getiEnd();
		int yF = m.getjEnd();
		int c = getColorePedina(x, y);
		int spostamento = calcolaSpostamento(x, y, xF, yF);
		int spostamentoFuori = calcolaCelleFuori(x, y, xF, yF);
		
		
//		if(!turnoGiocatore) {
//			System.out.println("(!turnoGiocatore && c == PEDINA_BIANCA)"+(!turnoGiocatore && c == PEDINA_BIANCA));
//			System.out.println("scacchiera.getNumeroPedine(x, y) < spostamento:"+ (scacchiera.getNumeroPedine(x, y) < spostamento));
//		}

		if (turnoGiocatore && c != PEDINA_BIANCA)
			return false;

		if (!turnoGiocatore && c != PEDINA_NERA)
			return false;
		// TODO: controllo mosse fuori
		
		if(checkPosOut(xF, yF) & scacchiera.getNumeroPedine(x, y) < spostamentoFuori) {
			
			return false;
		}
		
		// se lo spostamento richiede un numero di pedine maggiore di quello disponibile
		if (scacchiera.getNumeroPedine(x, y) < spostamento)
			return false;

		if (checkPosOut(m.getiEnd(), m.getjEnd()))
			return true;

		// mossa indietro e uguale a 0 ==> non può mangiare
		if (!checkMosseInAvanti(m, x, y) && ((scacchiera.getValue(xF * 8 + yF) == 0) || (getColorePedina(xF, yF) == c)))
			return false;

		// CAPTURE
		if (scacchiera.getNumeroPedine(x, y) < scacchiera.getNumeroPedine(xF, yF))
			return false;

		// P.s. Vengono valutate solo le mosse valide interne alla scacchiera.
		return true;
	}

	public int[] getMinimo(int[] v, int x, int y) {
		int min = 10, i;
		int[] ret = new int[2];
		int c = getColorePedina(x, y);
//		System.out.println(c);
		if (c == PEDINA_BIANCA) {
			for (i = 0; i < posInteressantiBianchi.length; i++) {
				if (v[posInteressantiBianchi[i]] < min) {
					ret[0] = posInteressantiBianchi[i]; // direzione
					ret[1] = v[posInteressantiBianchi[i]]; // elemento
					min = v[posInteressantiBianchi[i]];
				}
			}
		} else {
			for (i = 0; i < posInteressantiNero.length; i++) {
				if (v[posInteressantiNero[i]] < min) {
					ret[0] = posInteressantiNero[i];
					ret[1] = v[posInteressantiNero[i]];
					min = v[posInteressantiNero[i]];
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
		scacchiera.printValues();

		System.out.println();
//		private int scacchieraBianchi, scacchieraNeri; //
//		System.out.println("bit bianchi: " + scacchieraBianchi);
//		System.out.println("bit neri: " + scacchieraNeri);
		System.out.println("CONFIGURAZINE SCACCHIERA:");
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

//	public static void main(String[] args) {
//		ScacchieraBit scacchiera = new ScacchieraBit();
//
//		// scacchiera.stampaScacchiera();
//		scacchiera.generaMosse(7, 4);
////		
//		ArrayList<Mossa> m = scacchiera.getMoves();
//		int c = 0;
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println("Mosse disponibili: " + m.size());
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println();
//		for (Mossa mossa : m) {
//			System.out.println("mossa numero " + c + mossa);
//			c++;
//		}
//
//		scacchiera.muovi(m.get(0));
//		scacchiera.stampaScacchiera();
//
//		scacchiera.generaMosse(0, 3);
//		m = scacchiera.getMoves();
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println("Mosse disponibili: " + m.size());
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println();
//		c = 0;
//		for (Mossa mossa : m) {
//
//			System.out.println("mossa numero " + c + mossa);
//			c++;
//		}
//
//		scacchiera.muovi(m.get(10));
//
//		scacchiera.stampaScacchiera();
//
//		scacchiera.generaMosse(7, 4);
//		m = scacchiera.getMoves();
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println("Mosse disponibili: " + m.size());
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println();
//		c = 0;
//		for (Mossa mossa : m) {
//
//			System.out.println("mossa numero " + c + mossa);
//			c++;
//		}
//
//		System.out.println();
//		scacchiera.muovi(m.get(20));
//		scacchiera.stampaScacchiera();
//
//		scacchiera.generaMosse(0, 3);
//		m = scacchiera.getMoves();
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println("Mosse disponibili: " + m.size());
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println();
//		c = 0;
//		for (Mossa mossa : m) {
//
//			System.out.println("mossa numero " + c + mossa);
//			c++;
//		}
//
//		System.out.println();
//		scacchiera.muovi(m.get(30));
//		scacchiera.stampaScacchiera();
//		scacchiera.generaMosse(5, 4);
//		m = scacchiera.getMoves();
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println("Mosse disponibili: " + m.size());
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println();
//		c = 0;
//		for (Mossa mossa : m) {
//
//			System.out.println("mossa numero " + c + mossa);
//			c++;
//		}
//
//		System.out.println();
//		scacchiera.muovi(m.get(44));
//		scacchiera.stampaScacchiera();
//
//		scacchiera.generaMosse(2, 3);
//		m = scacchiera.getMoves();
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println("Mosse disponibili: " + m.size());
//		System.out.println("___________________");
//		System.out.println("");
//		System.out.println();
//		c = 0;
//		for (Mossa mossa : m) {
//
//			System.out.println("mossa numero " + c + mossa);
//			c++;
//		}
//
//		System.out.println();
//		scacchiera.muovi(m.get(58));
//		scacchiera.stampaScacchiera();
//	}
}
