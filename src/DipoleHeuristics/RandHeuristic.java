package DipoleHeuristics;

import java.util.ArrayList;

import Dipole.Mossa;
import Dipole.ScacchieraBit;
import java.util.Random;
public class RandHeuristic implements HeuristicInterface{

	private Random generator;
	
	public RandHeuristic() {
		super();
		generator = new Random(0l);
	}

	@Override
	public int valuta(ScacchieraBit stato) {
		int res = generator.nextInt(10);
		return res;
	}

	@Override
	public int valuta(ScacchieraBit stato, ArrayList<Mossa> mosse) {
		return valuta(stato);
	}
	
	

}
