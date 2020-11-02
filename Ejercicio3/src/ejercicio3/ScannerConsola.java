package ejercicio3;

import java.util.Scanner;

class ScannerConsola {
	private Scanner scanConsola;
	
	public ScannerConsola() {
		scanConsola = new Scanner(System.in, "UTF-8");
	}
	
	public String nextLine() {
		return scanConsola.nextLine();
	}
}
