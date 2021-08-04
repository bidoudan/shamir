
import java.lang.Math;
public class Shamir {

	   private static final int prime = 257;

    public static int[] gcdD(int a, int b) {
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

    public static int modInverse(int k) {
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

    public static int join_shares(int[] xy_pairs, int n) {
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
                    startposition = xy_pairs[i * 2];        // x for share i
                    nextposition = xy_pairs[j * 2];        // x for share j
                    numerator = (numerator * -nextposition) % prime;
                    denominator = (denominator * (startposition - nextposition)) % prime;
                }
            }

            value = xy_pairs[i * 2 + 1];

            secret = (secret + (value * numerator * modInverse(denominator))) % prime;
        }

        /* Sometimes we're getting negative numbers, and need to fix that */
        secret = (secret + prime) % prime;

        return secret;
    }

    public static String joinStrings(String[] shares, int n) {
        int len = (shares[0].length() - 6) / 2;
        String result = "";
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
            int[] chunks = new int[n * 2];

            // Collect all shares for character i
            for (int j = 0; j < n; ++j) {
                // Store x value for share
                chunks[j * 2] = x[j];

                codon[0] = shares[j].charAt(6 + i * 2);
                codon[1] = shares[j].charAt(6 + i * 2 + 1);

                String codi = "" + codon[0] + codon[1];
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


    public static String extractSecretFromShareStrings(String[] strings) {
        
        String secret = joinStrings(strings, strings.length);
        
        return secret;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String[] shares = new String[]{"0103AA73C196959FF88843EA3B6892C72762107043EF656B2E41E45AEE636866BB139A5BCA6B184963DCF13955E7DABFB890614EADE1F07ACE581038497BB77AA6198C736536599F709A5CE775266B047E01F57E9B6737E82D1D6D35B08878725DE7CD9CFD3AE1EDED085B7CBAC4919A139F", "0203AA79824828C490042EDB7FEDC96E10042A48B9EB262328A8C23A597B7B31CB97C009B7EDF42456457737E59497C2FF9660FB40EDD5056865F8C6990190D2EE9970EE3267239E43E14B4FA6E522612740D7CD136B909CC077CBF9BC133F2719D057C17EE1CAFFE0E854879749BDDD3C71", "0303AA86B48C1CB301E0FB3A3AD0115C2F32A3BCCE44B4803468DD01A68E6FAE87D7BD6C2DDAD8011B88E733147B6F6B47606C66F174FEEB035D17105C0D004C08CCG0B2BED8944AD2451785F5AB7D8A2DEE1334BC7A6386087F5E9591FA8A55811F02E0FD49EF7D2FD56291EDE4F7FED5AE"};
        System.out.println(extractSecretFromShareStrings(shares));
    }

}
