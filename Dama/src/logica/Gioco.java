package logica;

import java.util.Collection;
import java.util.LinkedList;


/**Questa classe definisce i possibili movimenti durante una partita.
 * 
 * @author Pino Pinguino , Matteo Lavazza
 *
 */
public class Gioco {
	
	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;
	
	public Scacchiera s= new Scacchiera();
	protected Scacchiera scacchieraCopiata= new Scacchiera();
	protected LinkedList<Casella> caselleChePossonoEssMang= new LinkedList<Casella>();
	protected LinkedList<Mossa> mosseChePossonoEssMang= new LinkedList<Mossa>();
	protected LinkedList<Mossa> mosseChePossonoAvanz= new LinkedList<Mossa>();
	protected LinkedList<Mossa> mosseChePossonoEssMangDopoAvanz= new LinkedList<Mossa>();  
	public final int NORD_EST=1;
	public final int SUD_EST=2;
	public final int SUD_OVEST=3;
	public final int NORD_OVEST=4;
	protected int direzioneMangia;  //direzione verso la quale e possibile mangiare/soffiare aggiornata in ePossibileSoffiare(c)
	protected Mossa mossaCorrente;
	public Mossa vecchiaMossa;
	public boolean deveRimangiare=false;
	public int vincitore;
	public Mossa mangia;//aggiunto
	protected int numVolteMangiaVecchiaMossa=0;
	
	
	
	private void azzeraListe(){
		caselleChePossonoEssMang.clear();
		mosseChePossonoAvanz.clear();
		mosseChePossonoEssMang.clear();
		mosseChePossonoMang.clear();
		mosseChePossonoEssMangDopoAvanz.clear();
	}
	
	

	/**Controlla se la mossa che si vuole effettuare � verso avanti rispettivamente per la pedina nera e la pedina bianca.
	 * Per quanto riguarda la dama non effettua nessun controllo dato che pu� andare in tutte le direzioni.
	 * @param Mossa m
	 * @return boolean 
	 */
	protected boolean eVersoAvanti(Mossa m){
		if(s.contenuto(m.partenza)== s.PEDINA_BIANCA){
			if(m.direzione==NORD_EST || m.direzione==NORD_OVEST) return true;               
		}
		if(s.contenuto(m.partenza)== s.PEDINA_NERA){
			if(m.direzione== SUD_EST || m.direzione== SUD_OVEST) return true;
		}
		if(s.contenuto(m.partenza)== s.DAMA_NERA || s.contenuto(m.partenza)==s.DAMA_BIANCA) return true;
		return false;
	}
	
	
	
	protected Casella casellaAdiacente(Mossa m){
		if(s.eDentro(m.arrivo)){
			if(m.direzione==NORD_EST){
				return new Casella(m.partenza.riga-1,m.partenza.colonna+1);
			}
			if(m.direzione==SUD_EST ){
				return new Casella (m.partenza.riga+1,m.partenza.colonna+1);
			}
			if(m.direzione==SUD_OVEST ){
				return new Casella(m.partenza.riga+1,m.partenza.colonna-1);
			}
			if(m.direzione==NORD_OVEST){
				return new Casella(m.partenza.riga-1,m.partenza.colonna-1);
			}
		}
		return null;

	}
	
	
	
	public boolean mossaPossibile(Mossa m){
		if(m==null) return false;
		if(!eVersoAvanti(m)) return false;
		if(!s.eDentro(m.arrivo)) return false;
		if(s.colore(s.contenuto(m.arrivo))==s.aChiTocca)return false;
		if(s.contenuto(m.arrivo)!=s.VUOTA && s.colore(s.contenuto(m.arrivo))!=s.aChiTocca && s.contenuto(casellaAdiacente(new Mossa(m.arrivo,m.direzione)))!=s.VUOTA) return false;
		return true;
	}
	
	
	
	protected void toccaAllAltro(){
		if(s.aChiTocca==s.BIANCO) s.aChiTocca=s.NERO;
		else s.aChiTocca= s.BIANCO;
	}
	
	
	
