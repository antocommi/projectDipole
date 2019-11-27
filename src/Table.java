public class Table {
	
	public int table = 0;

	// Ritorna l'intero modificato con la nuova posizione 
	public static int modifyBit(int numero, int posizione, int valBinario) {
		int mask = 1 << posizione;
		return (numero & ~mask) | ((valBinario << posizione) & mask);
	}
	
	
	

}