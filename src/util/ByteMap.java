package util;

public class ByteMap {
	private int[] bitMap;
	private int mask = 0b1111;
	private int nBitIndex = 4;

	// ~ negazione
	// NUMERO DI VALORE PER INTERO: 8 se |valore|=4 bit
	public ByteMap(int capacity) {
		bitMap = new int[capacity];
	}

	public ByteMap(ByteMap b) {
		this.bitMap = new int[b.bitMap.length];
		java.lang.System.arraycopy(b.bitMap,0,this.bitMap,0,b.bitMap.length);
	}

	public void setValue(int value, int index) {
//		index � un valore compreso tra 0 e capacity*8
		int k, l;
		k = index % 8;
		l = index / 8;
		int shiftWidth = (7 - k) * 4;
		int n = bitMap[l], m = mask << shiftWidth;
		bitMap[l] = (n & ~m) | ((value << shiftWidth) & m);
	}

	public int[] getbm() {
		return bitMap;
	}

	public int getIndex(int x, int y) {
		return x * 8 + y;
	}

	public int getValue(int index) {
		int i = index % 8;
		int j = index / 8;
		int n = bitMap[j];
		return (n & (mask << (7 - i) * 4)) >>> (7 - i) * 4;
	}

	public int getNumeroPedine(int i, int j) {
		return getValue(i * 8 + j);
	}

	public void printValues() {
		int i, j;
		System.out.println("Situazione scacchiera");
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				System.out.print(" " + getValue(i * 8 + j));
			}
			System.out.println("");
		}
//		for (int l = 0; l < 8 * 8; l++) {
//			System.out.print(getValue(l)+" ");
//			if(l%8 ==0) System.out.println("");
//		}
//		System.out.println(" ");
//
//		System.out.println("_________________");
//
//		for (int k = 0; k < 8; k++) {
//			System.out.println(Integer.toBinaryString(bitMap[k]));
//		}
	}

}