	public Mossa scansione(){
		azzeraListe();
		for(int i=0; i<s.DIM_LATO; i++){
			for(int j=0; j<s.DIM_LATO; j++){
				if(s.colore(s.contenuto(i,j))==s.aChiTocca){
					Casella c= new Casella(i,j);
					pezziChePossonoEssereMangiati(c);
					quanteVolteEPossileMangiare(c);//mosse ke possono mangiare, mosse ke possono essere mangiate dopo mangia
					pezziChePossonoAvanzare(c);
					
				}
			}
		}
		return null;
	}
	
	
	
	protected boolean pezziBloccati(){
		scansione();
		if(mosseChePossonoAvanz.isEmpty() && mosseChePossonoMang.isEmpty()) return true;
		return false;
	}
	
	
	
	private Scacchiera copiaScacchiera(){
		Scacchiera ns= new Scacchiera();
		for(int i=0; i<s.DIM_LATO; i++){
			for(int j=0; j<s.DIM_LATO; j++){
				ns.contenutoCaselle[i][j]=this.s.contenutoCaselle[i][j];
			}
		}
		return ns;
	}
	

	
	protected int mossaCasuale(Collection<Mossa> c){
		int i=c.size();
		return (int) (Math.random()*i);
	}
	
	
	
	public boolean partitaFinita(){
		if(pezziBloccati()){
			if(s.aChiTocca== s.NERO && s.pezziNeriMangiati==12){
				vincitore= s.BIANCO;
				return true;
			}
			else if(s.aChiTocca==s.BIANCO && s.pezziBianchiMangiati==12){
				vincitore= s.NERO;
				return true;
			}
			else {
				vincitore= s.NON_COLORE;
				return true;
			}
		}
		return false;
	}
	
	
	
	/**Controlla se il pezzo contenuto nella casella indicata in mossa ha la possibilit� di mangiare nella direzione indicata, 
	 * prevenendo tutte le possibili situazioni di trabocco.
	 * @param Mossa m
	 * @return boolean 
	 */
	protected boolean ePossibileMangiare(Mossa m){// funziona
		if( s.contenuto(m.partenza) == s.VUOTA) return false;
		
		//pedina bianca
		if( s.contenuto(m.partenza) == s.PEDINA_BIANCA){
			if (( m.direzione==NORD_OVEST) && s.eDentro(m.partenza.riga-2,m.partenza.colonna-2) && (s.contenuto(m.partenza.riga-2,m.partenza.colonna-2))==s.VUOTA && s.contenuto(m.partenza.riga-1, m.partenza.colonna-1)== s.PEDINA_NERA)
				return true;
			if (( m.direzione==NORD_EST) && s.eDentro(m.partenza.riga-2,m.partenza.colonna+2) &&(s.contenuto(m.partenza.riga-2,m.partenza.colonna+2))==s.VUOTA && s.contenuto(m.partenza.riga-1, m.partenza.colonna+1)== s.PEDINA_NERA)
				return true;
		}
		//pedina nera
		if( s.contenuto(m.partenza) == s.PEDINA_NERA){
			if (( m.direzione==SUD_OVEST) && s.eDentro(m.partenza.riga+2,m.partenza.colonna-2) && (s.contenuto(m.partenza.riga+2,m.partenza.colonna-2))==s.VUOTA && s.contenuto(m.partenza.riga+1, m.partenza.colonna-1)== s.PEDINA_BIANCA)
				return true;
			if (( m.direzione==SUD_EST) && s.eDentro(m.partenza.riga+2,m.partenza.colonna+2) && (s.contenuto(m.partenza.riga+2,m.partenza.colonna+2))==s.VUOTA && s.contenuto(m.partenza.riga+1, m.partenza.colonna+1)== s.PEDINA_BIANCA)
				return true;
			return false; //prova
		}
		//dama bianca
		if( s.contenuto(m.partenza) == s.DAMA_BIANCA){
			if (( m.direzione==SUD_OVEST) && s.eDentro(m.partenza.riga+2,m.partenza.colonna-2) && (s.contenuto(m.partenza.riga+2,m.partenza.colonna-2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga+1, m.partenza.colonna-1))== s.NERO)
				return true;
			if (( m.direzione==SUD_EST) && s.eDentro(m.partenza.riga+2,m.partenza.colonna+2) && (s.contenuto(m.partenza.riga+2,m.partenza.colonna+2))== s.VUOTA && s.colore(s.contenuto(m.partenza.riga+1, m.partenza.colonna+1))== s.NERO)
				return true;
			if (( m.direzione==NORD_OVEST) && s.eDentro(m.partenza.riga-2,m.partenza.colonna-2) && (s.contenuto(m.partenza.riga-2,m.partenza.colonna-2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga-1, m.partenza.colonna-1))== s.NERO)
				return true;
			if (( m.direzione==NORD_EST) && s.eDentro(m.partenza.riga-2,m.partenza.colonna+2) &&(s.contenuto(m.partenza.riga-2,m.partenza.colonna+2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga-1, m.partenza.colonna+1))== s.NERO)
				return true;
		}
		//dama nera
		if( s.contenuto(m.partenza) == s.DAMA_NERA){
			if (( m.direzione==SUD_OVEST) && s.eDentro(m.partenza.riga+2,m.partenza.colonna-2)&&(s.contenuto(m.partenza.riga+2,m.partenza.colonna-2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga+1, m.partenza.colonna-1))== s.BIANCO)
				return true;
			if (( m.direzione==SUD_EST) && s.eDentro(m.partenza.riga+2,m.partenza.colonna+2) && (s.contenuto(m.partenza.riga+2,m.partenza.colonna+2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga+1, m.partenza.colonna+1))== s.BIANCO)
				return true;
			if (( m.direzione==NORD_OVEST) &&s.eDentro(m.partenza.riga-2,m.partenza.colonna-2)&& (s.contenuto(m.partenza.riga-2,m.partenza.colonna-2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga-1, m.partenza.colonna-1))== s.BIANCO)
				return true;
			if (( m.direzione==NORD_EST) && s.eDentro(m.partenza.riga-2,m.partenza.colonna+2)&& (s.contenuto(m.partenza.riga-2,m.partenza.colonna+2))==s.VUOTA && s.colore(s.contenuto(m.partenza.riga-1, m.partenza.colonna+1))== s.BIANCO)
				return true;
		}
		
		return false;
		
	}
	
	
	
