package Dipole;

import java.util.ArrayList;
import java.util.Scanner;

import DipoleHeuristics.HeuristicInterface;
import DipoleHeuristics.NaiveHeuristic;
import util.TraspositionTable;

public class PlayerM {

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
	private long hashCode;

	private int size = (int) Math.pow(2, 22); // TODO: forse troppo grande - da verificare

	private HeuristicInterface euristica;
	private Zobrist zobrist;
	private TraspositionTable traspositionTable;
//	private TTElement[] transpositionTable;
	private boolean turnoGiocatore;

	public PlayerM(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = scacchiera;
//		this.transpositionTable = new TTElement[size]; TODO
		zobrist = new Zobrist();
		euristica = new NaiveHeuristic();
		traspositionTable = new TraspositionTable(TT_SIZE);
		turnoGiocatore = scacchiera.getTurnoGiocatore();
	}

	public Object[] abNegamax(ScacchieraBit board, int depth, int currDepth, int alfa, int beta, Mossa[] path) {

		// STAMPE INIZIALI
		long controlloTempo = System.currentTimeMillis() - start;
//		System.out.println("INIZIO");
//		System.out.println("INIZIO");
//		System.out.println("INIZIO");

//		System.out.println("");
//		System.out.println("_____________________________________________________________________");
//		System.out.println("");
//		System.out.format("CurrDepth: %d Depth: %d Alfa: %d Beta: %d Tempo: %d \n", currDepth, depth, alfa, beta,
//				controlloTempo);
//		System.out.println("");
//		System.out.println("_____________________________________________________________________");
		// SE SUPERA IL TEMPO MASSIMO RITORNA UN INTEGER DI ERRORE
		if (controlloTempo >= 900) {
//			System.out.println("TEMPO SCADUTO");
			return new Object[] { new Integer(-1), null };
		}
		ScacchieraBit newBoard = null;
		int bestScore = Integer.MIN_VALUE;
		int currScore = 0;
		int score = 0;
		Mossa bestMove = null;
		Mossa currMove = null;
		Object[] res;

		// SE QUESTA CONFIGURAZUIONE CI PORTA ALLA VITTORIA
		if (board.checkFin(board) || currDepth == depth) {
			int giocatore = board.getTurnoGiocatore() ? 0 : 1;
			// int e = euristica.valuta(board, giocatore);
			int e=0;
			if (path[path.length - 1] != null)
				e = euristica.valuta(board, giocatore, path[path.length - 1]);

//			System.out.println("");
//			System.out.println("______________________________________________");
//			System.out.println("Valore euristica: " + e);
////			board.stampaScacchiera();
//			System.out.println("______________________________________________");
//			System.out.println("");

//			System.out.println("VAFFANCULO");
//			if (path[0] != null)
//				System.out.println(path[0]);
//			System.out.println("VAFFANCULO");

			return new Object[] { e, null };
		}

		TTElement trasposition;
		long ttKey = zobrist.getHashcode(board, board.getTurnoGiocatore() ? 0 : 1);
		ArrayList<Mossa> mosse;

//		System.out.println("***************************************** D*SD**D*D*D*D*D*D*");

		if (traspositionTable.contains(ttKey)) {
			trasposition = traspositionTable.get(ttKey);
			mosse = trasposition.getM();
//			System.out.println("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO CICOOOOOOOOOOOOO");
//			System.out.println("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO CICOOOOOOOOOOOOO");
//
//			System.out.println("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO CICOOOOOOOOOOOOO");
//
//			System.out.println("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO CICOOOOOOOOOOOOO");
			
//			System.out.println("");
//			System.out.println("----------------------------------");
//			System.out.format("MOssa CORENTE iS= %d - jS= %d - iF= %d - jF= %d ", mosse.get(0).getiStart(),	mosse.get(0).getjStart(), mosse.get(0).getiEnd(), mosse.get(0).getjEnd());
//			System.out.println("----------------------------------");
//			System.out.println("");
		} else {
			mosse = board.getAllMoves();
//			for (Mossa mossa : mosse) {
//				System.out.println("------------M OSSE --------------");
//				System.out.println(mossa);
//				System.out.println("----------------------------------");
//			}
//			System.out.println("CICOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		}

		if (mosse.size() == 0) {
			System.out.println("Nessuna mossa disponibile!!!");
			board.debugStatus(true, "Nessuna mossa disponibile");
		}

		for (Mossa mossa : mosse) {
			path[currDepth] = mossa;
			try {
				newBoard = ScacchieraBit.muovi(mossa, board,board.getTurnoGiocatore() ? 0 : 1);
//				newBoard.setTurnoGiocatore(!newBoard.getTurnoGiocatore());
//				System.out.println("scacchiera");
				
//				System.out.println("----------------------------------");
//				newBoard.stampaScacchiera();
//				System.out.println("----------------------------------");
				

			} catch (Exception e) {
				e.printStackTrace();
			}

			int max = 0;
			if (bestScore > alfa) {
//				System.out.println(" ");
//				System.out.println("ALFA: " + alfa + " MINORE DI BETA: " + bestScore);
//				System.out.println(" ");
				max = bestScore;
			} else {
				max = alfa;
//				System.out.println(" ");
//				System.out.println("BETA: " + beta + " MAGGIORE DI ALFA: " + alfa);
//				System.out.println(" ");
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

//			System.out.println("      ");
//			System.out.println("CURRENT SCORE: ________ " + currScore);
//			System.out.println("BEST SCORE: _________" + bestScore);
//			System.out.println("BETA: _________" + beta);
//			System.out.println("      ");
//			System.out.println(currMove);
//			System.out.println("   ");

			if (currScore > bestScore) {
				bestScore = currScore;
				bestMove = currMove;
//				System.out.println("CICO");
			}

			trasposition = new TTElement(ttKey, currDepth, currScore, mosse, 0);
			traspositionTable.put(ttKey, trasposition);

			if (bestScore >= beta) {
//				System.out.println("esco da bestScore>=beta!!" + bestMove);
				return new Object[] { new Integer(bestScore), bestMove };
			}
		}
//		System.out.println("esco dall'ultimo return " + bestScore + " bestMove:" + bestMove);
		return new Object[] { new Integer(bestScore), bestMove };
	}

	public Object[] negamaxIterativeDeepening() {
		start = System.currentTimeMillis();
		Object[] bestConfig = null;
		Object[] bestConfigAdversary = null;
		Mossa bestMoveAdversary = null;
		Mossa bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		int alfa = -MAX;
		int beta = MAX;
		int alfaAdversary = -MAX;
		int betaAdversary = MAX;
//		System.out.println("1");
		for (int i = 1; i <= PROFONDITA; i++) {
//			System.out.println("IterativeDeepining - Profondita': " + i);
			bestConfig = abNegamax(root, i, 0, alfa, beta, new Mossa[i]);
			Mossa m = (Mossa) bestConfig[1];
			int a = (int) bestConfig[0];
//			System.out.println("Valori ritornati da negamax " + a + " " + m);
			if (bestConfig[1] != null) {
				bestMove = (Mossa) bestConfig[1];
				bestScore = (int) bestConfig[0];
//				System.out.println("CONFIGURAZIONE DIVERSA DA NULL _------------------------------------>");
//				System.out.println("CONFIGURAZIONE DIVERSA DA NULL _------------------------------------>");
				if (((Integer) bestConfig[0]) == FINE_GIOCO) {
//					System.out.println("RETURN QUANDO FINISCE IL GIOCO");
//					System.out.println("RETURN QUANDO FINISCE IL GIOCO");
					return new Object[] { bestScore, bestMove };
				}
//				root.muovi((Mossa)bestConfig[1]);
//				boolean turno =  (turnoGiocatore== true) ? false : true;
//				root.setTurnoGiocatore(turno);
//				System.out.println("__________________---------------------------------______________________________________");
//				bestConfigAdversary = abNegamax(root, i, 0, alfaAdversary, betaAdversary, new Mossa [i]);
//				root.muovi((Mossa)bestConfigAdversary[1]);
//				System.out.println("-------------------------------     --------------------");
//				System.out.println(""+bestConfigAdversary[1]);
//				System.out.println("--------------------    -------------------------------");
				
			}

//			System.out.println("RETURN FINALEEEEEE");
//			System.out.println("RETURN FINALEEEEEE");
		}

//		System.out.println("RETURN NEGAMAXITERATIVING ---------------------->");
		return new Object[] { bestScore, bestMove };
	}

	public void saveState() {
		stampaMosse(root.generaListaMosse(0, 3,PLAYER));
	}

	public void stampaMosse(ArrayList<Mossa> m) {
		System.out.println(" ");
		System.out.println("Mosse disponibili: " + m.size());
		System.out.println(" ");
		for (Mossa mossa : m) {
			System.out.println(mossa);
		}
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s, 0);
		Player p1 = new Player(s, 1);
		int mossePartita=0;
		Scanner scanner = new Scanner(System.in);
		while(mossePartita < 60) {
			Object[] res = p.negamaxIterativeDeepening();
			Mossa m = (Mossa) res[1];
			s.muovi(m,0);
			int valoreEuristica = (int) res[0];
			System.out.println("");
			System.out.println("Il Player 0 effettua la "+ m);
			System.out.println("");
			s.stampaScacchiera();
			System.out.println("");
			System.out.println("Inserire la mossa dell'avversario: ");
			System.out.println("");
			System.out.print("x: \n");
	        int x = Integer.parseInt(scanner. nextLine());
	        System.out.println("y \n");
	        int y = Integer.parseInt(scanner. nextLine());
	        System.out.println("xF \n");
	        int xF = Integer.parseInt(scanner. nextLine());
	        System.out.println("yF \n");
	        int yF = Integer.parseInt(scanner. nextLine());
			Mossa m1 = new Mossa(x, y, xF, yF, s.calcolaDirezione(x,y,xF,yF));
			s.muovi(m1,1);
			System.out.println("Il Player 1 effettua la mossa "+ m1);
			System.out.println("");
			mossePartita++;
			s.stampaScacchiera();
			System.out.println("");
		}
		scanner.close();
	}

}
