package DipoleHeuristics;

import java.util.ArrayList;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public interface HeuristicInterface {
	
	/*
	 * Il metodo valuta la configurazione in ingresso basandosi sulla stato attuale. Non necessariamente
	 * si deve basare sulle mosse generate.
	 */
	
//	public int valuta(ScacchieraBit stato);
	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec);
	
	public int valuta(ScacchieraBit stato, int giocatore);
	
	public int valuta(ScacchieraBit stato, ArrayList<Mossa> mosse, int giocatore);
}
