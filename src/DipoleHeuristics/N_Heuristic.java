package DipoleHeuristics;

import java.util.HashMap;
import java.util.Random;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class N_Heuristic implements HeuristicInterface {

	private int[] v = { 1, 7, 8, 14, 17, 23, 24, 30, 33, 39, 40, 46, 49, 55, 56, 62 };
	private HashMap<Integer, Integer> POSIZIONI_BORDI;
	private Random r = new Random(10);

	public N_Heuristic() {
		POSIZIONI_BORDI = new HashMap<Integer, Integer>();
		for (int i : v) {
			POSIZIONI_BORDI.put(i, i);
		}
	}

	@Override
	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec) {
		// TODO Auto-generated method stub
		return perturbazioneRandom();
	}

	@SuppressWarnings("unused")
	private int perturbazioneRandom() {
		int soglia = r.nextInt(100);
		if (soglia > 60 && soglia < 90) {
			return r.nextInt(16) - 6;
		}
		if (soglia > 90) {
			return r.nextInt(20) - r.nextInt(10);
		}
		if (soglia < 60) {
			return r.nextInt(6) - 3;
		}
		return r.nextInt(12) - 4;
	}

}