	/**Mangia il pezzo che si trova nella direzione indicata da Mossa senza preoccuparsi se lo puo mangiare.
	 * Da usare con ePossibileMangiare() per evitare eventuali errori o condizioni di trabocco.
	 * @param Mossa m
	 * @return int che identifica il pezzo mangiato
	 */
	protected int mangia(Mossa m){
		int pezzo=-1;
		if(m.direzione==NORD_EST){
            if(s.eDentro(m.partenza.riga-2, m.partenza.colonna+2) && s.bordoOpposto(m.partenza.riga-2, m.partenza.colonna+2, s.colore(s.contenuto(m.partenza))))
            	s.inserisci(m.partenza.riga-2, m.partenza.colonna+2, s.promossaDama(s.contenuto(m.partenza)));
           	else{
           		s.inserisci(m.partenza.riga-2, m.partenza.colonna+2, s.contenuto(m.partenza));
           		
           	}
			pezzo= s.contenuto(m.partenza.riga-1,m.partenza.colonna+1);
			s.inserisci(m.partenza.riga-1,m.partenza.colonna+1, s.VUOTA);
			s.inserisci(m.partenza.riga,m.partenza.colonna, s.VUOTA);
			m.arrivo.riga= m.partenza.riga-2;
			m.arrivo.colonna= m.partenza.colonna+2;
		}
		if(m.direzione==SUD_EST){
            if(s.eDentro(m.partenza.riga+2, m.partenza.colonna+2) && s.bordoOpposto(m.partenza.riga+2, m.partenza.colonna+2, s.colore(s.contenuto(m.partenza))))
            	s.inserisci(m.partenza.riga+2, m.partenza.colonna+2, s.promossaDama(s.contenuto(m.partenza)));
            else{
           		s.inserisci(m.partenza.riga+2, m.partenza.colonna+2, s.contenuto(m.partenza));
            }
			pezzo= s.contenuto(m.partenza.riga+1,m.partenza.colonna+1);
			s.inserisci(m.partenza.riga+1,m.partenza.colonna+1, s.VUOTA);
			s.inserisci(m.partenza.riga,m.partenza.colonna, s.VUOTA);
			m.arrivo.riga= m.partenza.riga+2;
			m.arrivo.colonna= m.partenza.colonna+2;
		}
		if(m.direzione==SUD_OVEST){
        	if(s.eDentro(m.partenza.riga+2, m.partenza.colonna-2) && s.bordoOpposto(m.partenza.riga+2, m.partenza.colonna-2, s.colore(s.contenuto(m.partenza))))
                s.inserisci(m.partenza.riga+2, m.partenza.colonna-2, s.promossaDama(s.contenuto(m.partenza)));
        	else{
        		s.inserisci(m.partenza.riga+2, m.partenza.colonna-2, s.contenuto(m.partenza));
        	}
			pezzo= s.contenuto(m.partenza.riga+1,m.partenza.colonna-1);
			s.inserisci(m.partenza.riga+1,m.partenza.colonna-1, s.VUOTA);
			s.inserisci(m.partenza.riga,m.partenza.colonna, s.VUOTA);
			m.arrivo.riga= m.partenza.riga+2;
			m.arrivo.colonna= m.partenza.colonna-2;
		}
		if(m.direzione==NORD_OVEST){
        	if(s.eDentro(m.partenza.riga-2, m.partenza.colonna-2) && s.bordoOpposto(m.partenza.riga-2, m.partenza.colonna-2, s.colore(s.contenuto(m.partenza))))
                s.inserisci(m.partenza.riga-2, m.partenza.colonna-2, s.promossaDama(s.contenuto(m.partenza)));
        	else{
        		s.inserisci(m.partenza.riga-2, m.partenza.colonna-2, s.contenuto(m.partenza));
        	}
			pezzo= s.contenuto(m.partenza.riga-1,m.partenza.colonna-1);
			s.inserisci(m.partenza.riga-1,m.partenza.colonna-1, s.VUOTA);
			s.inserisci(m.partenza.riga,m.partenza.colonna, s.VUOTA);
			m.arrivo.riga= m.partenza.riga-2;
			m.arrivo.colonna= m.partenza.colonna-2;
		}
		return pezzo;
	}
	
	
	
