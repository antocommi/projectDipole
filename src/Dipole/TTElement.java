package Dipole;

public class TTElement {

	private long key;
	private int depth;
	private int value;
	private Mossa[] m;
	
	public TTElement(long key, int depth, int value, Mossa[] mosse, int indexBest) {
		this.key = key;
		this.depth = depth;
		this.value = value;
		if (mosse != null) {
			this.m = new Mossa[mosse.length];
			for (int i = 1; i <= indexBest; i++) {
				this.m[i] = mosse[i - 1];
			}
			for (int i = indexBest + 1; i < mosse.length; i++) {
				if (mosse[i] == null)
					break;
				this.m[i] = mosse[i];
			}
			this.m[0] = mosse[indexBest];
		}
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

	public Mossa[] getM() {
		return m;
	}

	public void setM(Mossa[] m) {
		this.m = m;
	}

}
