package util;

public class ByteMap {
	private int[] bitMap;
	private int mask = 0b00000000000000000000000000000111;
	private int nBitIndex = 4;
//	~ negazione
	// NUMERO DI VALORE PER INTERO: 8 se |valore|=4 bit
	public ByteMap(int capacity) {
		bitMap = new int[capacity];
	}
	
	public void setValue(int value, int index) {
//		index è un valore compreso tra 0 e capacity*8
		int k,l;
		k = index % 8;
		l = index / 8;
		int n = bitMap[l], m  = mask << k*3 ;
		bitMap[l] = ( n & ~ m ) | ( ( value << k*3 ) & m );  
	}
	
	public int[] getbm() {
		return bitMap;
	}
	
	public int getIndex(int x, int y) {
		return x*8+y;
	}
	
	public int getValue(int index) {
		// index \in [0, 8* bitMap.len ]
		int i = index % 8, j = index / 8, n;
		n = bitMap[j];
		return (n & (mask << i*3)) ;//>> (8 - i)*3;
	}
	
	public static void main(String[] args) {
		ByteMap bm = new ByteMap(4);
		bm.setValue(2,1);
		
		System.out.println(bm.getValue(1)+" "+bm.getbm()[0]);
	}
}
