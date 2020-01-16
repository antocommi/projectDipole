package Dipole;

import java.util.ArrayList;
import java.util.Random;

import DipoleHeuristics.HeuristicInterface;
import DipoleHeuristics.RandHeuristic;
import util.TraspositionTable;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;

	private int PLAYER;
	private ScacchieraBit root;
	private int PROFONDITA = 4;
	private long start = 0;
	private final static int FINE_GIOCO = 100000;
	private static final int TT_SIZE = 10000;

	private long hashCode;

	private int size = (int) Math.pow(2, 22); // TODO: forse troppo grande - da verificare

	private HeuristicInterface euristica;
	private Zobrist zobrist;
	private TraspositionTable traspositionTable;
//	private TTElement[] transpositionTable;

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = scacchiera;
//		this.transpositionTable = new TTElement[size]; TODO
		zobrist = new Zobrist();
		euristica = new RandHeuristic();
		traspositionTable = new TraspositionTable(TT_SIZE);
	}

	public void play() {

	}

	public void saveState() {
		root.generaMosse(0, 3);
		stampaMosse(root.getMoves());
	}

	public void stampaMosse(ArrayList<Mossa> m) {
		System.out.println(" ");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println(" ");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}

	public Object[] abNegamax(ScacchieraBit board, int depth, int currDepth, int alfa, int beta) {
		long controlloTempo = System.currentTimeMillis() - start;
		if (controlloTempo >= 800)
			return null;

		ScacchieraBit newBoard;
		int bestScore = Integer.MIN_VALUE, currScore, score;
		Mossa bestMove = null, currMove = null;
		Object[] res;
		System.out.println("checkwin: " + board.checkWin());
		if (board.checkWin() || currDepth == depth) {
//		if(currDepth == depth) {
			board.stampaScacchiera();
			return new Object[] { euristica.valuta(board), null };
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
//		System.out.println("--------negamax-------");
//		for (Mossa m : mosse) {
//			System.out.println(m);
//		}
//		System.out.println("--------negamax-------");
		for (Mossa mossa : mosse) {
			newBoard = ScacchieraBit.muovi(mossa, board);
			res = abNegamax(newBoard, depth, ++currDepth, -beta, -Math.max(bestScore, alfa));
			score = ((Integer) res[0]).intValue();
			currMove = (Mossa) res[1];
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

//	public Object[] abNegamax(ScacchieraBit board, byte depth, byte currDepth, int alfa, int beta) {
	public Mossa negamaxIterativeDeepening() {
		start = System.currentTimeMillis();
		Object[] bestConfig = null;
		Mossa bestMove = null;
		int bestScore = 0;
		System.out.println("nid 1");
		for (int i = 1; i <= PROFONDITA; i++) {
			bestConfig = abNegamax(root, i, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
			System.out.println("nid 2");
			if (bestConfig[1] != null) {

				System.out.println("nid 22");
				bestMove = (Mossa) bestConfig[1];
				if (((Integer) bestConfig[0]) == FINE_GIOCO)
					return bestMove;
			} // else
				// break;
		}

		System.out.println("3");
		return bestMove;
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s, 0);
		Mossa m = p.negamaxIterativeDeepening();
		System.out.println(m);
	}

}
