package Dipole;

import java.util.Random;

public class Zobrist {

	private static final long[] zobrist;
	// hash to be used to compare two board states
	private long zhash;
	private final int BLACK = 1;
	private final int WHITE = 0;

	static {
		Random random = new Random();
		zobrist = new long[8 * 8];
		for (int i = 0; i < zobrist.length; i++) {
			zobrist[i] = random.nextLong();
		}
	}

	public Zobrist() {
		zhash = 0;
	}

	public Zobrist(long zhash) {
		this.zhash = zhash;
	}

	public void toggleStone(int x, int y) {
		zhash ^= zobrist[ScacchieraBit.scacchiera.getIndex(x, y)];
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
		for (int i = 0; i < z.zobrist.length; i++) {
			System.out.println(zobrist[i]);
		}
	}
}