package Dipole;

import java.util.Random;

public class Zobrist {

	private static final long[] zobrist;
	// hash to be used to compare two board states
	private long zhash;

	static {
		Random random = new Random();
		zobrist = new long[8 * 8];
		for (int i = 0; i < zobrist.length; i++) {
			zobrist[i] = random.nextLong();
		}
	}

	public Zobrist() {
		this.zhash = 0;
	}

	public Zobrist(long zhash) {
		this.zhash = zhash;
	}

	public void selezionaCella(int x, int y) {
		zhash ^= zobrist[x * 8 + y]; // prima: ScacchieraBit.scacchiera.getIndex(x,y)
	}

	public long[] getZobrist() {
		return zobrist;
	}

	public long getHashCodeCella(int i, int j) {
		selezionaCella(i,j);
		return zhash;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Zobrist && (((Zobrist) o).zhash == zhash);
	}

	@Override
	public int hashCode() {
		return (int) zhash;
	}

	@Override
	public String toString() {
		return "" + zhash;
	}

	public static void main(String[] args) {
		Zobrist z = new Zobrist();
		for (int i = 0; i < z.getZobrist().length; i++) {
			System.out.println(zobrist[i]);
		}
	}
}