
import java.lang.Math;
public class Shamir {

	private static final int prime = 257;
	
	public int[] gcdD(int a, int b) {
		int[] xyz = new int[3];

		if (b == 0) {
			xyz[0] = a;
			xyz[1] = 1;
			xyz[2] = 0;
		} else {
			int n = (int) Math.floor(a / b);
			int c = a % b;
			int[] r = gcdD(b, c);

			xyz[0] = r[0];
			xyz[1] = r[2];
			xyz[2] = r[1] - r[2] * n;

		}

		return xyz;
	}
	
	public int modInverse(int k) {
		k = k % prime;

		int r;
		int[] xyz;

		if (k < 0) {
			xyz = gcdD(prime, -k);
			r = -xyz[2];
		} else {
			xyz = gcdD(prime, k);
			r = xyz[2];
		}

		return (prime + r) % prime;
	}
	
	public int join_shares(int[] xy_pairs, int n) {
		int secret = 0;
		int numerator;
		int denominator;
		int startposition;
		int nextposition;
		int value;
		int i;
		int j;

		// Pairwise calculations between all shares
		for (i = 0; i < n; ++i) {
			numerator = 1;
			denominator = 1;

			for (j = 0; j < n; ++j) {
				if (i != j) {
					startposition = xy_pairs[i * 2];		// x for share i
					nextposition = xy_pairs[j * 2];		// x for share j
					numerator = (numerator * -nextposition) % 257;
					denominator = (denominator * (startposition - nextposition)) % 257;
				}
			}

			value = xy_pairs[i * 2 + 1];

			secret = (secret + (value * numerator * modInverse(denominator))) % 257;
		}

		/* Sometimes we're getting negative numbers, and need to fix that */
		secret = (secret + 257) % 257;

		return secret;
	}
	
	public String joinString(String[] shares, int n) {
		
		int len = (shares[0].length() - 6)/2;
		String result = null;
		char[] codon = new char[2];
		int[] x = new int[n];
		
		// Determine x value for each share
		for (int i = 0; i < n; ++i) {
			if (shares[i] == null) {
				return null;
			}

			codon[0] = shares[i].charAt(0);
			codon[1] = shares[i].charAt(1);

			x[i] = Integer.parseInt("" + codon[0] + codon[1], 16);
		}
		
		
		// Iterate through characters and calculate original secret
		for (int i = 0; i < len; ++i) {
			int[] chunks = new int[n* 2];

			// Collect all shares for character i
			for (int j = 0; j < n; ++j) {
				// Store x value for share
				chunks[j * 2] = x[j];

				codon[0] = shares[j].charAt(6 + i * 2);
				codon[1] = shares[j].charAt(6 + i * 2 + 1);
				
				String codi = "" +codon[0]+codon[1];
				// Store y value for share
				if (codi.equals("G0")) {
					chunks[j * 2 + 1] = 256;
				} else {
					chunks[j * 2 + 1] = Integer.parseInt("" + codon[0] + codon[1], 16);
				}
			}

			//unsigned char letter = join_shares(chunks, n);
			char letter = (char) join_shares(chunks, n);

			result = result + String.format("%c", letter);
		}
		
		return result;
	}
	
	
	String extract_secret_from_share_strings(String[] string) {
		cha shares = malloc(sizeof(char *) * 255);

		char * share;
		char * saveptr = NULL;
		int i = 0;

		/* strtok_rr modifies the string we are looking at, so make a temp copy */
		char * temp_string = strdup(string);

		/* Parse the string by line, remove trailing whitespace */
		share = strtok_rr(temp_string, "\n", &saveptr);

		shares[i] = strdup(share);
		trim_trailing_whitespace(shares[i]);

		while ( (share = strtok_rr(NULL, "\n", &saveptr))) {
			i++;

			shares[i] = strdup(share);

			trim_trailing_whitespace(shares[i]);

			if ((shares[i] != NULL) && (strlen(shares[i]) == 0)) {
				/* Ignore blank lines */
				free(shares[i]);
				i--;
			}
		}

		i++;

		String secret = joinStrings(shares, i);

		free_string_shares(shares, i);

		return secret;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello");
	}

}
