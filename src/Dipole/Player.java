package Dipole;

import java.util.ArrayList;
import java.util.Random;

import util.ByteMap;

public class Player {

	private static final int PEDINA_BIANCA = 0;
	private static final int PEDINA_NERA = 1;

	private int PLAYER;
	private ScacchieraBit scacchiera;
	private int PROFONDITA = 4;
	private long start = 0;
	private final static int FINE_GIOCO = 100000;
	
	private TTElement[] transpositionTable;
	private long hashCode;

	private int size = (int) Math.pow(2, 22);

	public Player(ScacchieraBit scacchiera, int player) {
		this.PLAYER = player;
		this.scacchiera = scacchiera;
		this.transpositionTable = new TTElement[size];
		Zobrist z = new Zobrist();
	}

	public void saveState() {
		scacchiera.generaMosse(0, 3);
		stampaMosse(scacchiera.getMoves());
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

	/**
	 * Funzione utile per debug, stampa la scacchiera. Oss. Sia le pedine bianche
	 * che le nere si trovano su caselle di colore nero.
	 */
	public void stampaScacchiera(ByteMap scacchiera, ScacchieraBit s) {
		System.out.println();
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		for (r = 0; r < 8; r++) {
			for (c = 0; c < 8; c++)
				System.out.print(" - ");
			System.out.println("-");
			for (c = 0; c < 8; c++) {
				if (scacchiera.getNumeroPedine(r, c) == 0) {
					if ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0)) {
						System.out.print(" N ");
					} else {
						System.out.print(" B ");
					}
				}
				if (scacchiera.getNumeroPedine(r, c) > 0) {
					if (s.getColorePedina(r, c) == PEDINA_NERA) {
						System.out.print(scacchiera.getNumeroPedine(r, c) + "N");
					} else if (s.getColorePedina(r, c) == PEDINA_BIANCA) {
						System.out.print(scacchiera.getNumeroPedine(r, c) + "B");
					}
				}

			}
			System.out.println("");
		}
		for (c = 0; c < 8; c++)
			System.out.print(" - ");
		System.out.println(" - ");
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
	
	public int valutaConfigurazioneRandom(ScacchieraBit s) {
		return new Random().nextInt(1000);
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		Player p = new Player(s, 0);
		p.stampaScacchiera(s.getScacchiera(), s);
		p.saveState();
	}

}
