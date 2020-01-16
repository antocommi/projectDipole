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

	public boolean contains(TTElement e) {
		int key = (int) (e.getKey() % size);
		key = Math.abs(key);
		return table[key].getKey() == e.getKey();
	}

	public boolean contains(long key) {
		key = Math.abs(key);
		if(table[(int) (key % size)] == null) return false;
		return table[(int) (key % size)].getKey() == key;
	}

	private TTElement replace(TTElement oldValue, TTElement newValue) {
		if (oldValue == null)
			return newValue;
		if (oldValue.getM().size() >= newValue.getM().size())
			return oldValue;
		return newValue;
	}

	public void put(long key, TTElement value) {
		key = Math.abs(key);
		// TODO: Replacement scheme - Ora politica LIFO
		TTElement old = table[(int) (key % size)];
		table[(int) (key % size)] = replace(old, value);
	}

	public TTElement get(long key) {
		TTElement e = table[(int) (key % size)];
		return e.getKey() == key ? e : null;
	}

}
