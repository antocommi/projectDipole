package progDipole;

import java.util.*;

public interface ScacchieraInterface {
	
	public boolean muovi(String pos_iniziale, int dir, int nCelleMove);
	
	public void verificaMosseAmm(int x, int y);
	
	public int checkVittoria();
	
	public void stampaScacchiera();
	
	public LinkedList<Mossa> getListaMosse(int k);
}
