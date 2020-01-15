package Dipole;

import java.util.ArrayList;
import java.util.Random;

import DipoleHeuristics.HeuristicInterface;
import DipoleHeuristics.RandHeuristic;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;

	private int PLAYER;
	private ScacchieraBit root;
	private int PROFONDITA = 4;
	private long start = 0;
	private final static int FINE_GIOCO = 100000;

	private long hashCode;

	private int size = (int) Math.pow(2, 22); // TODO: forse troppo grande - da verificare

	private HeuristicInterface euristica;
	private Zobrist zobrist;
	private TTElement[] transpositionTable;

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = scacchiera;
//		this.transpositionTable = new TTElement[size]; TODO
		zobrist = new Zobrist();
		euristica = new RandHeuristic();
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

	private void caricaStatoCorrente(long hashCode, int value, int depth, Mossa[] mosse, int indexBest) {
		TTElement state = new TTElement(hashCode, depth, value, mosse, indexBest);
		int pos = (int) (hashCode % size);
		transpositionTable[pos] = state;
	}

	public Object[] abNegamax(ScacchieraBit board, byte depth, byte currDepth, int alfa, int beta) {
		ScacchieraBit newBoard;
		int bestScore = Integer.MIN_VALUE, currScore, score;
		Mossa bestMove=null, currMove=null;
		Object[] res;
		// TODO: aggiunta controllo tempo

		// Se è un nodo terminale, valuta il nodo.
		if (board.checkWin() || currDepth == depth)
			return new Object[] { euristica.valuta(board), null };

		ArrayList<Mossa>[] mosse = board.getAllMoves();

		// altrimenti, valuta il nodo in modo ricorsivo.
		for (int i = 0; i < mosse.length; i++) {
			for (Mossa mossa : mosse[i]) {
				
				newBoard = ScacchieraBit.muovi(mossa, board);
				
				res = abNegamax(newBoard, depth, (byte) (currDepth + 1), -beta, -Math.max(bestScore, alfa));
				
				score = ((Integer) res[0]).intValue();
				
				currMove = (Mossa) res[1];
				
				currScore = -score;

				if (currScore > bestScore) {
					bestScore = currScore;
					bestMove = currMove;
				}

				if (bestScore >= beta) {
					return new Object[] { new Integer(bestScore), bestMove };
				}
			}
		}
		
		return new Object[] { new Integer(bestScore), bestMove };
	}

//	public TTElement(long key, int depth, int value, Mossa[] mosse, int indexBest) 

//	private TTElement negamax(ScacchieraBit board, int depht, double alfa, double beta, int player) {
//		long controlloTempo = System.currentTimeMillis() - start;
//        if (controlloTempo >= 2750) 
//          return null;
//		if(board.vittoria(-player))   	
//	    	return new TTElement(board, -FINE_GIOCO);                    
//		if(depht == 0 )
//			return new TTElement(board, board.valutaConfigurazione()*player);
//		double v = -Integer.MAX_VALUE;
//		TTElement nodoPadre = new TTElement(board, alfa);
//		for(Mossa x : board.configurazioniPossibili(player)) {
//	        ScacchieraBit newBoard = new ScacchieraBit(scacchiera);
//	        newBoard.applicaMossa(x.getRiga(), x.getColonna(), player);
//	        TTElement nodoFiglio = negamax(newBoard, depht-1, -beta, -alfa, -player);
//	        if(nodoFiglio == null)
//	        	return null;
//	        v = Math.max(v,-nodoFiglio.getValue());
//	        if(v > alfa) { 
//	        	alfa = v; 
//	        	nodoPadre.setValue(alfa);  
//	        	nodoPadre.setMossa(x.getRiga(), x.getColonna(), x.getGiocatore()); 
//	        }
//	        if(alfa >= beta)
//	        	break;
//		}
//		return nodoPadre;
//	}

	public Mossa negamaxIterativeDeepening() {
		start = System.currentTimeMillis();
		TTElement bestConfig = null;
		Mossa bestMove = null;
		for (int i = 1; i <= PROFONDITA; i++) {
//        	bestConfig = negamax(scacchiera, i, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
			if (bestConfig != null) {
				bestMove = bestConfig.getBestMove(bestConfig.getM());
				if (bestConfig.getValue() == FINE_GIOCO)
					return bestMove;
			} else
				break;
		}
		return bestMove;
	}

	public int valutaConfigurazioneRandom(ScacchieraBit s) {
		return new Random().nextInt(1000);
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s, 0);
		s.stampaScacchiera();
//		p.stampaScacchiera(s.getScacchiera(), s);
		p.saveState();
	}

}