	protected boolean ePossibileMangiareAncora(Mossa m){ 
		if(ePossibileSoffiare(m.arrivo)) //casella di arrivo della mossa precedente
			return true;
			
		return false;
	}
	
	
	
	protected int mangiaAutomatico(Mossa m){
		int pezziMangiati=0;
		Casella c= m.partenza;
		Mossa tmp;
		while(ePossibileSoffiare(c)){
			tmp=new Mossa(c, direzioneMangia);
			mangia(tmp);
			pezziMangiati ++;
			c= tmp.arrivo;
		}
		return pezziMangiati;
	}
	
	
	
	protected Casella simulaMangia(Mossa m){
		scacchieraCopiata=copiaScacchiera();
		mangia(m);
		Casella c=m.arrivo;
		this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle;
		return c;
	}
	
	
	
	protected int quanteVolteEPossileMangiare(Casella c){
		scacchieraCopiata= copiaScacchiera(); //copia lo stato iniziale della scacchiera
		int volte=0;
		Mossa tmp=null;
		boolean ePrimaMossa=true;
		Mossa primaMossa=null;
		
		while(ePossibileSoffiare(c)){
			volte++;
			tmp=new Mossa(c,direzioneMangia); //direzione soffio nn � inizializzata
			mangia(tmp);
			if(mossaPossibile(tmp))mangia(tmp);
			
			if(ePrimaMossa){
				primaMossa=tmp;
				ePrimaMossa=false;
			}
			c= tmp.arrivo; //casella di arrivo dopo aver mangiato da gestire x i neri !!! attenzione
		}
		
		if(tmp!=null && puoEssereMangiato(tmp.arrivo)){
			
			mosseChePossonoEssMang.add(primaMossa);
		}

		if(volte>=numVolteMangiaVecchiaMossa && tmp!=null){//mette in testa le mosse con maggior possibilit� di mangiare***
			mosseChePossonoMang.addFirst(primaMossa);
			numVolteMangiaVecchiaMossa=volte;
		}
		else if(tmp!=null)
			mosseChePossonoMang.add(primaMossa);
		this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle; //ritorna la scacchiera com era prima
		return volte;
	}
	
	
	
