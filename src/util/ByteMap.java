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
//		index è un valore compreso tra 0 e capacity*8
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

	public String getValue(int index) {
		// index \in [0, 8* bitMap.len ]
		int i = index % 8, j = index / 8, n;
		n = bitMap[j];
		return Integer.toBinaryString( 
				(n & (mask << (7-i) * 4)) >> (7-i)*4
						);
	}

//	public int getNumeroPedine(int i, int j) {
//		return getValue(i*8+j);
//	}
	
	public void printValues() {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				System.out.print(" "+this.getValue(i+j));
			}
			System.out.println("");
		}
		
		for(int k=0;k<8;k++) {
			System.out.println(Integer.toBinaryString(bitMap[k]));
		}
	}
	
	public static void main(String[] args) {
		ByteMap bm = new ByteMap(8);
		bm.setValue(13, 0);
		bm.printValues();
//		System.out.println(bm.getValue(2));
	}

}
