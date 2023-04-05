package yun74.bindery.generator.svg2ttf;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import yun74.bindery.generator.svg2ttf.doubletype.FontFormatWriter;
import yun74.bindery.generator.svg2ttf.doubletype.TTGlyph;

/**
 * This class converts an SVG glyph to a TTF glyph. Caveats: scaling might not
 * work properly. Produces intersecting curves, which are rejected under
 * Windows. Works only with very particular path commands.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class GlyphCreator {

    /** Retrieves next command or -1*/
    public static int nextCommand(int pos, String path) {
        while (pos < path.length() && path.charAt(pos) == ' ')
            pos++;
        if (pos < path.length()) return (path.charAt(pos));
        return (-1);
    }

    /** Produces a glyph for an SVG character path */
    protected static TTGlyph glyph(char c, double width, String path) {
        double scale = Svg2Ttf.scale;
        TTGlyph glyph = new TTGlyph();
        glyph.setAdvanceWidth((int) (width * scale));
        Point point = new Point(0, 0);
        boolean hasContours = false;
        for (String contour : path.split("z")) {
            Matcher m2 = SvgFont.pathPattern.matcher(contour);
            int numPoints = glyph.getNumOfPoints();
            while (m2.find()) {
                hasContours = true;
                char command = m2.group(1).charAt(0);
                List<Double> numbers = GlyphCreator.getNumbers(m2.group(2));
                switch (command) {
                case 'l':
                case 'L':
                case 'M':
                case 'm':
                    if (numbers.size() != 2) {
                        FontFormatWriter.warning("Invalid numbers in glyph", FontFormatWriter.toString(c), "at command",
                                command, m2.group(2));
                        break;
                    }
                    int x = (int) (numbers.get(0) * scale);
                    int y = (int) (numbers.get(1) * scale);
                    if (Character.isLowerCase(command)) point.setLocation(x + point.x, y + point.y);
                    else point.setLocation(x, y);
                    // Look ahead: if the next command is also a move, no need to put a point                    
                    if (command == 'M' && nextCommand(m2.end(), contour) == 'm') break;
                    // Make a new point object because otherwise it's the same
                    glyph.addPoint(new Point(point.x, point.y));
                    glyph.addFlag(1); // on curve
                    break;
                case 'c':
                    if (numbers.size() != 6) {
                        FontFormatWriter.warning("Invalid numbers in glyph", FontFormatWriter.toString(c), "at command",
                                command, m2.group(2));
                        break;
                    }
                    double q0x = point.x;
                    double q0y = point.y;
                    double q1x = point.x + (numbers.get(0) * scale);
                    double q1y = point.y + (numbers.get(1) * scale);
                    double q2x = point.x + (numbers.get(2) * scale);
                    double q2y = point.y + (numbers.get(3) * scale);
                    double q3x = point.x + (numbers.get(4) * scale);
                    double q3y = point.y + (numbers.get(5) * scale);
                    // "c" is a cubic curve, but TTF can handle only quadratic curves.
                    // Split the curve
                    CubicBezierCurve[] curves = new CubicBezierCurve(q0x, q0y, q1x, q1y, q2x, q2y, q3x, q3y)
                            .splitInFour();
                    for (CubicBezierCurve b : curves) {
                        glyph.addPoint(b.quadraticControlPoint());
                        glyph.addFlag(0); // off curve
                        glyph.addPoint(b.endPoint());
                        glyph.addFlag(1); // on curve
                    }
                    point.setLocation(q3x, q3y);
                    break;
                case 'h':
                    if (numbers.size() != 1) {
                        FontFormatWriter.warning("Invalid numbers in glyph", FontFormatWriter.toString(c), "at command",
                                command, m2.group(2));
                        break;
                    }
                    int h = (int) (numbers.get(0) * scale);
                    point.setLocation(h + point.x, point.y);
                    // Make a new point because otherwise it's the same
                    glyph.addPoint(new Point(point.x, point.y));
                    glyph.addFlag(1); // on curve
                    break;
                case 'v':
                    if (numbers.size() != 1) {
                        FontFormatWriter.warning("Invalid numbers in glyph", FontFormatWriter.toString(c), "at command",
                                command, m2.group(2));
                        break;
                    }
                    int v = (int) (numbers.get(0) * scale);
                    point.setLocation(point.x, point.y + v);
                    // Make a new point because otherwise it's the same
                    glyph.addPoint(new Point(point.x, point.y));
                    glyph.addFlag(1); // on curve
                    break;
                case 'a':
                case 'A':
                    if (numbers.size() != 7) {
                        FontFormatWriter.warning("Invalid numbers in glyph", FontFormatWriter.toString(c), "at command",
                                command, m2.group(2));
                        break;
                    }
                    double x1 = point.x / scale;
                    double y1 = point.y / scale;
                    double rx = numbers.get(0);
                    double ry = numbers.get(1);
                    double rotation = numbers.get(2);
                    double largeflag = numbers.get(3);
                    double sweepflag = numbers.get(4);
                    double x2 = numbers.get(5);
                    double y2 = numbers.get(6);
                    if (Character.isLowerCase(command)) {
                        x2 = x2 + x1;
                        y2 = y2 + y1;
                    }
                    if (rotation != 0) FontFormatWriter.warning("Unsupported rotation in glyph",
                            FontFormatWriter.toString(c), ":", m2.group());
                    if (rx != ry) FontFormatWriter.warning("Unsupported elliptic curve in glyph",
                            FontFormatWriter.toString(c), ":", m2.group());
                    Arc arc = new Arc(x1, y1, x2, y2, rx, ry, sweepflag, largeflag);
                    if (!arc.isOK()) {
                        FontFormatWriter.warning("Pathological arc in glyph", FontFormatWriter.toString(c), ":",
                                m2.group(), arc);
                    } else {
                        for (double[] bezier : arc.asBezier()) {
                            glyph.addPoint(new Point((int) (bezier[0] * scale), (int) (bezier[1] * scale)));
                            glyph.addFlag((int) bezier[2]);
                        }
                    }
                    // Update final point
                    point.setLocation(x2 * scale, y2 * scale);
                    break;
                default:
                    FontFormatWriter.warning("Unsupported path command in glyph", c, (int) (c), ":", m2.group());
                    break;
                }
            }
            if (numPoints < glyph.getNumOfPoints()) {
                glyph.addEndPoint(glyph.getNumOfPoints() - 1);
            }
        }
        // If they tried to declare something, but nothing came out of it, fail
        if (hasContours && glyph.getNumOfContours() == 0) return (null);
        // Otherwise return the glyph
        return (glyph);
    }

    /** Creates a glyph */
    public static TTGlyph makeGlyph(char c, SvgFont svgFont) {
        String svgPath = svgFont.pathOf(c);
        if (svgPath == null) {
            FontFormatWriter.warning("No glyph defined for", FontFormatWriter.toString(c));
            return (null);
        }
        double width = svgFont.widthFor(c);
        TTGlyph glyph = glyph(c, width, svgPath);
        return (glyph);
    }

    /** Returns all numbers in a string */
    public static List<Double> getNumbers(String string) {
        List<Double> result = new ArrayList<>();
        Matcher m = SvgFont.numberPattern.matcher(string);
        while (m.find()) {
            result.add(Double.parseDouble(m.group()));
        }
        return (result);
    }

}