	protected boolean puoEssereMangiato(Casella c){//non gestisce mossa possibile		

		Casella nordEst= casellaAdiacente(new Mossa(c,NORD_EST));
		if(nordEst!=null){
			Mossa SO=new Mossa(nordEst,SUD_OVEST);
	
			if(SO!=null && ePossibileMangiare(SO)){
				mangia=SO;//aggiunto
				return true;
			}
		}
			
		Casella sudEst= casellaAdiacente(new Mossa(c,SUD_EST));
		if(sudEst!=null){
			Mossa NO=new Mossa(sudEst,NORD_OVEST);
			
			if(NO!=null && ePossibileMangiare(NO)){
				mangia=NO;//aggiunto
				return true;
			}
		}
		
		Casella sudOvest= casellaAdiacente(new Mossa(c,SUD_OVEST));
		if(sudOvest!=null){
			Mossa NE=new Mossa(sudOvest,NORD_EST);
			
			if(NE!=null && ePossibileMangiare(NE)){
				mangia=NE;//aggiunto
				return true;
			}
		}
	
		
		Casella nordOvest= casellaAdiacente(new Mossa(c,NORD_OVEST));
		if(nordOvest!=null){
			Mossa SE=new Mossa(nordOvest,SUD_EST);
			
			if(SE!=null && ePossibileMangiare(SE)){
				mangia=SE;//aggiunto
				return true;
				
			}
		}
		
		return false;
	}
	
	
	
	private void pezziChePossonoEssereMangiati(Casella c){
		if(puoEssereMangiato(c) && !caselleChePossonoEssMang.contains(c)){
			caselleChePossonoEssMang.add(c);
		}
	}
	
	
	
	/**Controlla se il pezzo contenuto nella casella indicata in mossa ha la possibilit� di avanzare, prevenendo tutte
	 * le possibili situazioni di trabocco.
	 * @param Mossa m
	 * @return boolean
	 */
	protected boolean ePossibileAvanzare(Mossa m){ //funziona
		if((s.contenuto(m.partenza)== s.PEDINA_BIANCA || s.contenuto(m.partenza)== s.PEDINA_NERA) && eVersoAvanti(m)){
			if(m.direzione==NORD_EST && s.eDentro(m.partenza.riga-1, m.partenza.colonna+1)){ 
				if(s.contenuto(m.partenza.riga-1, m.partenza.colonna+1)== s.VUOTA ){
					return true;
				}
			}
			if(m.direzione==SUD_EST && s.eDentro(m.partenza.riga+1, m.partenza.colonna+1)){
				if(s.contenuto(m.partenza.riga+1, m.partenza.colonna+1)== s.VUOTA) return true;
			}
			if(m.direzione==SUD_OVEST && s.eDentro(m.partenza.riga+1, m.partenza.colonna-1)){
				if(s.contenuto(m.partenza.riga+1, m.partenza.colonna-1)== s.VUOTA) return true;
			}
			if(m.direzione==NORD_OVEST && s.eDentro(m.partenza.riga-1, m.partenza.colonna-1)){
				if(s.contenuto(m.partenza.riga-1, m.partenza.colonna-1)== s.VUOTA) return true;
			}
		}
		if(s.contenuto(m.partenza)== s.DAMA_BIANCA || s.contenuto(m.partenza)== s.DAMA_NERA ){
			if(m.direzione==NORD_EST && s.eDentro(m.partenza.riga-1, m.partenza.colonna+1)){ 
				if(s.contenuto(m.partenza.riga-1, m.partenza.colonna+1)== s.VUOTA ) return true;
			}
			if(m.direzione==SUD_EST && s.eDentro(m.partenza.riga+1, m.partenza.colonna+1)){
				if(s.contenuto(m.partenza.riga+1, m.partenza.colonna+1)== s.VUOTA) return true;
			}
			if(m.direzione==SUD_OVEST && s.eDentro(m.partenza.riga+1, m.partenza.colonna-1)){
				if(s.contenuto(m.partenza.riga+1, m.partenza.colonna-1)== s.VUOTA) return true;
			}
			if(m.direzione==NORD_OVEST && s.eDentro(m.partenza.riga-1, m.partenza.colonna-1)){
				if(s.contenuto(m.partenza.riga-1, m.partenza.colonna-1)== s.VUOTA) return true;
			}
		}
		return false;
	}
	
	
	
