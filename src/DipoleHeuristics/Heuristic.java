package DipoleHeuristics;

import java.util.ArrayList;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class Heuristic implements HeuristicInterface {

	@Override
	public int valuta(ScacchieraBit stato) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int valuta(ScacchieraBit stato, ArrayList<Mossa> mosse, int giocatore) {
		int euristica=0;
		int nPedineCurrent;
		int nPedineAdversary;
		if(giocatore == 0) {
			nPedineCurrent = stato.getNumeroStackGiocatore(giocatore);	
			nPedineAdversary = stato.getNumeroStackGiocatore(1);
		}
		else {
			nPedineCurrent = stato.getNumeroStackGiocatore(giocatore);	
			nPedineAdversary = stato.getNumeroStackGiocatore(0);
		}
		if(nPedineCurrent > nPedineAdversary) {
			return 10;
		}
		else {
			return -10;
		}
		
		
		
		
	}

}
