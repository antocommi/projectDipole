package logica;


/**Intelligenza artificiale
 */
public class IA  {
	
	public Gioco g;
	
	public IA(Gioco g)
	{
		this.g= g;
	}
	
	
	
	/**IA per la difficolt� facile
	 */
	public void muoviIA1 (){
		if(g.s.aChiTocca==g.s.NERO){
			g.scansione();
			System.out.println("mosse ke possono avanzare "+g.mosseChePossonoAvanz);
			System.out.println("mosse ke possono essere mang dopo avanz"+g.mosseChePossonoEssMangDopoAvanz);
			System.out.println("mosse ke possono mangiare "+g.mosseChePossonoMang);
			System.out.println("mosse ke possono essere mang "+g.mosseChePossonoEssMang);
			
			if(g.partitaFinita())	return;
			
			if(!g.mosseChePossonoMang.isEmpty()){
				if(!g.mosseChePossonoEssMang.isEmpty() && g.mosseChePossonoMang.size()>g.mosseChePossonoEssMang.size()){
					g.mosseChePossonoMang.removeAll(g.mosseChePossonoEssMang);
					
				}
				if(g.ePossibileMangiare(g.mosseChePossonoMang.getFirst())){
				
					int pezziMangiati = g.mangiaAutomatico(g.mosseChePossonoMang.getFirst());
					g.s.pezziBianchiMangiati += pezziMangiati;
					g.partitaFinita();
					g.toccaAllAltro();
				}
				return;
			}
			if(!g.mosseChePossonoAvanz.isEmpty()){
				if(!g.mosseChePossonoEssMangDopoAvanz.isEmpty() && g.mosseChePossonoAvanz.size()>g.mosseChePossonoEssMangDopoAvanz.size()){
					g.mosseChePossonoAvanz.removeAll(g.mosseChePossonoEssMangDopoAvanz);
				}
				Mossa casuale=g.mosseChePossonoAvanz.get(g.mossaCasuale(g.mosseChePossonoAvanz));
				if(g.ePossibileAvanzare(casuale)){
					g.avanza(casuale);
					g.toccaAllAltro();
				}
				return;
			}
		}
			
		return;	
	}
	
	
	
	/**IA per la difficolt� difficile
	 */
	public void muoviIA2(){
		if(g.s.aChiTocca==g.s.NERO){
			g.scansione();
			System.out.println("mosse ke possono avanzare "+g.mosseChePossonoAvanz);
			System.out.println("mosse ke possono essere mang dopo avanz"+g.mosseChePossonoEssMangDopoAvanz);
			System.out.println("mosse ke possono mangiare "+g.mosseChePossonoMang);
			System.out.println("mosse ke possono essere mang "+g.mosseChePossonoEssMang);
			System.out.println("caselle ke possono essere mangiate :"+g.caselleChePossonoEssMang);
			
			if(g.partitaFinita())	return;

			if(!g.mosseChePossonoMang.isEmpty()){
				if(!g.mosseChePossonoEssMang.isEmpty() && g.mosseChePossonoMang.size()>g.mosseChePossonoEssMang.size()){
					g.mosseChePossonoMang.removeAll(g.mosseChePossonoEssMang);
				}
				if(g.ePossibileMangiare(g.mosseChePossonoMang.getFirst())){
					int pezziMangiati = g.mangiaAutomatico(g.mosseChePossonoMang.getFirst());
					g.s.pezziBianchiMangiati += pezziMangiati;
					g.partitaFinita();
					g.toccaAllAltro();
				}
				return;
			}
			
			if(!g.mosseChePossonoAvanz.isEmpty()){
				if(!g.caselleChePossonoEssMang.isEmpty()){
					for(int i=0;i<g.mosseChePossonoAvanz.size();i++){
						for(int j=0;j<g.caselleChePossonoEssMang.size();j++){
							g.puoEssereMangiato(g.caselleChePossonoEssMang.get(j));
							if(g.mosseChePossonoAvanz.get(i).arrivo.equals(g.simulaMangia(g.mangia))){
								g.avanza(g.mosseChePossonoAvanz.get(i));
								g.toccaAllAltro();
								return;
							}
							if(g.mosseChePossonoAvanz.get(i).partenza.equals(g.caselleChePossonoEssMang.get(j))){
								g.avanza(g.mosseChePossonoAvanz.get(i));
								g.toccaAllAltro();
								return;
							}	
							
						}
					}
				}
				
				if(!g.mosseChePossonoEssMangDopoAvanz.isEmpty() && g.mosseChePossonoAvanz.size()>g.mosseChePossonoEssMangDopoAvanz.size()){
					g.mosseChePossonoAvanz.removeAll(g.mosseChePossonoEssMangDopoAvanz);
				}
				
				for(int i=0;i<g.mosseChePossonoAvanz.size();i++){
					if(g.s.bordoOpposto(g.mosseChePossonoAvanz.get(i).arrivo, g.s.aChiTocca) && g.s.contenuto(g.mosseChePossonoAvanz.get(i).partenza)!=g.s.DAMA_NERA){
						g.avanza(g.mosseChePossonoAvanz.get(i));
						g.toccaAllAltro();
						return;
					}
				
					if(g.ePossibileSoffiare(g.simulaAvanza(g.mosseChePossonoAvanz.get(i)))){
						g.avanza(g.mosseChePossonoAvanz.get(i));
						g.toccaAllAltro();
						return;
					}
				
				}
				
				if(g.ePossibileAvanzare(g.mosseChePossonoAvanz.getLast())){
					g.avanza(g.mosseChePossonoAvanz.getLast());
					g.toccaAllAltro();
				}
				return;
			}
			
			return;	
		}
	}
}



