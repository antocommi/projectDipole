package logica;

import java.io.*;
import java.util.*;

public class Classifica implements Serializable 
{
	
	
	private static final long serialVersionUID = 1L;
	public String nome;
	public int mosseAttuali;
	public LinkedList<String> classifica= new LinkedList<String>();
	private int[] mosseDaRecord= new int[15];
	private final String dir="./salvataggi/classifica";
	File file= new File("classifica");
	int indiceNuovoRecord;
	int size=0;
	
	
	
	/** Controlla se e' stato effettuato un nuovo record.
	 * 	@param mosse: numero di mosse effettuate per terminare la partita
	 */
	public boolean nuovoRecord(int mosse)
	{
		for(int i=0; i<mosseDaRecord.length; i++)
		{
			if(mosseDaRecord[i]==0 || mosse < mosseDaRecord[i])
			{
				indiceNuovoRecord=i;
				if(size< mosseDaRecord.length)
				{
					size++;
				}
				aggiornaClassifica();
				mosseDaRecord[i]=mosse;
				mosseAttuali=mosse;
				return true;
			}
		}
		return false;
		
	}
	
	
	
	/**Aggiunge un record in classifica.
	 * @param nome
	 */
	public void aggiungiRecord(String nome)
	{
		classifica.add(indiceNuovoRecord,toString(nome,mosseAttuali));
		while(classifica.size()>size)
			classifica.remove(classifica.size()-1);
		salvaClassifica();
	}
	
	
	
	/**Aggiorna la classifica locale contenuta in un array.
	 */
	public void aggiornaClassifica()
	{
		for(int i=size-1;i>indiceNuovoRecord;i--){
			mosseDaRecord[i]=mosseDaRecord[i-1];
		}
	}
	
	
	
	/** Serializza l'oggetto.
	 */
	private void salvaClassifica()
	{
		  try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dir));
				oos.writeObject(this);
				oos.close();
		  } catch (IOException e) {
				e.printStackTrace();
		  }
	  
	
	}
	
	/** Deserializza l'oggetto se esiste
	 *  @return true se il file esiste e l'ha ripristinato
	 */
	
	 public boolean ripristina() {
		  ObjectInputStream ois;
		  Classifica c=null;
		  try {
			  ois = new ObjectInputStream(new FileInputStream(dir));
			  c= ((Classifica)ois.readObject());
			  ois.close();
		  } catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (ClassNotFoundException e) {
				return false;
			}
			this.mosseDaRecord= c.mosseDaRecord;
			this.classifica= c.classifica;
			this.size= c.size;
			return true;
	  }
	
	
	 public String toString(String nome,int mosse){
		 	this.nome=nome; this.mosseAttuali=mosse;
			return (this.nome+ "   "+mosseAttuali+ " mosse");
	}		

}
