package Dipole;

import java.util.ArrayList;
import java.util.Random;

import DipoleHeuristics.HeuristicInterface;
import DipoleHeuristics.NaiveHeuristic;
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
	private boolean turnoGiocatore;

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = scacchiera;
//		this.transpositionTable = new TTElement[size]; TODO
		zobrist = new Zobrist();
		euristica = new NaiveHeuristic();
		traspositionTable = new TraspositionTable(TT_SIZE);
	}

	public Object[] abNegamax(ScacchieraBit board, int depth, int currDepth, int alfa, int beta) {
		long controlloTempo = System.currentTimeMillis() - start;
		System.out.print("|");
		for (int i = 0; i < currDepth; i++) {
			System.out.format("--");
		}
		System.out.format("currDepth: %d depth: %d alfa: %d beta: %d tempo: %d\n", currDepth, depth, alfa, beta,
				controlloTempo);
		if (controlloTempo >= 800)
			return null;
		ScacchieraBit newBoard = null;
		int bestScore = Integer.MIN_VALUE, currScore, score;
		Mossa bestMove = null, currMove = null;
		Object[] res;
//		System.out.println("checkwin: " + board.checkWin());
		if (board.checkFin() || currDepth >= depth) {
			int giocatore = board.getTurnoGiocatore() ? 0 : 1;
			int e = euristica.valuta(board, giocatore);
//			board.debugStatus(false, "negamax");
//			System.out.println("Valore euristica: " + e);
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
//			System.out.println("Nessuna mossa disponibile!!!");
			board.debugStatus(true, "Nessuna mossa disponibile");
		}

		for (Mossa mossa : mosse) {
//			System.out.println(mossa);
//			board.debugStatus(false, "oldBoard "+depth+" "+currDepth);
			try {
				newBoard = ScacchieraBit.muovi(mossa, board);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(mossa);
				board.debugStatus(true, "eccezione lanciata");
			}
//			newBoard.debugStatus(false, "newBoard "+depth+" "+currDepth);
			res = abNegamax(newBoard, depth, currDepth + 1, -beta, -Math.max(bestScore, alfa));
			score = ((int) res[0]);
			currMove = (Mossa) res[1];
			currScore = -score;

			if (currScore > bestScore) {
				bestScore = currScore;
				bestMove = currMove;
			}

			trasposition = new TTElement(ttKey, currDepth, currScore, mosse, 0);
			traspositionTable.put(ttKey, trasposition);

			if (bestScore >= beta) {
				System.out.println("esco da questo!!");
				return new Object[] { new Integer(bestScore), bestMove };
			}
		}
		return new Object[] { new Integer(bestScore), bestMove };
	}

//	public Object[] abNegamax(ScacchieraBit board, byte depth, byte currDepth, int alfa, int beta) {
	public Object[] negamaxIterativeDeepening() {
		start = System.currentTimeMillis();
		Object[] bestConfig = null;
		Mossa bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		System.out.println("1");
		for (int i = 1; i <= PROFONDITA; i++) {
			System.out.println("IterativeDeepining - Profondita': " + i);
			bestConfig = abNegamax(root, i, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
			Mossa m = (Mossa) bestConfig[1];
			int a = (int) bestConfig[0];
			System.out.println("Valori ritornati da negamax " + a + " " + m);
			if (bestConfig[1] != null) {
				bestMove = (Mossa) bestConfig[1];
				bestScore = (int) bestConfig[0];
				if (((Integer) bestConfig[0]) == FINE_GIOCO)
					return new Object[] { bestScore, bestMove };
			}
			// else {
			// break;
			// }
		}
//		System.out.println("XXX -- esco da qui");
		return new Object[] { bestScore, bestMove };
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		long seed = System.currentTimeMillis();
		Random r = new Random(seed);
		System.out.println("SEED utilizzato: " + seed);
		ArrayList<Mossa> moves;
		int scelta;
		for (int i = 0; i < 5; i++) {
			System.out.println("XXX - " + i);
			moves = s.getAllMoves();
//			System.out.println("size: " + moves.size());
			if (moves.size() > 0) {
				scelta = r.nextInt(moves.size());
//				System.out.println(moves.get(scelta));
				
				try{
					s = ScacchieraBit.muovi(moves.get(scelta), s);
					if(s.checkFin()) break;
					s.debugStatus(false, moves.get(scelta).toString());
				}catch(Exception e) {
					e.printStackTrace();
					s.debugStatus(false, "errore");
					break;
				}
//				System.out.println("\n\n\n");
			}
		}
		s.debugStatus(true, "fine");
//		Player p = new Player(s, 0);
//		Object[] res = p.negamaxIterativeDeepening();
//		Mossa m = (Mossa) res[1];
//		int a = (int) res[0];
//		System.out.println("Valori ritornati da negamax " + a + " " + m);
	}

	public void saveState() {
		stampaMosse(root.generaListaMosse(0, 3));
	}

	public void stampaMosse(ArrayList<Mossa> m) {
		System.out.println(" ");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println(" ");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}
}