	protected void pezziChePossonoAvanzare(Casella c){
		
		Casella nordEst =casellaAdiacente(new Mossa(c,NORD_EST));
		Casella sudEst =casellaAdiacente(new Mossa(c,SUD_EST));
		Casella sudOvest =casellaAdiacente(new Mossa(c,SUD_OVEST));
		Casella nordOvest =casellaAdiacente(new Mossa(c,NORD_OVEST));
		
		
		Mossa NE=new Mossa(c,NORD_EST);
		if(ePossibileAvanzare(NE)){
			scacchieraCopiata= copiaScacchiera(); //copia lo stato iniziale della scacchiera
			mosseChePossonoAvanz.add(NE);
			avanza(NE);
			if(puoEssereMangiato(nordEst)){
				mosseChePossonoEssMangDopoAvanz.add(NE);
			}
			this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle; //ritorna la scacchiera com era prima
		}
		
		Mossa SE=new Mossa(c,SUD_EST);
		if(ePossibileAvanzare(SE)){
			scacchieraCopiata= copiaScacchiera(); //copia lo stato iniziale della scacchiera
			mosseChePossonoAvanz.add(SE);
			avanza(SE);
			if(puoEssereMangiato(sudEst)){
				mosseChePossonoEssMangDopoAvanz.add(SE);
			}
			this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle; //ritorna la scacchiera com era prima
		}
		
		Mossa SO=new Mossa(c,SUD_OVEST);
		if(ePossibileAvanzare(SO)){
			scacchieraCopiata= copiaScacchiera(); //copia lo stato iniziale della scacchiera
			mosseChePossonoAvanz.add(SO);
			avanza(SO);
			if(puoEssereMangiato(sudOvest)){
				mosseChePossonoEssMangDopoAvanz.add(SO);
			}
			this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle; //ritorna la scacchiera com era prima
		}
		
		
		Mossa NO=new Mossa(c,NORD_OVEST);
		if(ePossibileAvanzare(NO)){
			scacchieraCopiata= copiaScacchiera(); //copia lo stato iniziale della scacchiera
			mosseChePossonoAvanz.add(NO);
			avanza(NO);
			if(puoEssereMangiato(nordOvest)){
				mosseChePossonoEssMangDopoAvanz.add(NO);
			}
			this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle; //ritorna la scacchiera com era prima
		}
		
	}
	
	
	
	/**Fa avanzare il pezzo che si trova nella casella indicata, verso la direzione indicata senza preoccuparsi se lo pu� fare
	 * Da usare con ePossibileAvanzare() per evitare eventuali errori o eventuali condizioni di trabocco.
	 * @param Mossa m
	 */
	protected void avanza(Mossa m){ //funziona
            if(m.direzione==NORD_EST){
            	if(s.bordoOpposto(m.partenza.riga-1, m.partenza.colonna+1, s.colore(s.contenuto(m.partenza))))
                    s.inserisci(m.partenza.riga-1, m.partenza.colonna+1, s.promossaDama(s.contenuto(m.partenza)));
            	else
            		s.inserisci(m.partenza.riga-1, m.partenza.colonna+1, s.contenuto(m.partenza));
            	m.arrivo.riga= m.partenza.riga-1; 
            	m.arrivo.colonna= m.partenza.colonna+1;
            }
            if(m.direzione==SUD_EST){
            	if(s.bordoOpposto(m.partenza.riga+1, m.partenza.colonna+1, s.colore(s.contenuto(m.partenza))))
                    s.inserisci(m.partenza.riga+1, m.partenza.colonna+1, s.promossaDama(s.contenuto(m.partenza)));
            	else
            		s.inserisci(m.partenza.riga+1, m.partenza.colonna+1, s.contenuto(m.partenza));
            	m.arrivo.riga= m.partenza.riga+1; 
            	m.arrivo.colonna= m.partenza.colonna+1;
            }
            if(m.direzione==SUD_OVEST){
            	if(s.bordoOpposto(m.partenza.riga+1, m.partenza.colonna-1, s.colore(s.contenuto(m.partenza))))
                    s.inserisci(m.partenza.riga+1, m.partenza.colonna-1, s.promossaDama(s.contenuto(m.partenza)));
            	else
            		s.inserisci(m.partenza.riga+1, m.partenza.colonna-1, s.contenuto(m.partenza));
            	m.arrivo.riga= m.partenza.riga+1; 
            	m.arrivo.colonna= m.partenza.colonna-1;
            }
            if(m.direzione==NORD_OVEST){
            	if(s.bordoOpposto(m.partenza.riga-1, m.partenza.colonna-1, s.colore(s.contenuto(m.partenza))))
                    s.inserisci(m.partenza.riga-1, m.partenza.colonna-1, s.promossaDama(s.contenuto(m.partenza)));
            	else
            		s.inserisci(m.partenza.riga-1, m.partenza.colonna-1, s.contenuto(m.partenza));
            	m.arrivo.riga= m.partenza.riga-1; 
            	m.arrivo.colonna= m.partenza.colonna-1;
            }
            s.inserisci(m.partenza.riga, m.partenza.colonna, s.VUOTA);
		
	}
	
	
	
