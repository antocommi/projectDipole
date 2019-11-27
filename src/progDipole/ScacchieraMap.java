package progDipole;

import java.util.BitSet;
import java.util.LinkedList;

public class ScacchieraMap implements ScacchieraInterface{

	private BitSet TTValue;
	
	public ScacchieraMap(int nBits) {
		if(nBits <= 0) throw new RuntimeException("Numero di bit minore di zero");
		TTValue = new BitSet(nBits);
	}
	
	@Override
	public boolean muovi(String pos_iniziale, int dir, int nCelleMove) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void verificaMosseAmm(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int checkVittoria() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stampaScacchiera() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LinkedList<Mossa> getListaMosse(int k) {
		// TODO Auto-generated method stub
		return null;
	}

}
