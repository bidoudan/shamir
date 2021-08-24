
import java.util.Random;

public class Shamir {

	private static final int prime = 257;

	private static int modular_exponentiation(int base, int exp, int mod) {
		if (exp == 0) {
			return 1;
		} else if (exp % 2 == 0) {
			int mysqrt = modular_exponentiation(base, exp / 2, mod);
			return (mysqrt * mysqrt) % mod;
		} else {
			return (base * modular_exponentiation(base, exp - 1, mod)) % mod;
		}
	}

	private static int[] split_number(int number, int n, int t) {
		int[] shares = new int[n];
		int[] coef = new int[t];
		coef[0] = number;
		for(int i = 1; i < t; ++i) {
			Random r = new Random();
			coef[i] = r.nextInt(prime);
		}

		for (int x = 0; x < n; ++x) {
			int y = coef[0];

			/* Calculate the shares */
			for (int i = 1; i < t; ++i) {
				int temp = modular_exponentiation(x + 1, i, prime);

				y = (y + (coef[i] * temp % prime)) % prime;
			}

			/* Sometimes we're getting negative numbers, and need to fix that */
			y = (y + prime) % prime;

			shares[x] = y;
		}

		return shares;
	}

	public static String[] split_string(String secret, int n, int t) {
		int len = secret.length();

		String[] shares = new String[n];
		for (int i = 0; i < n; ++i) {
			shares[i] = String.format("%02X%02XAA", (i+1), t);
		}

		/* Now, handle the secret */

		for (int i = 0; i < len; ++i) {
			int letter = secret.charAt(i); // - '0';

			if (letter < 0) {
				letter = 256 + letter;
			}

			int[] chunks = split_number(letter, n, t);

			for (int j = 0; j < n; ++j) {
				if (chunks[j] == 256) {
					shares[j]  += String.format("%s", "G0");
				} else {

					shares[j] += String.format("%02X", chunks[j]);
				}
			}

		}

		return shares;
	}



	public static void main(String[] args) {
		String[] shares = split_string("tpubD8iUw4tBBwUdScqzUiK7rhaMVdHkzzwsX6TzkCF7gddy4kwpwgVChSZ1VRnWKn2EdcrjHydZ6apcjoRnezv6Z1bzrBus4PsUEhfmhaPbF9j", 5, 3);
		System.out.println(DeShamir.extractSecretFromShareStrings(shares));
	}

}