	/**Si crea una scacchiera di prova per simulare un 
	 * 
	 * @param Mossa m
	 * @return
	 */
	protected Casella simulaAvanza(Mossa m){
		scacchieraCopiata= copiaScacchiera();
		avanza(m);
		Casella c= m.arrivo;
		this.s.contenutoCaselle= scacchieraCopiata.contenutoCaselle;
		return c;
	}
	
	
	
	/**Controlla se ci troviamo nella situazione di soffio, cio� se avanziamo quando era possibile mangiare.
	 * In questo caso il pezzo contenuto nella casella indicata verr� soffiato (eliminato).
	 * @param Mossa m
	 * @return boolean
	 */
	protected boolean ePossibileSoffiare(Casella da){
		if(s.contenuto(da)==s.PEDINA_BIANCA ){  
			direzioneMangia= NORD_EST;
            Mossa NE=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(NE) ) return true;    //(mossaPossibile(NE) && 
		
			direzioneMangia= NORD_OVEST;
            Mossa NO=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(NO) ) return true;
		}
		
		if(s.contenuto(da)==s.PEDINA_NERA ){
			direzioneMangia= SUD_EST;
            Mossa SE=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(SE)) return true;
			
			
			direzioneMangia= SUD_OVEST;
            Mossa SO=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(SO)) return true;
		}
		
		if(s.contenuto(da)==s.DAMA_BIANCA){
			direzioneMangia= NORD_EST;
            Mossa NE=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(NE)) return true;
			direzioneMangia= SUD_EST;
            Mossa SE=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(SE)) return true;
			direzioneMangia= SUD_OVEST;
            Mossa SO=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(SO)) return true;
			direzioneMangia= NORD_OVEST;
            Mossa NO=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(NO)) return true;
		}
		
		if(s.contenuto(da)==s.DAMA_NERA){
			direzioneMangia= NORD_EST;
            Mossa NE=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(NE)) return true;
			direzioneMangia= SUD_EST;
            Mossa SE=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(SE)) return true;
			direzioneMangia= SUD_OVEST;
            Mossa SO=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(SO)) return true;
			direzioneMangia= NORD_OVEST;
            Mossa NO=new Mossa(da,direzioneMangia);
			if(ePossibileMangiare(NO)) return true;
		}
		return false;
		
	}
	
	
	
	/**Soffia (elimina) il pezzo che si trova nella casella indicata da Mossa.
	 * Da usare con ePossibileSoffiare() per evitare eventuali errori o eccezioni.
	 * @return int che identifica il pezzo soffiato
	 */
	protected int soffia(Casella c){//funziona
		int pezzo=s.contenuto(c);
		s.inserisci(c, s.VUOTA);
		return pezzo;
	}
	
	
	
	/**Controlla se � possibile soffiare un pezzo in una posizione qualsiasi della scacchiera.
	 * @return boolean: true se un pezzo � stato soffiato; false altrimenti.
	 */
	protected boolean scansioneSoffio(){
		
		for(int i=0;i<s.DIM_LATO;i++){
			for(int j=0;j<s.DIM_LATO; j++){
				
				if(s.colore(s.contenuto(i,j))==s.aChiTocca){
					
					Casella c= new Casella(i,j);
					if(ePossibileSoffiare(c)){
						soffia(c);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
}
