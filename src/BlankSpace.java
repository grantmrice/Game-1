import java.util.Scanner;

public class BlankSpace {
	
	static Scanner read = new Scanner(System.in);
	
	public static void main(String [] args){
		System.out.println("5 numbers");
		int ar[] = new int[5];
		int sum = 0;
		for(int x = 0;x < ar.length ;x++){
			ar[x] = read.nextInt();
			sum += ar[x];
			
		}
		System.out.println(sum/ar.length);
		
		read.close();
	}
}