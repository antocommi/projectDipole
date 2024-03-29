package Dipole;

import java.util.ArrayList;

public class TTElement {

	private long key;
	private int depth;
	private int value;
	private ArrayList<Mossa> m;

	public TTElement(long key, int depth, int value, ArrayList<Mossa> mosse, int indexBest) {
		this.key = key;
		this.depth = depth;
		this.value = value;
		this.m = mosse;
//		TODO: DA SISTEMARE CON ARRAYLIST
//		if (mosse != null) {
//			this.m = new Mossa[mosse.length];
//			for (int i = 1; i <= indexBest; i++) {
//				this.m[i] = mosse[i - 1];
//			}
//			for (int i = indexBest + 1; i < mosse.length; i++) {
//				if (mosse[i] == null)
//					break;
//				this.m[i] = mosse[i];
//			}
//			this.m[0] = mosse[indexBest];
//		}
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public ArrayList<Mossa> getM() {
		return m;
	}

	public void setM(ArrayList<Mossa> m) {
		this.m = m;
	}

	public Mossa getBestMove(Mossa[] mosse) {
		return mosse[0];
	}

}
