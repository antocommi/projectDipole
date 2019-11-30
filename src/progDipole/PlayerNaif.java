package progDipole;

import java.util.Random;
import java.util.Scanner;

public class PlayerNaif{
	
	private static String calcolaLettera(int nLettera) {
		return "ABCDEFGH".substring(nLettera,nLettera+1);
	}
	
	public static void main(String[] args) {
		ScacchieraMatrix s = new ScacchieraMatrix();
		Random r= new Random();
		int[] v = new int[3];
		s.verificaMosseAmm(7,4);
		v[0] = s.getBASE().size();
		v[1] = s.getMERGE().size();
		v[2] = s.getCAPTURE().size();
		int i = r.nextInt(3);
		if( v[i] == 0 ) i = (i+1) % 3;
		if( v[i] == 0 ) i = (i+1) % 3;
	
	
		int k = r.nextInt(v[0]);
		
		Mossa m = new Mossa(1,1,1,1);
		
		if(i==0) m = s.getBASE().get(k);
		else if(i==1) m = s.getMERGE().get(k);
		else if(i==2) m = s.getCAPTURE().get(k);
		
		
		System.out.println(m);
		while(true) {
			boolean turnoBianco=true;
			String posB= "H5";
			s.muovi(posB,m.getDirection(),m.getSpostamento());
			s.stampaScacchiera();
			turnoBianco=false;
			Scanner sc= new Scanner(System.in);
			System.out.println("inserici posizione iniziale");
			String posN = sc.nextLine();
			System.out.println("Inserire direzione:");
			int dir= sc.nextInt();
			System.out.println("Inserire spostamento:");
			int spost = sc.nextInt();
			s.muovi(posN,dir,spost);
		}
		
		
		
	}
}
