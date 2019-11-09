package logica;


import java.io.File;

import javax.swing.JOptionPane;

import grafica.Dama;

public class _Start_ {
		
	public static void main(String[] args){
		Gioco g= new Gioco();
        @SuppressWarnings("unused")
		Dama d=new Dama(g);
        salvataggi(); 
	}
	
	
	private static void salvataggi(){
		try{
			File f=new File("./salvataggi");
			if(!f.exists()){
				f.mkdirs();
			}
		}catch(Exception e){JOptionPane.showMessageDialog(null,"Non � stato possibile creare la cartella salvataggi!!!");}
		
	}
	
}

