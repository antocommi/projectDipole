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

	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec);

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

	public default boolean possoMangiareENonMiMangia(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		int n = board.calcolaSpostamento(m.getiStart(), m.getiEnd(), m.getjStart(), m.getjEnd());
		if (board.getColorePedina(x, y) == (1 - color) && board.getNumeroPedine(x, y) <= n)
			if (miMangiaGetMossa(m, color, board) == null)
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
		if (board.getColorePedina(x - 1, y - 1) == (1 - giocatore) & board.getNumeroPedine(x - 1, y - 1) > 1)
			cont++;
		if (board.getColorePedina(x - 1, y + 1) == (1 - giocatore) & board.getNumeroPedine(x - 1, y + 1) > 1)
			cont++;
		if (board.getColorePedina(x + 1, y + 1) == (1 - giocatore) & board.getNumeroPedine(x + 1, y + 1) > 1)
			cont++;
		if (board.getColorePedina(x + 1, y - 1) == (1 - giocatore) & board.getNumeroPedine(x + 1, y - 1) > 1)
			cont++;
		return cont;
	}

	public default Mossa miMangiaGetMossa(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		int n = board.calcolaSpostamento(m.getiStart(), m.getiEnd(), m.getjStart(), m.getjEnd());
		ArrayList<Mossa> listaMosseReturn = new ArrayList<Mossa>();
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {

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
					nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {

						listaMosseReturn.add(mossa);
					}
				}
			}
		}

		if (listaMosseReturn.size() != 0) {
			if (listaMosseReturn.size() == 1) {
				return listaMosseReturn.get(0);
			} else {
				int max = 0;
				Mossa mR = null;
				for (Mossa move : listaMosseReturn) {
					if (board.getNumeroPedine(move.getiStart(), move.getjStart()) > max) {
						mR = move;
						max = board.getNumeroPedine(move.getiStart(), move.getjStart());
					}
				}
				return mR;
			}
		} else
			return null;

	}

	public default boolean miMangiaPochePedineEritornoAmangiarlo(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		int n = board.calcolaSpostamento(m.getiStart(), m.getiEnd(), m.getjStart(), m.getjEnd());
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (n <= 2) {
			if (color == PEDINA_BIANCA) {
				int nPedStack = 0;
				for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
					listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
							PEDINA_NERA, board);
					for (Mossa mossa : listaMosse) {
						nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
						if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
							if (miMangiaGetMossa(mossa, 1 - color, board) != null) {
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
						nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
						if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
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

	public default int generaMosseSenzaCheck(Mossa m, int c, ScacchieraBit board) {
		int x = m.getiEnd();
		int y = m.getjEnd();
		int cont = 0;
		int pos, curr_pos, numeroCelleSpostamento = 0;
		if (board.checkPosOut(x, y))
			return 0;
		board.calcolaMassimoSpostamento(board.MAX_SPOSTAMENTO, x, y);
		for (int dir = 0; dir < 8; dir++) {
			pos = x * 8 + y;
			numeroCelleSpostamento = 0;
			curr_pos = pos;
			while (numeroCelleSpostamento++ < board.MAX_SPOSTAMENTO[dir] && curr_pos > 0 && curr_pos < 64) {
				curr_pos += ScacchieraBit.DIRECTIONS[dir];
				Mossa mossa = new Mossa(x, y, curr_pos / 8, curr_pos % 8, dir);
				if (board.calcolaSpostamento(m.getiStart(), m.getjStart(), x, y) >= board.calcolaSpostamento(x, y,
						curr_pos / 8, curr_pos % 8) && board.checkMosseInAvanti(mossa, c))
					cont++;
			}
		}
		return cont;
	}

	public default boolean miMangiaStackRestante(Mossa m, int color, ScacchieraBit board) {
		int x = m.getiStart();
		int y = m.getjStart();
		int n = board.calcolaSpostamento(m.getiStart(), m.getiEnd(), m.getjStart(), m.getjEnd());
		int stackRestante = board.getNumeroPedine(x, y) - n;
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
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
					nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
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
		int n = board.calcolaSpostamento(adversary.getiStart(), adversary.getiEnd(), adversary.getjStart(),
				adversary.getjEnd());
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		if (color == PEDINA_BIANCA) {
			int nPedStack = 0;
			for (int i = 0; i < board.numeroStackGiocatore[1 - color]; i++) {
				listaMosse = generaListaMosseAusiliario(board.listaPedineNere[i] / 8, board.listaPedineNere[i] % 8,
						1 - color, board);
				for (Mossa mossa : listaMosse) {
					nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
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
					nPedStack = board.getNumeroPedine(mossa.getiStart(), mossa.getjStart());
					if ((mossa.getiEnd() == x && mossa.getjEnd() == y) & nPedStack >= n) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public default boolean checkMosseAusiliario(Mossa m, int c, ScacchieraBit board) {
		int x = m.getiStart();
		int y = m.getjStart();
		int xF = m.getiEnd();
		int yF = m.getjEnd();
		int spostamento = board.calcolaSpostamento(x, y, xF, yF);
		int dir = board.calcolaDirezione(x, y, xF, yF);
		int spostamentoFuori = Mossa.calcolaCelleFuori(x, y, xF, yF, dir);

//		if(!turnoGiocatore) {
//			System.out.println("(!turnoGiocatore && c == PEDINA_BIANCA)"+(!turnoGiocatore && c == PEDINA_BIANCA));
//			System.out.println("scacchiera.getNumeroPedine(x, y) < spostamento:"+ (scacchiera.getNumeroPedine(x, y) < spostamento));
//		}

		// TODO: controllo mosse fuori

		if (board.checkPosOut(xF, yF) & board.getScacchiera().getNumeroPedine(x, y) < spostamentoFuori) {
//			System.out.println("a");
			return false;
		}

		if (board.checkPosOut(m.getiEnd(), m.getjEnd())) {
//			System.out.println("c");
			return true;
		}

		// se lo spostamento richiede un numero di pedine maggiore di quello disponibile
		if (board.getScacchiera().getNumeroPedine(x, y) < spostamento) {
//			System.out.println("b");
			return false;
		}

		// mossa indietro e uguale a 0 ==> non può mangiare
		if (!board.checkMosseInAvanti(m, c)
				&& ((board.getScacchiera().getValue(xF * 8 + yF) == 0) || (board.getColorePedina(xF, yF) == c))) {
//			System.out.println("d");
			return false;
		}

		// CAPTURE
		if (board.getScacchiera().getNumeroPedine(x, y) < board.getScacchiera().getNumeroPedine(xF, yF)) {
//			System.out.println("e");
			return false;
		}

		if (board.getScacchiera().getNumeroPedine(xF, yF) > spostamento) {

//			System.out.println("f");
			return false;
		}

		// P.s. Vengono valutate solo le mosse valide interne alla scacchiera.
		return true;
	}

}
