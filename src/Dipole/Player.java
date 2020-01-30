
package Dipole;

import java.util.ArrayList;
import java.util.Scanner;

import DipoleHeuristics.HeuristicInterface;
import DipoleHeuristics.NaiveHeuristic;
import util.TraspositionTable;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;
	private static final int MAX = Integer.MAX_VALUE;

	private int PLAYER;
	private ScacchieraBit root;
	private int PROFONDITA = 5;
	private long start = 0;
	private final static int FINE_GIOCO = 100000;
	private static final int TT_SIZE = 10000;
	private int NUMERO_MAX_MOSSE = 60;
	public static final int SIZE = 8;

	private HeuristicInterface euristica;
	private Zobrist zobrist;
	private TraspositionTable traspositionTable;

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = new ScacchieraBit(scacchiera);
		zobrist = new Zobrist();
		euristica = new NaiveHeuristic();
		traspositionTable = new TraspositionTable(TT_SIZE);
	}

	public void muovi(Mossa mossa, int player) {
		root.muovi(mossa, player);
	}

	public Mossa elaboraProssimaMossa() {
		Object[] res = negamaxIterativeDeepening();
		return (Mossa) res[1];
	}

	@SuppressWarnings("deprecation")
	public Object[] abNegamax(ScacchieraBit board, int depth, int currDepth, int alfa, int beta, Mossa[] path) {
		long controlloTempo = System.currentTimeMillis() - start;
		if (controlloTempo >= 900) {
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
			int giocatore = board.getTurnoGiocatore() ? 0 : 1;
			int e = 0;
			if (path[path.length - 1] != null)
				e = euristica.valuta(board, giocatore, path[path.length - 1]);
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
			try {
				newBoard = ScacchieraBit.muovi(mossa, board, board.getTurnoGiocatore() ? 0 : 1);
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

	public void saveState() {
		stampaMosse(root.generaListaMosse(0, 3, PLAYER));
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

	public void stampaScacchiera() {
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		for (r = 0; r < SIZE; r++) {
			for (c = 0; c < SIZE; c++) {
				System.out.print(" - ");
			}
			System.out.println("-");
			for (c = 0; c < SIZE; c++) {
				if (root.getNumeroPedine(r, c) == 0)
					if ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0))
						System.out.print(" N ");
					else
						System.out.print(" B ");
				if (root.getNumeroPedine(r, c) > 0)
					if (root.getColorePedina(r, c) == PEDINA_NERA)
						System.out.print(root.getNumeroPedine(r, c) + "N");
					else if (root.getColorePedina(r, c) == PEDINA_BIANCA)
						System.out.print(root.getNumeroPedine(r, c) + "B");
			}
			System.out.println("");
		}
		for (c = 0; c < SIZE; c++)
			System.out.print(" - ");
		System.out.println(" - ");
	}

	public static void main(String[] args) {
		ScacchieraBit root = new ScacchieraBit();
		Player player = new Player(root, PEDINA_BIANCA);
		int mossePartita = 0;
		Scanner scanner = new Scanner(System.in);
		while (mossePartita < player.NUMERO_MAX_MOSSE) {
			Mossa mossa = player.elaboraProssimaMossa();
			player.muovi(mossa, PEDINA_BIANCA);
			System.out.println("");
			System.out.println("Il Player 0 effettua la " + mossa);
			System.out.println("");
			player.stampaScacchiera();
			System.out.println("");
			System.out.println("Inserire la mossa dell'avversario: ");
			System.out.println("");
			System.out.print("x: \n");
			int x = Integer.parseInt(scanner.nextLine());
			System.out.println("y \n");
			int y = Integer.parseInt(scanner.nextLine());
			System.out.println("xF \n");
			int xF = Integer.parseInt(scanner.nextLine());
			System.out.println("yF \n");
			int yF = Integer.parseInt(scanner.nextLine());
			Mossa m1 = new Mossa(x, y, xF, yF, root.calcolaDirezione(x, y, xF, yF));
			player.muovi(m1, PEDINA_NERA);
			System.out.println("Il Player 1 effettua la mossa " + m1);
			System.out.println("");
			mossePartita++;
			player.stampaScacchiera();
			System.out.println("");
		}
		scanner.close();
	}

}
