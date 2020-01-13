package Dipole;

import java.util.ArrayList;

import util.ByteMap;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;

	private int PLAYER;
	private ScacchieraBit root;
	private int PROFONDITA = 4;
	private long start = 0;
	private final static int FINE_GIOCO = 100000;
	
	private TTElement[] transpositionTable;
	private long hashCode;

//	private int size = (int) Math.pow(2, 22); forse troppo grande - da verificare
	private int size = (int) Math.pow(2, 19);

	
	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.root = scacchiera;
//		this.transpositionTable = new TTElement[size]; TODO
		Zobrist z = new Zobrist();
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
        for(int i=1; i<=PROFONDITA;i++) {  	
//        	bestConfig = negamax(scacchiera, i, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
        	if (bestConfig != null) {
        		bestMove = bestConfig.getBestMove(bestConfig.getM());
        		if(bestConfig.getValue() == FINE_GIOCO) 
           			return bestMove;
        	}
            else
                break;
        }			
        return bestMove;
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s, 0);
		s.stampaScacchiera();
//		p.stampaScacchiera(s.getScacchiera(), s);
		p.saveState();
	}

}
