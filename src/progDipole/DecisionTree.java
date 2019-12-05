package progDipole;

import java.util.Random;

public class DecisionTree {
	
	
	
	private static String calcolaLettera(int nLettera) {
		return "ABCDEFGH".substring(nLettera,nLettera+1);
	}
	
	public static void main(String[] args) {
		Scacchiera s = new Scacchiera();
		Random r= new Random();
		int posizione1= r.nextInt(8);
		int posizione2= r.nextInt(8);
		int dir = r.nextInt(8);
		String pos =calcolaLettera(posizione1) + posizione2;
		int cont=10;
		s.muovi("H5", s.NORTHWEST, 3);
		while(cont>0) {
				if(!s.muovi(pos,1,1))
			{
				posizione1= r.nextInt(8);
				posizione2= r.nextInt(8);
				dir = r.nextInt(8);
				pos =calcolaLettera(posizione1) + posizione2;
			}
			s.muovi(pos,1,1);
			s.stampaScacchiera();
			cont--;
		}
	}

}
