package progDipole;

import java.util.HashMap;

public class Scacchiera {

	public final int SIZE = 8;
	protected Cella[][] scacchiera;
	/** Codifica il contenuto di una casella vuota. */
	private static final int VUOTA = -1;
	/** Codifica il contenuto di una casella con pedina bianca. */
	private static final int PEDINA_BIANCA = 0;
	/** Codifica il contenuto di una casella con pedina nera. */
	private static final int PEDINA_NERA = 1;
	/** Codifica il COLORE DI UNA CELLA BIANCA */
	private static final int CELLA_BIANCA = 0;
	/** Codifica il COLORE DI UNA CELLA NERA. */
	private static final int CELLA_NERA = 1;
	private int STACK_BIANCO = 12;
	private int STACK_NERO = 12;
	public static final int NORTH = 0;
	public static final int NORTHEAST = 1;
	public static final int EAST = 2;
	public static final int SOUTHEAST = 3;
	public static final int SOUTH = 4;
	public static final int SOUTHWEST = 5;
	public static final int WEST = 6;
	public static final int NORTHWEST = 7;
	private static final int NESSUNA_VITTORIA = 0;
	private static final int VITTORIA_BIANCO = 1;
	private static final int VITTORIA_NERO = 2;
	private boolean turnoGiocatore; // Indica il giocatore che deve giocare

	private enum tipoMossa {
		BASE, MERGE, CAPTURE
	};

	private HashMap<String, Integer> riga;

	/**
	 * Costruttore di Default che inizializza la sessione di gioco
	 */
	public Scacchiera() {
		turnoGiocatore = true;
		riga = new HashMap<>();
		riga.put("A", 0);
		riga.put("B", 1);
		riga.put("C", 2);
		riga.put("D", 3);
		riga.put("E", 4);
		riga.put("F", 5);
		riga.put("G", 6);
		riga.put("H", 7);
		scacchiera = new Cella[SIZE][SIZE];
		inizializzaScacchiera();
	}

