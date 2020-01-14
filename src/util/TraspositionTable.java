package util;

import Dipole.TTElement;

public class TraspositionTable{
	
	private TTElement[] table;
	private int size;
	
	//TODO: Valutare cosa inserire in TTElement, ad esempio la chiave. 
	
	public TraspositionTable(int size){
		super();
		this.size = size;
		table = new TTElement[size];
	}

	public void put(long key, TTElement value) {
		// TODO: Replacement scheme - Ora politica LIFO
		table[(int) (key % size)] = value;
	}
	
	public TTElement get(long key) {
		return table[(int) (key % size)];
	}
}
