public class DeShamir {
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
        String[] shares = new String[]{"0103AAC1FD589676CB716F56551CE4159C7FD04BCE70AF120066DA02F88969C06533483959B42F78F430581A19D2D35F6C4EEB14A846769D7E7E90D9CA6FDBD4011D8D4B83C6E9C6C5C31201471E7096A8D9C0A3744BE8F06F255A34A6CCAB7C3E3553F975B88B55DA814BE5DCCB28617DC0", "0203AA4A6AG0D9DC7BD033A30058D667AE106FA2F807704D3EB5CBE8E6687F8F92316FA2D065BD79C7C021EA064BA4374F00E80584CD39D7ED64D6BAFE75D17A818E7343466A85A04183380089FA9D439BECDF6CC7CF672CBF31D3765695903297F6A8ED93CF2E37CDDCF0EFACBAD6ED731A", "0303AA10B96B2A754986864D71F6435BAAG03338CA18ACFF212307021C13B0C7E063DC92D468F154C7E59DD329C5CDEAG05A4B24E8FCAC06C62521D7CE7C1865D38B224F8E334E05B9B0CB49F701D85A51ADAFCC5AEDCA01517E9B127ACD03927E993F3D8EB01DFD1C533B62D9357DF31383"};
        System.out.println(extractSecretFromShareStrings(shares));
    }
}
