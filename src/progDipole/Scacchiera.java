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
	private static final int STACK_BIANCO = 12;
	private static final int STACK_NERO = 12;
	private static final int NORTH = 0;
	private static final int NORTHEAST= 1;
	private static final int EAST= 2;
	private static final int SOUTHEAST= 3;
	private static final int SOUTH= 4;
	private static final int SOUTWEST= 5;
	private static final int WEST= 6;
	private static final int NORTHWEST= 7;
	private static final int NESSUNA_VITTORIA  = 0;
	private static final int VITTORIA_BIANCO  = 1;
	private static final int VITTORIA_NERO  = 2;
	private boolean turnoGiocatore; // Indica il giocatore che deve giocare  
	
	
	
	
	private enum tipoMossa {
		BASE, MERGE, CAPTURE
	};
	
	
	private HashMap<String,Integer> riga;

	/**
	 * Costruttore di Default che inizializza la sessione di gioco
	 */
	public Scacchiera() {
		turnoGiocatore = true;
		riga = new HashMap<>();
		riga.put("A",0);
		riga.put("B",1);
		riga.put("C",2);
		riga.put("D",3);
		riga.put("E",4);
		riga.put("F",5);
		riga.put("G",6);
		riga.put("H",7);
		scacchiera = new Cella[SIZE][SIZE];
		inizializzaScacchiera();
	}

	/**
	 * Inizializza la configurazione iniziale della scacchiera. Imposta il colore di
	 * ogni cella e posiziona le pedine iniziali dei due giocatori.
	 */
	public void inizializzaScacchiera() {
		for (int i = 0; i < scacchiera.length; i++) {
			for (int j = 0; j <scacchiera.length; j++) {
				if ((i % 2 == 0 && j % 2 != 0)||(i % 2 != 0 && j % 2 == 0)) {
					Cella c = new Cella(i, j, 0, CELLA_NERA, VUOTA);
					scacchiera[i][j] = c;
				}
				else {
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
	 * Verifica se la posizione della prossima mossa � valida. ***DA MODIFICARE***
	 */
	public boolean checkPosizione(int rigaStart, int colStart, int rigaEnd, int colEnd, int numCelle) {
		
		Cella partenza = scacchiera[rigaStart][colStart];
		Cella destinazione = scacchiera[rigaEnd][colEnd];
		
		if (turnoGiocatore && partenza.getColorePedina()==PEDINA_NERA) return false;
		
		if (!turnoGiocatore && partenza.getColorePedina()==PEDINA_BIANCA) return false;

		if (partenza.getnPedine() == 0 || numCelle > partenza.getnPedine())
			return false;
		if (checkPosOut(rigaEnd, colEnd))
			return true;

		// possibilit� DI MOSSA SICURA

		if (destinazione.getnPedine() == 0) {
			// BASE//TODO
			return false;
		} else if (partenza.getColorePedina() == destinazione.getColorePedina()) {
			// MERGE//TODO
			return false;
		} else {
			// CAPTURE //TODO
			return false;
		}
	}
	
	public int checkVittoria() {
		if(STACK_BIANCO==0) return VITTORIA_NERO;
		else if (STACK_NERO==0) return VITTORIA_BIANCO;
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
		s.stampaScacchiera();
	}

}
