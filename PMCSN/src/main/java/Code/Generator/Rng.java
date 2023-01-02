package Code.Generator;

import java.util.Arrays;
import java.util.Date;

public class Rng {

    private final long MODULUS =  2147483647 ;/* DON'T CHANGE THIS VALUE                  */
    private final long MULTIPLIER = 48271    ;  /* DON'T CHANGE THIS VALUE                  */
    private final long CHECK  =  399268537;  /* DON'T CHANGE THIS VALUE                  */
    private static final int STREAMS  =  256    ;    /* # of streams, DON'T CHANGE THIS VALUE    */
    private final long A256   =    22925   ;   /* jump multiplier, DON'T CHANGE THIS VALUE */
    private static final long DEFAULT  =  123456789 ; /* initial seed, use 0 < DEFAULT < MODULUS  */
    static long[] seed = new long[ STREAMS];  /* current state of each stream   */
    static int  stream        = 0;          /* stream index, 0 is the default */
    static int  initialized   = 0;

    public Rng(){
        Arrays.fill(seed, DEFAULT);
    }
    /* ----------------------------------------------------------------
     * Random returns a pseudo-random real number uniformly distributed
     * between 0.0 and 1.0.
     * ----------------------------------------------------------------
     */
    public double random(){

        long Q = MODULUS / MULTIPLIER;
        long R = MODULUS % MULTIPLIER;
        long t;

        t = MULTIPLIER * (seed[stream] % Q) - R * (seed[stream] / Q);
        if (t > 0)
            seed[stream] = t;
        else
            seed[stream] = t + MODULUS;
        return ((double) seed[stream] / MODULUS);
    }

    /* ---------------------------------------------------------------------
     * Use this function to set the state of all the random number generator
     * streams by "planting" a sequence of states (seeds), one per stream,
     * with all states dictated by the state of the default stream.
     * The sequence of planted states is separated one from the next by
     * 8,367,782 calls to Random().
     * ---------------------------------------------------------------------
     */
    public void plantSeeds(long x) {
        long Q = MODULUS / A256;
        long R = MODULUS % A256;
        int  j;
        int  s;

        initialized = 1;
        s = stream;                            /* remember the current stream */
        selectStream(0);                       /* change to stream 0          */
        putSeed(x);                            /* set seed[0]                 */
        stream = s;                            /* reset the current stream    */
        for (j = 1; j < STREAMS; j++) {
            x = A256 * (seed[j - 1] % Q) - R * (seed[j - 1] / Q);
            if (x > 0)
                seed[j] = x;
            else
                seed[j] = x + MODULUS;
        }
    }


    /* ---------------------------------------------------------------
     * Use this function to set the state of the current random number
     * generator stream according to the following conventions:
     *    if x > 0 then x is the state (unless too large)
     *    if x < 0 then the state is obtained from the system clock
     * ---------------------------------------------------------------
     */
    public void putSeed(long x) {

        if (x > 0)
            x = x % MODULUS;                       /* correct if x is too large  */
        if (x < 0)
            x = new Date().getTime() % MODULUS;
        seed[stream] = x;
    }


    /* ---------------------------------------------------------------
     * Use this function to get the state of the current random number
     * generator stream.
     * ---------------------------------------------------------------
     */
    public long getSeed() {
        return seed[stream];
    }


    /* ------------------------------------------------------------------
     * Use this function to set the current random number generator
     * stream -- that stream from which the next random number will come.
     * ------------------------------------------------------------------
     */
    public void selectStream(int index) {
        stream = (int) (index % STREAMS);
        if ((initialized == 0) && (stream != 0))   /* protect against        */
            plantSeeds(DEFAULT);                     /* un-initialized streams */
    }


}
