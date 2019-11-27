package util;

public class ByteMap {
	private int[] bitMap;
	private int mask = 0b00000000000000000000000000000111;
	private int nBitIndex = 4;
//	~ negazione
	
	public ByteMap(int capacity) {
		bitMap = new int[capacity];
	}
	
	public void setValue(int value, int index) {
		int k,l;
		k = index % 8;
		l = index / 8;
		int n = bitMap[k];
		// (n & ~mask ) | ( (b << p) & mask )
		value = (n & ~mask) & ( mask << 8 - l );  
		
	}
	
	public static void main(String[] args) {
		int a=0b1,b=0b1;
		a = a << 1;
		System.out.println(a);
	}
}
