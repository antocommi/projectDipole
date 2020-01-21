package DipoleHeuristics;

import java.util.ArrayList;
import java.util.Random;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class NaiveHeuristic implements HeuristicInterface {

//	@Override
//	public int valuta(ScacchieraBit stato) {
//		int k=0;
//		for(int i=0;i<stato;i++) {
//			
//		}
//		return 0;
//	}

	@Override
	public int valuta(ScacchieraBit stato, ArrayList<Mossa> mosse, int giocatore) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int perturbazioneRandom() {
		Random r = new Random();
		return r.nextInt(100)-50;
	}
	
	@Override
	public int valuta(ScacchieraBit stato, int giocatore) {
		int altroGiocatore = giocatore == 1 ? 0 : 1; // si può calcolare da stato TODO
		int k = 0, nStack = stato.getNumeroStackGiocatore(altroGiocatore);
		byte[] listaPedine = stato.getListaPosizioni(altroGiocatore);
		for (int i = 0; i < nStack; i++) {
			k += stato.getScacchiera().getValue((int) listaPedine[i]);
		}
		return k;
	}

}
