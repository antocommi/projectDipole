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
	
	public void setValue(int value, int index) {
//		index � un valore compreso tra 0 e capacity*8
		int k, l;
		k = index % 8;
		l = index / 8;
		int shiftWidth = (7-k) * 4;
		int n = bitMap[l], m = mask << shiftWidth;
		bitMap[l] = (n & ~m) | ( (value << shiftWidth) & m);
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
		return (n & (mask << (7-i) * 4)) >>> (7-i)*4;
	}

	public int getNumeroPedine(int i, int j) {
		return getValue(i*8+j);
	}
	
	public void printValues() {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				System.out.print(" "+getValue((i*8)+j));
			}
			System.out.println("");
		}
		
		System.out.println("_________________--");
		
		for(int k=0;k<8;k++) {
			System.out.println(Integer.toBinaryString(bitMap[k]));
		}
	}
	

}
