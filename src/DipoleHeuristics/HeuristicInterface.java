package DipoleHeuristics;

import java.util.ArrayList;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public interface HeuristicInterface {

	static final int PEDINA_BIANCA = 0;
	static final int PEDINA_NERA = 1;
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int NORTHEAST = 2;
	public static final int SOUTHWEST = 3;
	public static final int SOUTHEAST = 4;
	public static final int NORTHWEST = 5;
	public static final int EAST = 6;
	public static final int WEST = 7;
	public final int[] posInteressantiBianchi = { NORTHEAST, NORTHWEST, NORTH };
	public final int[] posInteressantiNero = { SOUTHWEST, SOUTHEAST, SOUTH };

	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec);

	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec, ScacchieraBit oldBoard);

	public default boolean campoNostro(Mossa m, int color) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		byte pos = (byte) (x * 8 + y);
		if (color == 1) {
			if (pos < 32) {
				return true;
			} else {
				return false;
			}
		} else {
			if (pos >= 32) {
				return true;
			}

			else {
				return false;
			}
		}
	}

	public default boolean siPuoCaricareAdv(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.checkPosOut(x, y))
			return false;

		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {

			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {

//					System.out.println("Mossa gen " + mossa.oldtoString());
					if (eSullaLinea(x, y, mossa.getiStart(), mossa.getjStart())) {
						return true;
					}
				}
			}
		} else if (color == PEDINA_NERA) {
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineBianche[i] / 8,
						board.listaPedineBianche[i] % 8, 1 - color, board);
				for (Mossa mossa : listaMosse) {

//					System.out.println("Mossa gen " + mossa.oldtoString());
					if (eSullaLinea(x, y, mossa.getiStart(), mossa.getjStart())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int calcolaSpostamento(int a, int b, int x, int y) {
		int k, m;
		k = Math.abs(a - x);
		m = Math.abs(b - y);
		return k >= m ? k : m;
	}

	public static int calcolaDirezione(int a, int b, int x, int y) {
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

	public static boolean eSullaLinea(int x, int y, int xadv, int yadv) {
		// ritorna la direzione se è sulla linea
		// se non è sulla linea ritorna -1
		//
		int spostamento = calcolaSpostamento(x, y, xadv, yadv);
		for (int dir = 0; dir < 8; dir++) {
			int a = x + spostamento * ScacchieraBit.OUT_DIRECTIONS[2 * dir];
			int b = y + spostamento * ScacchieraBit.OUT_DIRECTIONS[2 * dir + 1];
			if (a == xadv && b == yadv)
				return true;
		}
		return false;
	}

	public default boolean cacca(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.checkPosOut(x, y))
			return false;
		int n = board.calcolaSpostamento(m.getiStart(), m.getjStart(), x, y);
		if (board.getColorePedina(x, y) == (1 - color) && board.getNumeroPedine(x, y) <= n)
			if (miMangiaGetMossa(m, color, board) == null)
				return true;
		return false;
	}

	public default boolean possoMangiareENonMiMangia(Mossa m, int color, ScacchieraBit board,
			ArrayList<Mossa> listaMosseMiMangia) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.checkPosOut(x, y))
			return false;
		int n = board.calcolaSpostamento(m.getiStart(), m.getjStart(), x, y);
		if (board.getColorePedina(x, y) == (1 - color) && board.getNumeroPedine(x, y) <= n)
			if (listaMosseMiMangia == null || listaMosseMiMangia.size() == 0)
				return true;
		return false;
	}

	public default boolean possoMangiareEMiMangia(Mossa m, int color, ScacchieraBit board,
			ArrayList<Mossa> listaMosseMiMangia) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.checkPosOut(x, y))
			return false;
		int n = board.calcolaSpostamento(m.getiStart(), m.getjStart(), x, y);
		if (board.getColorePedina(x, y) == (1 - color) && board.getNumeroPedine(x, y) <= n)
			if (listaMosseMiMangia != null && listaMosseMiMangia.size() != 0)
				return true;
		return false;
	}

	public default boolean possoMangiare(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.checkPosOut(x, y))
			return false;
		int n = board.calcolaSpostamento(m.getiStart(), m.getjStart(), x, y);
		if (board.getColorePedina(x, y) == (1 - color) && board.getNumeroPedine(x, y) <= n)
			return true;
		return false;
	}

	public default int getNumeroPedineMangiateAdv(Mossa m, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		return board.getNumeroPedine(x, y);
	}

	public default int cornice(int x, int y, int giocatore, ScacchieraBit board) {
		int cont = 0;
		if (board.checkPosOut(x, y))
			return -1;

		if (!board.checkPosOut(x - 1, y - 1) & board.getColorePedina(x - 1, y - 1) == (1 - giocatore)
				& board.getNumeroPedine(x - 1, y - 1) > 1)
			cont++;
		if (!board.checkPosOut(x - 1, y + 1) & board.getColorePedina(x - 1, y + 1) == (1 - giocatore)
				& board.getNumeroPedine(x - 1, y + 1) > 1)
			cont++;
		if (!board.checkPosOut(x + 1, y + 1) & board.getColorePedina(x + 1, y + 1) == (1 - giocatore)
				& board.getNumeroPedine(x + 1, y + 1) > 1)
			cont++;
		if (!board.checkPosOut(x + 1, y - 1) & board.getColorePedina(x + 1, y - 1) == (1 - giocatore)
				& board.getNumeroPedine(x + 1, y - 1) > 1)
			cont++;
		return cont;
	}

	public default ArrayList<Mossa> miMangiaGetMossa(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.getColorePedina(m.getiStart(), m.getjStart()) != color)
			return null;
		if (board.checkPosOut(x, y))
			return null;
		int n = board.calcolaSpostamento(m.getiStart(), m.getjStart(), m.getiEnd(), m.getjEnd());
		ArrayList<Mossa> listaMosseReturn = new ArrayList<Mossa>();
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
//					System.out.println("Mossa gen " + mossa.oldtoString());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) && nPedStack >= n) {
//						System.out.println("x " + x + "y " + y + "n " + n + "nadv " + nPedStack);
//						System.out.println("Mossa che mangia " + mossa.oldtoString());
						listaMosseReturn.add(mossa);
					}
				}
			}
		} else if (color == PEDINA_NERA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineBianche[i] / 8,
						board.listaPedineBianche[i] % 8, 1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
						listaMosseReturn.add(mossa);
					}
				}
			}
		}
		return listaMosseReturn;
	}

	public default boolean miMangiaStackCurr(int x, int y, int color, ScacchieraBit s) {
		int n = s.getNumeroPedine(x, y);
		ArrayList<Mossa> listaMosse;
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < s.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(s.listaPedineNere[i] / 8, s.listaPedineNere[i] % 8, 1 - color,
						s);
				for (Mossa mossa : listaMosse) {
					nPedStack = s.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) && nPedStack >= n) {
						return true;
					}
				}
			}
		} else if (color == PEDINA_NERA) {
			int nPedStack = 0;
			for (int i = 0; i < s.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(s.listaPedineBianche[i] / 8, s.listaPedineBianche[i] % 8,
						1 - color, s);
				for (Mossa mossa : listaMosse) {
					nPedStack = s.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public default int win(ScacchieraBit board, int giocatore) {
		int stallo = board.stallo(1 - giocatore);
		// stallo=1 vuol dire che ha solo mosse fuori
		// stallo ==2 vuol dire che è bloccato
		// stallo=0 se non è in stallo
		if (board.getNumeroPedineTot(1 - giocatore) == 0 || stallo == 1 || stallo == 2
				|| board.getMosseMaxGiocatore(1 - giocatore) == 0)
			return stallo;

		return -1;

	}

	public default boolean miMangiaPochePedineEritornoAmangiarlo(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		if (board.checkPosOut(x, y))
			return false;
		int n = board.calcolaSpostamento(m.getiStart(), m.getjStart(), m.getiEnd(), m.getjEnd());
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (n <= 3) {
			if (color == PEDINA_BIANCA) {
				int nPedStack = 0;
				for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
					listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
							PEDINA_NERA, board);
					for (Mossa mossa : listaMosse) {
						nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
								mossa.getjEnd());
						if ((mossa.getiEnd() == x && mossa.getjEnd() == y) && nPedStack >= n) {
							if (miMangiaGetMossa(mossa, 1 - color, board) != null) {
								System.out.println("ANTONELLA DICE CHE FUNZIONI!!!");
								System.out.println(mossa.oldtoString());
								return true;
							}
						}
					}
				}
			} else if (color == PEDINA_NERA) {
				int nPedStack = 0;
				for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
					listaMosse = generaListaMosseAusiliario(board.listaPedineBianche[i] / 8,
							board.listaPedineBianche[i] % 8, PEDINA_BIANCA, board);
					for (Mossa mossa : listaMosse) {
						nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
								mossa.getjEnd());
						if ((mossa.getiEnd() == x && mossa.getjEnd() == y) && nPedStack >= n) {
							if (miMangiaGetMossa(mossa, 1 - color, board) != null) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public default boolean checkMosseIndietro(Mossa m, int c) {
		if (c == PEDINA_NERA
				&& (m.getDirection() != NORTH && m.getDirection() != NORTHEAST && m.getDirection() != NORTHWEST))
			return false;
		else if (c == PEDINA_BIANCA
				&& (m.getDirection() != SOUTH && m.getDirection() != SOUTHEAST && m.getDirection() != SOUTHWEST))
			return false;
		return true;
	}

	public default boolean esisteMossaIndietro(ArrayList<Mossa> l, int c) {
		if (l != null && l.size() != 0) {
			for (Mossa m : l) {
				if (c == PEDINA_NERA && (m.getDirection() != NORTH && m.getDirection() != NORTHEAST
						&& m.getDirection() != NORTHWEST))
					return false;
				else if (c == PEDINA_BIANCA && (m.getDirection() != SOUTH && m.getDirection() != SOUTHEAST
						&& m.getDirection() != SOUTHWEST))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public default int powerCell(Mossa m, int c, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		int cont = 0;
		if (board.checkPosOut(x, y))
			return 0;
		board.calcolaMassimoSpostamento(board.MAX_SPOSTAMENTO, x, y);
		for (int s : board.MAX_SPOSTAMENTO)
			cont = cont + Math.min(s, m.calcolaSpostamento(m.getiStart(), m.getjStart(), m.getiEnd(), m.getjEnd()));
		return cont;
	}

	public default boolean miMangiaStackRestante(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiStart();
		int y = m.getjStart();
		int stackRestante = board.getNumeroPedine(x, y);

		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= stackRestante) {
						return true;
					}
				}
			}
		} else if (color == PEDINA_NERA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineBianche[i] / 8,
						board.listaPedineBianche[i] % 8, 1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= stackRestante) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public default ArrayList<Mossa> generaListaMosseAusiliario(int x, int y, int c, ScacchieraBit board) {
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		int pos, curr_pos, numeroCelleSpostamento = 0;
		if (board.checkPosOut(x, y))
			throw new RuntimeException("Indici non consentiti");
		if (board.getScacchiera().getIndex(x, y) == 0) {
			return listaMosse;
		}
		board.calcolaMassimoSpostamento(board.MAX_SPOSTAMENTO, x, y);
		for (int dir = 0; dir < 8; dir++) {
			pos = x * 8 + y;
			numeroCelleSpostamento = 0;
			curr_pos = pos;

			while (numeroCelleSpostamento++ < board.MAX_SPOSTAMENTO[dir] && curr_pos > 0 && curr_pos < 64) {
				curr_pos += ScacchieraBit.DIRECTIONS[dir];
				Mossa mossa = new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir);
				if (checkMosseAusiliario(mossa, c, board)) {

					listaMosse.add(mossa);
				}
			}
		}
		return listaMosse;
	}

	public default boolean loMangio(Mossa adversary, ScacchieraBit board, int color) {
		int x = adversary.getiEnd();
		int y = adversary.getjEnd();
		if (board.checkPosOut(x, y))
			return false;
		int n = board.calcolaSpostamento(adversary.getiStart(), adversary.getjStart(), adversary.getiEnd(),
				adversary.getjEnd());
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
						return true;
					}
				}
			}
		} else if (color == PEDINA_NERA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineBianche[i] / 8,
						board.listaPedineBianche[i] % 8, 1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.calcolaSpostamento(mossa.getiStart(), mossa.getjStart(), mossa.getiEnd(),
							mossa.getjEnd());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public default boolean checkMosseAusiliario(Mossa m, int c, ScacchieraBit stato) {
		int x = m.getiStart();
		int y = m.getjStart();
		int xF = m.getiEnd();
		int yF = m.getjEnd();
		int spostamento = stato.calcolaSpostamento(x, y, xF, yF);
		int dir = stato.calcolaDirezione(x, y, xF, yF);
		int spostamentoFuori = Mossa.calcolaCelleFuori(x, y, xF, yF, dir);

		if (stato.checkPosOut(xF, yF) && stato.getScacchiera().getNumeroPedine(x, y) >= spostamentoFuori) {
			return true;
		} else if (stato.checkPosOut(xF, yF)) {
			return false;
		}
		if (stato.getScacchiera().getNumeroPedine(x, y) < spostamento) {
			return false;
		}
		if (!stato.checkMosseInAvanti(m, c)
				&& ((stato.getScacchiera().getValue(xF * 8 + yF) == 0) || (stato.getColorePedina(xF, yF) == c))) {
			return false;
		}
		if (stato.getScacchiera().getNumeroPedine(x, y) < stato.getScacchiera().getNumeroPedine(xF, yF)) {
			return false;
		}
		if (stato.getScacchiera().getNumeroPedine(xF, yF) > spostamento) {
			return false;
		}
		return true;
	}

}
