
package Dipole;

import java.util.ArrayList;

import DipoleHeuristics.B_Heuristic;
import DipoleHeuristics.HeuristicInterface;
import DipoleHeuristics.N_Heuristic;
import DipoleHeuristics.NaiveHeuristic;
import util.TraspositionTable;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;
	private static final int MAX = Integer.MAX_VALUE;

	private int PLAYER;
	private ScacchieraBit root;
	private int PROFONDITA = 9;
	private long start = 0;
	private final static int FINE_GIOCO = 100000;
	private static final int TT_SIZE = 10000;
//	private int NUMERO_MAX_MOSSE = 60;
	public static final int SIZE = 8;

	private HeuristicInterface euristica;
	private Zobrist zobrist;
	private TraspositionTable traspositionTable;

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = new ScacchieraBit(scacchiera);
		zobrist = new Zobrist();
		if (player == PEDINA_BIANCA) {
			euristica = new B_Heuristic();
		} else {
			euristica = new B_Heuristic();
		}
		traspositionTable = new TraspositionTable(TT_SIZE);
	}

	public void muovi(Mossa mossa, int player) {
//		if(mossa==null) {
//			System.out.println("Passata una mossa nulla");
//			root.debugStatus(true, "Si vuole muovere con una mossa null");
//			throw new RuntimeException("Null");
//		}
		root.muovi(mossa, player);
	}

	public Mossa elaboraProssimaMossa() {
		Object[] res = negamaxIterativeDeepening();
		return (Mossa) res[1];
	}

	public void debug(boolean stampaMosse, String nome) {
		root.debugStatus(stampaMosse, nome);
	}

	@SuppressWarnings("deprecation")
	public Object[] abNegamax(ScacchieraBit board, int depth, int currDepth, int alfa, int beta, Mossa[] path) {
		long controlloTempo = System.currentTimeMillis() - start;
		if (controlloTempo >= 600) {
			return new Object[] { new Integer(-1), null };
		}
		ScacchieraBit newBoard = null;
		int bestScore = Integer.MIN_VALUE;
		int currScore = 0;
		int score = 0;
		Mossa bestMove = null;
		Mossa currMove = null;
		Object[] res;
		if (board.checkFin(board) || currDepth == depth) {
//			int giocatore = board.getTurnoGiocatore() ? 0 : 1;
//			System.out.println("giocatore Fin "+ giocatore);
			int e = 0;
			if (path[path.length - 1] != null)
//				System.out.println("ei"+giocatore);
				e = euristica.valuta(board, PLAYER, path[path.length - 1]);
			return new Object[] { e, null };
		}
		TTElement trasposition;
		long ttKey = zobrist.getHashcode(board, board.getTurnoGiocatore() ? 0 : 1);
		ArrayList<Mossa> mosse;
		if (traspositionTable.contains(ttKey)) {
			trasposition = traspositionTable.get(ttKey);
			mosse = trasposition.getM();
		} else {
			mosse = board.getAllMoves();
		}
		if (mosse.size() == 0) {
			System.out.println("Nessuna mossa disponibile!!!");
			board.debugStatus(true, "Nessuna mossa disponibile");
		}
		for (Mossa mossa : mosse) {
			path[currDepth] = mossa;
			System.out.println("mossa in player"+mossa);
			try {
				newBoard = ScacchieraBit.muovi(mossa, board, board.getTurnoGiocatore() ? 0 : 1);
				assert (newBoard.getTurnoGiocatore() != board.getTurnoGiocatore());
			} catch (Exception e) {
				e.printStackTrace();
			}

			int max = 0;
			if (bestScore > alfa) {
				max = bestScore;
			} else {
				max = alfa;
			}
			res = abNegamax(newBoard, depth, currDepth + 1, -beta, -max, path);
			try {
				score = ((int) res[0]);
				currMove = path[depth - 1];
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			path[currDepth] = null;
			currScore = -score;
			if (currScore > bestScore) {
				bestScore = currScore;
				bestMove = currMove;
			}

			trasposition = new TTElement(ttKey, currDepth, currScore, mosse, 0);
			traspositionTable.put(ttKey, trasposition);

			if (bestScore >= beta) {
				return new Object[] { new Integer(bestScore), bestMove };
			}
		}
		return new Object[] { new Integer(bestScore), bestMove };
	}

	public Object[] negamaxIterativeDeepening() {
		start = System.currentTimeMillis();
		Object[] bestConfig = null;
		Mossa bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		int alfa = -MAX;
		int beta = MAX;

		for (int i = 1; i <= PROFONDITA; i++) {
			bestConfig = abNegamax(root, i, 0, alfa, beta, new Mossa[i]);
			if (bestConfig[1] != null) {
				bestMove = (Mossa) bestConfig[1];
				bestScore = (int) bestConfig[0];
				if (((Integer) bestConfig[0]) == FINE_GIOCO) {
					return new Object[] { bestScore, bestMove };
				}
			}
		}
		return new Object[] { bestScore, bestMove };
	}

	public void stampaMosse(ArrayList<Mossa> m) {
		System.out.println(" ");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println(" ");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}

	public int getVincitore() {
		if (root.getMosseMaxBianco() == 0 || root.getMosseMaxNero() == 0) {
			if (root.getNumeroPedineTot(PEDINA_BIANCA) > root.getNumeroPedineTot(PEDINA_NERA))
				return PEDINA_BIANCA;
			if (root.getNumeroPedineTot(PEDINA_NERA) > root.getNumeroPedineTot(PEDINA_BIANCA))
				return PEDINA_NERA;
			if (root.getNumeroPedineTot(PEDINA_NERA) == root.getNumeroPedineTot(PEDINA_BIANCA))
				return 2;
		}
		if (root.getNumeroStackGiocatore(PEDINA_BIANCA) == 0 || root.zeroMosse(PEDINA_BIANCA))
			return PEDINA_NERA;
		if (root.getNumeroStackGiocatore(PEDINA_NERA) == 0 || root.zeroMosse(PEDINA_NERA))
			return PEDINA_BIANCA;
		return -1;
	}

	public boolean getTurnoGiocatore() {
		return root.getTurnoGiocatore();
	}
}