	/**
	 * Inizializza la configurazione iniziale della scacchiera. Imposta il colore di
	 * ogni cella e posiziona le pedine iniziali dei due giocatori.
	 */
	public void inizializzaScacchiera() {
		for (int i = 0; i < scacchiera.length; i++) {
			for (int j = 0; j < scacchiera.length; j++) {
				if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
					Cella c = new Cella(i, j, 0, CELLA_NERA, VUOTA);
					scacchiera[i][j] = c;
				} else {
					Cella c = new Cella(i, j, 0, CELLA_BIANCA, VUOTA);
					scacchiera[i][j] = c;
				}

			}
		}
		Cella c = new Cella(0, 3, STACK_NERO, CELLA_NERA, PEDINA_NERA);
		scacchiera[0][3] = c;
		Cella c1 = new Cella(7, 4, STACK_BIANCO, CELLA_NERA, PEDINA_BIANCA);
		scacchiera[7][4] = c1;
	}

	public Cella[][] getScacchiera() {
		return scacchiera;
	}

	public void setScacchiera(Cella[][] scacchiera) {
		this.scacchiera = scacchiera;
	}

	public int getSIZE() {
		return SIZE;
	}

	/**
	 * Return la cella desiderata
	 */
	public Cella getCella(int i, int j) {
		return scacchiera[i][j];
	}

	/**
	 * Controlla se nella cella in riga i e colonna j ci sono pedine posizionate
	 */
	public boolean isEmpty(int i, int j) {
		return scacchiera[i][j].getnPedine() == 0;
	}

	/**
	 * Restituisce il colore di una pedina se esiste, -1 altrimenti.
	 */
	public int getColorePedina(int i, int j) {
		return scacchiera[i][j].getColorePedina();
	}

	/**
	 * Restituisce il colore della cella di riga i e colonna j.
	 */
	public int getColoreCella(int i, int j) {
		return scacchiera[i][j].getColoreCella();
	}

	/**
	 * Controlla se la riga i e la colonna j sono all'interno della scacchiera.
	 */
	public boolean checkPosOut(int i, int j) {
		if (i > 7 || j > 7 || i < 0 || j < 0)
			return false;
		return true;
	}
	/**
	 *converte stringa ("A4")in pos indici 
	 */
	private int[] calcola_indici(String posizione) {
		int[] res = new int[2];
		res[0] = riga.get(posizione.charAt(0)+"");// get da il valore della chiave che in questo caso è la lettera
		res[1] = Integer.parseInt(posizione.substring(1))-1;
		return res;
	}

	private int[] calcola_indici(int i, int j, int dir, int nCelleMove) {
		int [] ris = new int[2];
		switch(dir){
			case NORTH: 
				ris[0]=i - nCelleMove; 
				break;
			case NORTHEAST: 
				ris[1]=j + nCelleMove;
				ris[0]=i - nCelleMove;	
				break;
			case EAST: 
				ris[1]=j + nCelleMove;
				break;
			case SOUTHEAST: 
				ris[1]=j + nCelleMove;
				ris[0]=i + nCelleMove;	
				break;
			case SOUTH:  
				ris[0]=i + nCelleMove;	
				break;
			case SOUTHWEST:  
				ris[1]=j - nCelleMove;
				ris[0]=i + nCelleMove;	
				break;
			case WEST:  
				ris[1]=j - nCelleMove;
				break;
			case NORTHWEST: 
				ris[1]=j - nCelleMove;
				ris[0]=i - nCelleMove;	
				break;
		}
		return ris;
	}

	private double distanzaCelle(int[] x, int[] y){
		return Math.sqrt(Math.pow(x[0]-y[0],2)+Math.pow(x[1]-y[1],2));
	}

	/**
	 * Verifica se la posizione della prossima mossa e' valida. ***DA MODIFICARE***
	 */
	public boolean muovi(String pos_iniziale, int dir, int nCelleMove) {
		
		double distanzaCelle;
		int[] pos = calcola_indici(pos_iniziale);
		int[] pos_finale = calcola_indici(pos[0], pos[1], dir, nCelleMove);
		Cella partenza, destinazione ;
		System.out.format("pos: %d, %d \n",pos[0],pos[1]);
		System.out.format("pos_finale: %d, %d \n",pos_finale[0],pos_finale[1]);
		partenza = scacchiera[pos[0]][pos[1]];
		destinazione = scacchiera[pos_finale[0]][pos_finale[1]];
		distanzaCelle = distanzaCelle(pos,pos_finale);
		
		if ( checkPosOut(pos_finale[0], pos_finale[1]) ){
			if(distanzaCelle>partenza.getnPedine()) return false;
			// altrimenti sta mettendo le pedine fuori
		}
		
		// GESTIONE DEI TURNI
		if (turnoGiocatore && partenza.getColorePedina() == PEDINA_NERA)
			return false;
		if (!turnoGiocatore && partenza.getColorePedina() == PEDINA_BIANCA)
			return false;
		
		if (partenza.getnPedine() == 0 || nCelleMove > partenza.getnPedine())
			return false;
		
		//BASE
		if (destinazione.getnPedine() == 0) {
			if(turnoGiocatore && (dir==SOUTH || dir==SOUTHEAST || dir == SOUTHWEST))
				return false;
			
			if(!turnoGiocatore && (dir==NORTH || dir==NORTHEAST || dir==NORTHWEST))
				return false;
			
			destinazione.base(partenza, nCelleMove);
			
		} else if (partenza.getColorePedina() == destinazione.getColorePedina()) {
			// MERGE//
			destinazione.mergeFrom(partenza, nCelleMove);
		} else {
			// CAPTURE //
			destinazione.captureFrom(partenza, nCelleMove);
		}
		
		turnoGiocatore = !turnoGiocatore;

		return true;
	}

	public int checkVittoria() {
		if (STACK_BIANCO == 0)
			return VITTORIA_NERO;
		else if (STACK_NERO == 0)
			return VITTORIA_BIANCO;
		return NESSUNA_VITTORIA;
	}

	/**
	 * Funzione utile per debug, stampa la scacchiera.
	 * 
	 * Oss. Sia le pedine bianche che le nere si trovano su caselle di colore nero.
	 */
	public void stampaScacchiera() {
		System.out.println();
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		for (r = 0; r < SIZE; r++) {
			for (c = 0; c < SIZE; c++)
				System.out.print(" - ");
			System.out.println("-");
			for (c = 0; c < SIZE; c++) {
				if (scacchiera[r][c].getnPedine() == 0)
					if (scacchiera[r][c].getColoreCella() == CELLA_NERA)
						System.out.print(" N ");
					else
						System.out.print(" B ");
				if (scacchiera[r][c].getnPedine() > 0) {
					if (scacchiera[r][c].getColorePedina() == 0)
						System.out.print(scacchiera[r][c].getnPedine() + "B ");
					else
						System.out.print(scacchiera[r][c].getnPedine() + "N ");
				}
			}
			System.out.println("");
		}
		for (c = 0; c < SIZE; c++)
			System.out.print(" - ");
		System.out.println(" - ");
	}

	public static void main(String[] args) {
		Scacchiera s = new Scacchiera();
		// s.stampaScacchiera();
		// Cella b = s.scacchiera[0][3];
		// Cella a = s.scacchiera[7][4];
		// System.out.println(a);
		// System.out.println(b);
		// System.out.println("");
		// System.out.println(a);
		// System.out.println(b);
		// b.captureFrom(a, 12);
		System.out.println(s.muovi("H5", Scacchiera.NORTH, 2));
		s.stampaScacchiera();
	}

}
