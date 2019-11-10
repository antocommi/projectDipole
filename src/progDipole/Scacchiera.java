package dipoleF;

public class Scacchiera {

	public final int SIZE = 8;
	protected Cella[][] scacchiera;

	private enum tipoMossa {
		BASE, MERGE, CAPTURE
	};

	/**
	 * Costruttore di Default che inizializza la sessione di gioco
	 */
	public Scacchiera() {
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
				if (i % 2 == 0 && j % 2 != 0) {
					Cella c = new Cella(i, j, 0, 1, -1);
					scacchiera[i][j] = c;
				} else if (i % 2 == 0 && j % 2 == 0) {
					Cella c = new Cella(i, j, 0, 0, -1);
					scacchiera[i][j] = c;
				} else if (i % 2 != 0 && j % 2 == 0) {
					Cella c = new Cella(i, j, 0, 1, -1);
					scacchiera[i][j] = c;
				} else {
					Cella c = new Cella(i, j, 0, 0, -1);
					scacchiera[i][j] = c;
				}
				if (i == 0 && j == 3) {
					Cella c = new Cella(i, j, 12, 1, 0);
					scacchiera[i][j] = c;
				}
				if (i == 7 && j == 4) {
					Cella c = new Cella(i, j, 12, 0, 1);
					scacchiera[i][j] = c;
				}
			}
		}
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
	 * Verifica se la posizione della prossima mossa è valida. ***DA MODIFICARE***
	 */
	public boolean checkPosizione(int rigaStart, int colStart, int rigaEnd, int colEnd, int numCelle) {

		Cella partenza = scacchiera[rigaStart][colStart];
		Cella destinazione = scacchiera[rigaEnd][colEnd];

		if (partenza.getnPedine() == 0 || numCelle > partenza.getnPedine())
			return false;
		if (checkPosOut(rigaEnd, colEnd))
			return true;

		// possibilità DI MOSSA SICURA

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
					if (scacchiera[r][c].getColoreCella() == 1)
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
