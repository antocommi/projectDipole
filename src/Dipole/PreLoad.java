package Dipole;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PreLoad {

	private ArrayList<ArrayList<Mossa>> listaMosse;

	private final static String[] directions = { "NORD     ", "SUD      ", "NORD-EST ", "SUD-OVEST", "SUD-EST  ",
			"NORD-EST ", "EST      ", "OVEST    " };

	private int SIZE = 8;

	public PreLoad(String path, ScacchieraBit s) {
//		Togliere il commento per generare il file delle combinazioni!
//		generaListaMossa(s, path);
		listaMosse = carica(path);
	}

	public void generaListaMossa(ScacchieraBit s, String path) {
		ArrayList<ArrayList<Mossa>> lista = new ArrayList<ArrayList<Mossa>>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
					lista.add(s.generaListaMosse(i, j));
				}
			}
		}
		salva(lista, path);
	}

	public ArrayList<Mossa> getMosseDisponibili(int i, int j) {
		ArrayList<Mossa> list = new ArrayList<Mossa>();
		for (ArrayList<Mossa> l : listaMosse) {
			for (Mossa m : l) {
				if (m.getiStart() == i && m.getjStart() == j) {
					list.add(m);
				}
			}
		}
		return list;
	}

	public static void salva(ArrayList<ArrayList<Mossa>> listaMosse, String path) {
		ObjectOutputStream o;
		try {
			o = new ObjectOutputStream(new FileOutputStream(path));
			o.writeObject(listaMosse);
			o.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ArrayList<Mossa>> getListaMosse() {
		return listaMosse;
	}

	@SuppressWarnings("resource")
	public static ArrayList<ArrayList<Mossa>> carica(String path) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(path));
			return (ArrayList<ArrayList<Mossa>>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void toStringCombinazioni() {
		System.out.println("Lista Combinazioni Possibili");
		System.out.println("");
		int cont = 0;
		for (ArrayList<Mossa> l : listaMosse) {
			for (Mossa m : l) {
				System.out.println(directions[m.getDirection()] + "   " + m.getiStart() + "," + m.getjStart() + "   "
						+ m.getiEnd() + "," + m.getjEnd());
				cont++;
			}
		}
		System.out.println("");
		System.out.println("Totale Combinazioni: " + cont);
	}

	public void toStringCombinazioni(ArrayList<Mossa> l) {
		System.out.println("");
		int cont = 0;
		for (Mossa m : l) {
			System.out.println(directions[m.getDirection()] + "   " + m.getiStart() + "," + m.getjStart() + "   "
					+ m.getiEnd() + "," + m.getjEnd());
			cont++;
		}
		System.out.println("");
		System.out.println("Totale Combinazioni: "+ cont);
	}

	public static void main(String[] args) {
		ScacchieraBit s = new ScacchieraBit();
		PreLoad p = new PreLoad("tmp.txt", s);
//		p.toStringCombinazioni();
		int i = 2;
		int j = 5;
		ArrayList<Mossa> l = p.getMosseDisponibili(i, j);
		System.out.println("Combinazioni per "+i+","+j);
		p.toStringCombinazioni(l);
	}

}
