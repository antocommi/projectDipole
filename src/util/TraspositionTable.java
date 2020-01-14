package util;

import Dipole.TTElement;

public class TraspositionTable {

	private TTElement[] table;
	private int size;

	// TODO: Valutare cosa inserire in TTElement, ad esempio la chiave.

	public TraspositionTable(int size) {
		super();
		this.size = size;
		table = new TTElement[size];
	}

	// TODO: Sistemare il metodo equals di TTElement altrimenti NON FUNZIONA
	public boolean contains(TTElement e) {
		return table[(int) (e.getKey() % size)].equals(e);
	}

	private TTElement replace(TTElement oldValue, TTElement newValue) {
		if (oldValue == null)
			return newValue;
		if (oldValue.getM().length >= newValue.getM().length)
			return oldValue;
		return newValue;
	}

	public void put(long key, TTElement value) {
		// TODO: Replacement scheme - Ora politica LIFO
		TTElement old = table[(int) (key % size)];
		table[(int) (key % size)] = replace(old, value);
	}

	public TTElement get(long key) {
		TTElement e = table[(int) (key % size)];
		return e.getKey() == key ? e : null;
	}

}
