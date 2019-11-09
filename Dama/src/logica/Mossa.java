package logica;

import java.io.*;



/**Questa classe definisce l'oggetto Mossa.
 */
public class Mossa implements Serializable{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int direzione;
	public Casella partenza= null;
	public Casella arrivo= null;
	
	
	
	/** Questo costruttore riceve la casella di partenza e la direzione verso cui ci muoviamo.
	 * @param da
	 * @param direzione
	 * @exception "direzione nulla" se la direzione passata non rientra fra le possibili direzioni.
	 */
	public Mossa(Casella partenza, int direzione){
		this (partenza.riga,partenza.colonna,direzione);
	}
	
	
	
	/** Questo costruttore riceve la riga e la colonna di partenza, la direzione verso cui ci muoviamo e 
	 * va a calcorare la casella d'arrivo.
	 * @param riga
	 * @param colonna
	 * @param direzione
	 * @exception "direzione nulla" se la direzione passata non rientra fra le possibili direzioni.
	 */
	public Mossa(int riga, int colonna, int direzione){
		if(direzione<1 || direzione>4) throw new RuntimeException("DirezioneNulla"); //da gestire dentro il main
		if(riga<0 || riga>7 || colonna<0 || colonna>7 ) throw new RuntimeException("sei fuori"); //da gestire dentro il main
		this.partenza= new Casella (riga, colonna);
		this.direzione= direzione;
		this.arrivo= ricavaDestinazione();
	}
	
	
	
	/** Questo costruttore riceve la riga e la colonna di partenza, la riga e la colonna di destinazione 
	 * e ricava la direzione della mossa.
	 * @param riga
	 * @param colonna
	 * @param riga1 : riga della casella d'arrivo
	 * @param colonna1 : colonna della casella d'arrivo
	 */
	public Mossa(int riga, int colonna, int riga1, int colonna1){
		if(riga1<riga && colonna1>colonna) direzione=1;//NORD_EST
		if(riga1>riga && colonna1>colonna) direzione=2;//SUD_EST
		if(riga1>riga && colonna1<colonna) direzione=3;//SUD_OVEST
		if(riga1<riga && colonna1<colonna) direzione=4;//NORD_EST
		if(riga<0 || riga>7 || riga1<0 ||riga1>7 || colonna<0 || colonna>7 || colonna1<0 || colonna1>7)
			throw new RuntimeException("sei fuori"); //da gestire dentro il main
		this.partenza= new Casella (riga, colonna);
		this.arrivo= new Casella(riga1,colonna1);
	}
	
	/** Questo costruttore riceve la casella di partenza e la casella di destinazione 
	 * e sfrutta il costruttore precedente Mossa(int riga, int colonna, int riga1, int colonna1).
	 */	
	public Mossa(Casella da,Casella a){
		this (da.riga, da.colonna, a.riga, a.colonna);
//		if(da==null || a==null) throw new RuntimeException("CasellaNulla");
	}
	
	
	
	/**Data una direzione sotto forma di intero restituisce la codifica corrispondente sotto forma di stringa.
	 * @return String
	 */
	public Casella ricavaDestinazione(){
		Casella tmp=null;
		if(direzione==1){
			tmp= new Casella(partenza.riga-1,partenza.colonna+1);
		}
		if(direzione==2){
			tmp= new Casella(partenza.riga+1,partenza.colonna+1);
		}
		if(direzione==3){
			tmp= new Casella(partenza.riga+1,partenza.colonna-1);
		}
		if(direzione==4){
			tmp= new Casella(partenza.riga-1,partenza.colonna-1);
		}
		return tmp;
	}
	
	
	
	private String direzioneCodificata(){
		if(direzione == 1) return ("NORD_EST");
		if(direzione == 2) return ("SUD_EST");
		if(direzione == 3) return ("SUD_OVEST");
		if(direzione == 4) return ("NORD_OVEST");
		return ("nessuna direzione");
	}
	
	
	
	/**Restituisce una stringa contenente la casella di partenza e la direzione dello spostamento.
	 * @return String
	 */
	public String toString(){
		String mossa= ("(" + partenza.riga+ ", " +partenza.colonna+ ", " +direzioneCodificata()+ ")");
		return mossa;
	}
	
}