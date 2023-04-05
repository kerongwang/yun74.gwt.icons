package yun74.bindery.generator.svg2ttf;

public class CubicBezierCurve {
    double q0x, q0y, q1x, q1y, q2x, q2y, q3x, q3y;

    public CubicBezierCurve(double q0x, double q0y, double q1x, double q1y, double q2x, double q2y, double q3x,
            double q3y) {
        super();
        this.q0x = q0x;
        this.q0y = q0y;
        this.q1x = q1x;
        this.q1y = q1y;
        this.q2x = q2x;
        this.q2y = q2y;
        this.q3x = q3x;
        this.q3y = q3y;
    }

    /** Splits the curve in two.
     * https://www.timotheegroleau.com/Flash/articles/cubic_bezier_in_flash.htm
     * */
    public CubicBezierCurve[] split() {
        double p12x = (q1x + q2x) / 2;
        double p12y = (q1y + q2y) / 2;
        double p01x = (q1x + q0x) / 2;
        double p01y = (q1y + q0y) / 2;
        double pAx = (p12x + p01x) / 2;
        double pAy = (p12y + p01y) / 2;
        double p23x = (q2x + q3x) / 2;
        double p23y = (q2y + q3y) / 2;
        double pBx = (p12x + p23x) / 2;
        double pBy = (p12y + p23y) / 2;
        double pCx = (pAx + pBx) / 2;
        double pCy = (pAy + pBy) / 2;

        CubicBezierCurve c1 = new CubicBezierCurve(q0x, q0y, p01x, p01y, pAx, pAy, pCx, pCy);
        CubicBezierCurve c2 = new CubicBezierCurve(pCx, pCy, pBx, pBy, p23x, p23y, q3x, q3y);
        return (new CubicBezierCurve[] { c1, c2 });
    }
    
    public CubicBezierCurve[] splitInFour() {
        CubicBezierCurve[] curves=split();
        CubicBezierCurve[] curves1=curves[0].split();
        CubicBezierCurve[] curves2=curves[1].split();
        return(new CubicBezierCurve[] {curves1[0],curves1[1],curves2[0],curves2[1]});
    }

    public Point endPoint() {
        return(new Point((int)q3x,(int)q3y));
    }
    /** Creates a quadratic control point. 
     * https://stackoverflow.com/questions/2009160/how-do-i-convert-the-2-control-points-of-a-cubic-curve-to-the-single-control-poi
     * */
    public Point quadraticControlPoint() {
        double p1x1 = 3 / 2 * q1x - 1 / 2 * q0x;
        double p1x2 = 3 / 2 * q2x - 1 / 2 * q3x;
        double p1y1 = 3 / 2 * q1y - 1 / 2 * q0y;
        double p1y2 = 3 / 2 * q2y - 1 / 2 * q3y;
        return (new Point((int) (p1x1 + p1x2) / 2, (int) (p1y1 + p1y2) / 2));
    }

}
