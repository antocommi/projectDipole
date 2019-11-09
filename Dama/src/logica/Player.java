package logica;

public class Player{
	
	public Gioco g;
	
	public Player(Gioco g){ 
		this.g= g; 
	}
	
	/**Riceve una mossa e controlla innanzi tutto se � possibile mangiare, altrimenti controlla se andando nella direzione indicata da mossa
	 * la pedina verra soffiata oppure se � possibile avanzare ed effettua la prima mossa possibile.
	 * @param Mossa m
	 * @return false se nn vi e stata alcuna modifica sulla scacchiera.
	 */
	public boolean muovi(Mossa m){ 
		
		if(!g.deveRimangiare && g.mossaPossibile(m)){
			g.mossaCorrente=m;	
			if(g.s.aChiTocca== g.s.BIANCO && g.s.colore(g.s.contenuto(m.partenza.riga, m.partenza.colonna))== g.s.NERO) return false;  
			if(g.s.aChiTocca== g.s.NERO && g.s.colore(g.s.contenuto(m.partenza.riga, m.partenza.colonna))== g.s.BIANCO) return false;
			if(g.ePossibileMangiare(m)){
				g.mangia(m);
				
				if(g.s.aChiTocca== g.s.BIANCO){ 
					g.s.pezziNeriMangiati++;
					g.s.mosse++;
				}else{
					g.s.pezziBianchiMangiati++;
				}
				
				if(g.ePossibileMangiareAncora(m)){
					g.deveRimangiare=true;
					g.vecchiaMossa=m;
					return true;
				}
				else{
					g.partitaFinita();
					g.toccaAllAltro();
					return true;
				}
				
			}
			else{
	            if(g.ePossibileSoffiare(m.partenza)){
	                g.soffia(m.partenza);
	                //soffia la ns pedina/dama
	                if(g.s.aChiTocca== g.s.BIANCO){
	                	g.s.pezziBianchiMangiati++;
	        //        	g.mosseBianco++;
	                }
					else{
						g.s.pezziNeriMangiati++;
					}
	                g.partitaFinita();
	                g.toccaAllAltro();
	                return true;
	            }
	            if(g.scansioneSoffio() ){
	            	//soffia sempre un pezzo
	            	if(g.s.aChiTocca== g.s.BIANCO){
	            		g.s.pezziBianchiMangiati++;
	         //   		g.mosseBianco++;
	            	}
					else{
						g.s.pezziNeriMangiati++;
					}
	            	g.partitaFinita();
	            	g.toccaAllAltro();
	                return true;
	            } 
	            if(g.ePossibileAvanzare(m)){
	                g.avanza(m);
	                if(g.s.aChiTocca== g.s.BIANCO) g.s.mosse++;
	                g.partitaFinita();
	                g.toccaAllAltro();
	                return true;
	            }
	                
	        }
	        return false;
		}
		else if(g.mossaPossibile(m) && g.ePossibileMangiare(m)){
			if(m.partenza.riga==g.vecchiaMossa.arrivo.riga && m.partenza.colonna==g.vecchiaMossa.arrivo.colonna){
				g.mossaCorrente=m;
				g.mangia(m);
				if(g.s.aChiTocca== g.s.BIANCO) g.s.pezziNeriMangiati++;
				else g.s.pezziBianchiMangiati++;
				
				if(g.ePossibileSoffiare(m.arrivo)){ 
					g.vecchiaMossa=m;
					return true;
				}
				else{ 
					g.deveRimangiare=false;
					g.partitaFinita();
					g.toccaAllAltro();
					return true;
				}
			}
			return false;
		}
		return false;
	}
	

}

