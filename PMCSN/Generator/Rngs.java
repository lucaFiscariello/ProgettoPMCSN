package Generator;

import static java.lang.Math.*;

public class Rngs {

    private final Rng rng = new Rng();

    public void plantSeed(long seed){
        rng.plantSeeds(seed);
    }

    public void selectStream(int stream){
        rng.selectStream(stream);
    }

    /* ========================================================
     * Returns 1 with probability p or 0 with probability 1 - p.
     * NOTE: use 0.0 < p < 1.0
     * ========================================================
     */
    public long bernoulli(double p) {
        return ((rng.random() < (1.0 - p)) ? 0 : 1);
    }

    /* ================================================================
     * Returns a binomial distributed integer between 0 and n inclusive.
     * NOTE: use n > 0 and 0.0 < p < 1.0
     * ================================================================
     */
    public long binomial(long n, double p) {
        long i, x = 0;

        for (i = 0; i < n; i++)
            x += bernoulli(p);
        return (x);
    }


    /* ===================================================================
     * Returns an equilikely distributed integer between a and b inclusive.
     * NOTE: use a < b
     * ===================================================================
     */
    public long equilikely(long a, long b) {
        return (a + (long) ((b - a + 1) * rng.random()));
    }

    /* ====================================================
     * Returns a geometric distributed non-negative integer.
     * NOTE: use 0.0 < p < 1.0
     * ====================================================
     */
    public long geometric(double p) {
        return ((long) (log(1.0 - rng.random()) / log(p)));
    }

    /* =================================================
     * Returns a Pascal distributed non-negative integer.
     * NOTE: use n > 0 and 0.0 < p < 1.0
     * =================================================
     */
    public long pascal(long n, double p) {
        long i, x = 0;

        for (i = 0; i < n; i++)
            x += geometric(p);
        return (x);
    }

    /* ==================================================
     * Returns a Poisson distributed non-negative integer.
     * NOTE: use m > 0
     * ==================================================
     */
    public long poisson(double m) {
        double t = 0.0;
        long   x = 0;

        while (t < m) {
            t += exponential(1.0);
            x++;
        }
        return (x - 1);
    }

    /* ===========================================================
     * Returns a uniformly distributed real number between a and b.
     * NOTE: use a < b
     * ===========================================================
     */
    public double uniform(double a, double b) {
        return (a + (b - a) * rng.random());
    }


    /* =========================================================
     * Returns an exponentially distributed positive real number.
     * NOTE: use m > 0.0
     * =========================================================
     */
    public double exponential(double m) {
        return (-m * log(1.0 - rng.random()));
    }

    /* ==================================================
     * Returns an Erlang distributed positive real number.
     * NOTE: use n > 0 and b > 0.0
     * ==================================================
     */
    public double erlang(long n, double b) {
        long   i;
        double x = 0.0;

        for (i = 0; i < n; i++)
            x += exponential(b);
        return (x);
    }

    /* ========================================================================
     * Returns a normal (Gaussian) distributed real number.
     * NOTE: use s > 0.0
     *
     * Uses a very accurate approximation of the normal idf due to Odeh & Evans,
     * J. Applied Statistics, 1974, vol 23, pp 96-97.
     * ========================================================================
     */
    public double normal(double m, double s) {
        double p0 = 0.322232431088;     double q0 = 0.099348462606;
        double p1 = 1.0;                double q1 = 0.588581570495;
        double p2 = 0.342242088547;     double q2 = 0.531103462366;
        double p3 = 0.204231210245e-1;  double q3 = 0.103537752850;
        double p4 = 0.453642210148e-4;  double q4 = 0.385607006340e-2;
        double u, t, p, q, z;

        u   = rng.random();
        if (u < 0.5)
            t = sqrt(-2.0 * log(u));
        else
            t = sqrt(-2.0 * log(1.0 - u));
        p   = p0 + t * (p1 + t * (p2 + t * (p3 + t * p4)));
        q   = q0 + t * (q1 + t * (q2 + t * (q3 + t * q4)));
        if (u < 0.5)
            z = (p / q) - t;
        else
            z = t - (p / q);
        return (m + s * z);
    }

    /* ====================================================
     * Returns a lognormal distributed positive real number.
     * NOTE: use b > 0.0
     * ====================================================
     */
    public double lognormal(double a, double b) {
        return (exp(a + b * normal(0.0, 1.0)));
    }

    /* =====================================================
     * Returns a chi-square distributed positive real number.
     * NOTE: use n > 0
     * =====================================================
     */
    public double chisquare(long n) {
        long   i;
        double z, x = 0.0;

        for (i = 0; i < n; i++) {
            z  = normal(0.0, 1.0);
            x += z * z;
        }
        return (x);
    }

    /* ===========================================
     * Returns a student-t distributed real number.
     * NOTE: use n > 0
     * ===========================================
     */
    double student(long n) {
        return (normal(0.0, 1.0) / sqrt(chisquare(n) / n));
    }

    /* ===========================================
     * Returns a pareto distributed real number.
     * NOTE: use n t.c. 0<n/(n-1)<2
     * ===========================================
     */
    public double pareto(double n) {
        double a = n*1.1/2.1;
        return (a/Math.pow(rng.random(), 1/2.1));
    }

    /* ===========================================
     * Returns a deterministic distributed real number.
     * ===========================================
     */
    public double deterministic(double n) {
        return (n);
    }



}
