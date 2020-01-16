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
		return -1;
	}

	public void annullaMossa(Mossa m) {
//		TODO
	}

	public static ScacchieraBit muovi(Mossa m, ScacchieraBit confI) {
		ScacchieraBit confF = new ScacchieraBit(confI);

		if (confF.turnoGiocatore)
			confF.mosseMaxBianco--;
		else
			confF.mosseMaxNero--;
		confF.muovi(m);

		return confF;
	}

	public void annullaMossa(Mossa m, int tipo) {
		int x = m.getiStart();
		int y = m.getjStart(), c = getColorePedina(x, y);
		int xF = m.getiEnd();
		int yF = m.getjEnd(), cF = getColorePedina(xF, yF);
		int iPositionOnBoard = x * 8 + y;// 10
		int fPositionOnBoard = xF * 8 + yF;// 2
		int nPedineF = scacchiera.getValue(fPositionOnBoard);// 2
		int nPedineI;
		int spostamento;
		// Cella di destinazione all'interno della scacchiera
		nPedineI = scacchiera.getValue(iPositionOnBoard); // 10
		spostamento = calcolaSpostamento(x, y, xF, yF); // 2
		if (tipo == 0) { // BASE
			// se le ha spostate tutte
			if (nPedineI == 0) {// se nella i non ci sono pedine vuol dire che le ha spost tutte
				for (int l = 0; l < 12; l++) {
					if (c == PEDINA_BIANCA) {
						if (listaPedineBianche[l] == fPositionOnBoard) {
							listaPedineBianche[l] = (byte) iPositionOnBoard;
							break;
						}
					} else {
						if (listaPedineNere[l] == fPositionOnBoard) {
							listaPedineNere[l] = (byte) iPositionOnBoard;
							break;
						}
					}
				}
				if (c == PEDINA_BIANCA) {
					scacchieraBianchi = modifyBit(1, x * 4 + y, scacchieraBianchi);
					scacchieraBianchi = modifyBit(0, xF * 4 + yF, scacchieraBianchi);
				} else {
					scacchieraNeri = modifyBit(1, x * 4 + y, scacchieraNeri);
					scacchieraNeri = modifyBit(0, xF * 4 + yF, scacchieraNeri);
				}
			} else {
				for (int l = 0; l < 12; l++) {
					// se ne sposta aveva spost una parte una parte
					if (c == PEDINA_BIANCA) {
						if (listaPedineBianche[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineBianche[k - 1] = listaPedineBianche[k];
								numeroStackGiocatore[PEDINA_BIANCA]--;
							}
							break;

						}
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) iPositionOnBoard;
					} else {

						if (listaPedineNere[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineNere[k - 1] = listaPedineNere[k];
								numeroStackGiocatore[PEDINA_NERA]--;
							}
							break;
						}
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) iPositionOnBoard;

					}
				}
				if (c == PEDINA_BIANCA) {
					scacchieraNeri = modifyBit(0, xF * 4 + yF, scacchieraNeri);
				} else {
					scacchieraBianchi = modifyBit(0, xF * 4 + yF, scacchieraBianchi);
				}
			}
			scacchiera.setValue(nPedineI + spostamento, iPositionOnBoard);
			scacchiera.setValue(0, fPositionOnBoard);
		} else if (tipo == 1) {
			// MERGE
			// se le sposta tutte
			if (nPedineI == 0) {
				for (int l = 0; l < 12; l++) {
					if (c == PEDINA_BIANCA) {
						if (listaPedineBianche[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineBianche[k - 1] = listaPedineBianche[k];
							}
						}
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) iPositionOnBoard;
					} else {
						if (listaPedineNere[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineNere[k - 1] = listaPedineNere[k];
							}
						}
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) iPositionOnBoard;

					}

				}
				if (c == PEDINA_BIANCA)
					scacchieraBianchi = modifyBit(1, x * 4 + y, scacchieraBianchi);
				else
					scacchieraNeri = modifyBit(1, x * 4 + y, scacchieraNeri);
			}
			scacchiera.setValue(nPedineI + spostamento, iPositionOnBoard);
			scacchiera.setValue(nPedineF - spostamento, fPositionOnBoard);
		} else { // CAPTURE

			// se le sposta tutte
			if (nPedineI == 0) {
				for (int l = 0; l < 12; l++) {
					if (c == PEDINA_BIANCA) {
						if (listaPedineBianche[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineBianche[k - 1] = listaPedineBianche[k];
							}
						}
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) iPositionOnBoard;
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) fPositionOnBoard;
					} else {
						if (listaPedineNere[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineNere[k - 1] = listaPedineNere[k];
							}
						}
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) fPositionOnBoard;
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) iPositionOnBoard;

					}
				}
				if (c == PEDINA_BIANCA) {
					scacchieraNeri = modifyBit(1, xF * 4 + yF, scacchieraNeri);
					scacchieraBianchi = modifyBit(1, x * 4 + y, scacchieraBianchi);
					scacchieraBianchi = modifyBit(0, xF * 4 + yF, scacchieraBianchi);
				} else {
					scacchieraNeri = modifyBit(1, x * 4 + y, scacchieraNeri);
					scacchieraBianchi = modifyBit(1, xF * 4 + yF, scacchieraBianchi);
					scacchieraNeri = modifyBit(0, xF * 4 + yF, scacchieraNeri);
				}
			} else {// se non le sposta tutte
				for (int l = 0; l < 12; l++) {
					if (c == PEDINA_BIANCA) {
						if (listaPedineBianche[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineBianche[k - 1] = listaPedineBianche[k];
							}
						}
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) iPositionOnBoard;
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) fPositionOnBoard;
					} else {
						if (listaPedineNere[l] == fPositionOnBoard) {
							for (int k = l + 1; k < 12; k++) {
								listaPedineNere[k - 1] = listaPedineNere[k];
							}
						}
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) iPositionOnBoard;
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) fPositionOnBoard;

					}
				}
				if (c == PEDINA_BIANCA) {
					scacchieraNeri = modifyBit(1, xF * 4 + yF, scacchieraNeri);
					scacchieraBianchi = modifyBit(0, xF * 4 + yF, scacchieraBianchi);
				} else {
					scacchieraBianchi = modifyBit(1, xF * 4 + yF, scacchieraBianchi);
					scacchieraNeri = modifyBit(0, xF * 4 + yF, scacchieraNeri);
				}
			}
		}
		scacchiera.setValue(nPedineI + spostamento, iPositionOnBoard);
		scacchiera.setValue(nPedineF - spostamento, fPositionOnBoard);

	}

	public int muovi(Mossa m) {
		int tipo = 0;
		// PRE-CONDIZIONE: m ? una mossa ammissibile.
		int x = m.getiStart();
		int y = m.getjStart(), c = getColorePedina(x, y);
		int xF = m.getiEnd();
		int yF = m.getjEnd(), cF = getColorePedina(xF, yF);
		int oldPositionOnBoard = x * 8 + y;
		int newPositionOnBoard = xF * 8 + yF;
		int nPedineOld = scacchiera.getValue(oldPositionOnBoard);
		int nPedineNew;
		int spostamento;
		System.out.format("mossa ricevuta in muovi, mossa: %s \n", m.toString());
		if (checkPosOut(xF, yF)) {
			// TODO: GESTIRE PEDINE FUORI DOPO AVER SCELTO FORMATO MOSSA PER MOSSE FUORI
			int pedineDaEliminare = calcolaCelleFuori(x, y, xF, yF);
			if (numeroStackGiocatore[PEDINA_BIANCA] < 12) {
				listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) newPositionOnBoard;
				// scacchiera.getValue(scacchiera.getIndex());
			} else {
				for (int l = 0; l < 12; l++) {
					if (listaPedineBianche[l] == oldPositionOnBoard) {

					}
				}
			}
		} else {
			// Cella di destinazione all'interno della scacchiera
			nPedineNew = scacchiera.getValue(newPositionOnBoard);
			spostamento = calcolaSpostamento(x, y, xF, yF);
			if (nPedineNew == 0) { // BASE
				tipo = 0;
				// se le sposta tutte
				if (spostamento == nPedineOld) {
					for (int l = 0; l < 12; l++) {
						if (c == PEDINA_BIANCA) {
							if (listaPedineBianche[l] == oldPositionOnBoard) {
								listaPedineBianche[l] = (byte) newPositionOnBoard;
								break;
							}
						} else {
							if (listaPedineNere[l] == oldPositionOnBoard) {
								listaPedineNere[l] = (byte) newPositionOnBoard;
								break;
							}
						}
					}
					if (c == PEDINA_BIANCA) {
						scacchieraBianchi = modifyBit(0, x * 4 + y, scacchieraBianchi);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF, scacchieraBianchi);
					} else {
						scacchieraNeri = modifyBit(0, x * 4 + y, scacchieraNeri);
						scacchieraNeri = modifyBit(1, xF * 4 + yF, scacchieraNeri);
					}
				} else {
					// se ne sposta una parte
					if (c == PEDINA_BIANCA)
						listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) newPositionOnBoard;
					else
						listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) newPositionOnBoard;

					if (c == PEDINA_BIANCA) {
						scacchieraNeri = modifyBit(1, xF * 4 + yF, scacchieraNeri);
					} else {
						scacchieraBianchi = modifyBit(1, xF * 4 + yF, scacchieraBianchi);
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
									numeroStackGiocatore[PEDINA_BIANCA]--;
								}
								break;
							}
						} else {

							if (listaPedineNere[l] == oldPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];
									numeroStackGiocatore[PEDINA_NERA]--;
								}
								break;
							}
						}
					}
					if (c == PEDINA_BIANCA)
						scacchieraBianchi = modifyBit(0, x * 4 + y, scacchieraBianchi);
					else
						scacchieraNeri = modifyBit(0, x * 4 + y, scacchieraNeri);
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
								break;
							} else if (listaPedineNere[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];
									numeroStackGiocatore[PEDINA_NERA]--;
								}
							}
						} else {
							if (listaPedineNere[l] == oldPositionOnBoard) {
								listaPedineNere[l] = (byte) newPositionOnBoard;
								break;
							} else if (listaPedineBianche[l] == oldPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineBianche[k - 1] = listaPedineBianche[k];
									numeroStackGiocatore[PEDINA_BIANCA]--;
								}
							}
						}
					}
					if (c == PEDINA_BIANCA) {
						scacchieraNeri = modifyBit(0, xF * 4 + yF, scacchieraNeri);
						scacchieraBianchi = modifyBit(0, x * 4 + y, scacchieraBianchi);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF, scacchieraBianchi);
					} else {
						scacchieraNeri = modifyBit(0, x * 4 + y, scacchieraNeri);
						scacchieraBianchi = modifyBit(0, xF * 4 + yF, scacchieraBianchi);
						scacchieraNeri = modifyBit(1, xF * 4 + yF, scacchieraNeri);
					}
				} else {// se non le sposta tutte
					for (int l = 0; l < 12; l++) {
						if (c == PEDINA_BIANCA) {
							if (listaPedineNere[l] == newPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineNere[k - 1] = listaPedineNere[k];
								}
							}
							listaPedineBianche[numeroStackGiocatore[PEDINA_BIANCA]++] = (byte) newPositionOnBoard;
						} else {
							if (listaPedineBianche[l] == oldPositionOnBoard) {
								for (int k = l + 1; k < 12; k++) {
									listaPedineBianche[k - 1] = listaPedineBianche[k];
								}
							}
							listaPedineNere[numeroStackGiocatore[PEDINA_NERA]++] = (byte) newPositionOnBoard;
						}
					}
					if (c == PEDINA_BIANCA) {
						scacchieraNeri = modifyBit(0, xF * 4 + yF, scacchieraNeri);
						scacchieraBianchi = modifyBit(1, xF * 4 + yF, scacchieraBianchi);
					} else {
						scacchieraBianchi = modifyBit(0, xF * 4 + yF, scacchieraBianchi);
						scacchieraNeri = modifyBit(1, xF * 4 + yF, scacchieraNeri);
					}
				}
			}
			scacchiera.setValue(nPedineOld - spostamento, oldPositionOnBoard);
			scacchiera.setValue(spostamento, newPositionOnBoard);

		}
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

	public boolean zeroPedineDaEliminare() {
		return false;
	}

	public boolean checkWin() {
		// TODO caso in cui non può più cacciare fuori ma ha ancora pedine
//		if (mosseMaxBianco == 0 || mosseMaxNero == 0)
//			return true;
		if (numeroStackGiocatore[0] == 0 || numeroStackGiocatore[1] == 0 ) {//|| zeroPedineDaEliminare()) {
			return true;
		}
		return false;
	}

	public void generaMosse(int x, int y) {
		int pos, curr_pos, numeroCelleSpostamento = 0;
//		System.out.println("x: " + x + " y: " + y);
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
//					System.out.println("dir: " + dir);
					moves.add(mossa);
				}

			}
		}
		//generaMosseFuori(x, y);
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
					//System.out.println("dir: " + dir);
				listaMosse.add(mossa);
				}
			}
		}
		return listaMosse;
	}

	public void generaMosseFuori(int x, int y) {
		int numeroCelleSpostamento = 0;
		int[] v = getMinimo(MAX_SPOSTAMENTO, x, y);

//		System.out.println("minimo" + v[0] + " " + v[1]);
//		int curr_pos = (x * 8 + y) + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0]];
		int curr_x = x + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2];
		int curr_y = y + MAX_SPOSTAMENTO[v[0]] * OUT_DIRECTIONS[v[0] * 2 + 1];
		while (numeroCelleSpostamento++ < 12) {
			curr_x += OUT_DIRECTIONS[v[0] * 2];
			curr_y += OUT_DIRECTIONS[v[0] * 2 + 1];
			Mossa m = new Mossa(x, y, curr_x, curr_y, v[0]);
			if (checkMosse(m)) {
				moves.add(m);
			}

		}
	}

	public ArrayList<Mossa> getAllMoves() {
		ArrayList<Mossa> listaMosse = new ArrayList<>();
		if (turnoGiocatore) {
			for (int i = 0; i < listaPedineBianche.length; i++) {
				listaMosse.addAll(generaListaMosse(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8));
			}
			return listaMosse;
		} else {
			for (int i = 0; i < listaPedineNere.length; i++) {
				listaMosse.addAll(generaListaMosse(listaPedineBianche[i] / 8, listaPedineBianche[i] % 8));
			}
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
				&& !(m.getDirection() != NORTH || m.getDirection() != NORTHEAST || m.getDirection() != NORTHWEST))
			return false;
		else if (c == PEDINA_NERA
				&& !(m.getDirection() != SOUTH || m.getDirection() != SOUTHEAST || m.getDirection() != SOUTHWEST))
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

		// TODO: controllo mosse fuori

		if (turnoGiocatore && c == PEDINA_NERA)
			return false;

		if (!turnoGiocatore && c == PEDINA_BIANCA)
			return false;

		// se lo spostamento richiede un numero di pedine maggiore di quello disponibile
		if (scacchiera.getNumeroPedine(x, y) < spostamento)
			return false;

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
		System.out.println();
//		private int scacchieraBianchi, scacchieraNeri; //
//		System.out.println("bit bianchi: " + scacchieraBianchi);
//		System.out.println("bit neri: " + scacchieraNeri);
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
//		scacchiera.printValues();
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
//		System.out.print("0,3" + "numero" + scacchiera.getNumeroPedine(0, 3) + " colore" + getColorePedina(0, 3));
//		System.out.println("");
//		System.out.print("7,4" + "numero" + scacchiera.getNumeroPedine(7, 4) + " colore" + getColorePedina(7, 4));
//		System.out.println("");
	}

	public static void main(String[] args) {
		ScacchieraBit scacchiera = new ScacchieraBit();
		scacchiera.stampaScacchiera();
		scacchiera.generaMosse(7, 4);
		ArrayList<Mossa> m = scacchiera.getMoves();

		System.out.println("___________________");
		System.out.println("");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println("___________________");
		System.out.println("");
		System.out.println();
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}

		
	}
}
