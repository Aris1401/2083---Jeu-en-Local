package engineClasses;

public class EngineMath {
	public static double Lerp(double a, double b, double t) {
	    return (1-t) * a + t * b;
	}
}
