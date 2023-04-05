package yun74.bindery.generator.svg2ttf;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import yun74.bindery.generator.svg2ttf.doubletype.FontFileWriter;
import yun74.bindery.generator.svg2ttf.doubletype.TTGlyph;
import yun74.bindery.generator.svg2ttf.doubletype.TTUnicodeRange;

/**
 * This class converts an SVG font to a TTF font -- at least in theory. In
 * practice, the converter can understand only a very limited form of SVG files.
 * These include the SVG files produced by PowerLine, the free SVG slide editor.
 * Unfortunately, this code produces faulty TTF files, which work only on Mac. To
 * use the TTF files under Windows, use any converter to convert TTF to TTF (to
 * the same file format), because this usually cleans out errors.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class Svg2Ttf {
    /**
     * Scaling for the SVG sizes. Not sure what value is good here in general. 2 works fine.
     */
    public static final int scale = 2;

    /** Adds a glyph */
    protected static boolean addGlyph(char c, SvgFont svgFont, FontFileWriter writer) {
        // Space character is already there
        if (c == ' ') return (false);
        TTUnicodeRange range = TTUnicodeRange.of(c);
        if (range != null) writer.addUnicodeRange(range);
        TTGlyph glyph = GlyphCreator.makeGlyph(c, svgFont);
        if (glyph == null) return (false);
        int index = writer.addGlyph(glyph);
        writer.addCharacterMapping(c, index);
        return (true);
    }

    /**
     * Converts an SVG font file to TTF
     */
    public static void convert(File fol) throws Exception {
    	String designer = "yun74";
    	String designerUrl = "https://yun74.gwt";
    	String outputFile = "font.ttf";
        System.out.println("Converting " + fol.getCanonicalPath());
        SvgFont svgFont = new SvgFont(fol);
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            FontFileWriter writer = new FontFileWriter(out);
            writer.setAscent((int) (svgFont.getAscent() * scale));
            writer.setDescent((int) (svgFont.getDescent() * -scale));
            writer.setLineGap(0 * scale);
            writer.setOffset(0);
            writer.setXHeight((int) (svgFont.getAscent() * 8 / 10 * scale));
            // Set code ranges from https://docs.microsoft.com/en-us/typography/opentype/spec/os2#ur
            for (int a : new int[] { 0, 1, 2, 3, 6, 7, 9, 29, 35, 37, 38, 68 }) {
                writer.setCodeRangeFlag(a);
            }
            writer.setNames(svgFont.getName(), designer, designerUrl);
            // We put the characters here in a specific order to comply
            // with the POST 1 table convention. However, we will finally use
            // POST 3.
            // At position 0, there must be a special character "undefined"
            writer.addCharacterMapping(TTUnicodeRange.k_notDef, writer.addGlyph(DefaultGlyphs.undef()));
            // At position 1, we want the NULL character
            writer.addCharacterMapping(TTUnicodeRange.k_null, writer.addGlyph(DefaultGlyphs.nullGlyph()));
            // At position 2, we want the CR character, which we render like
            // space
            TTGlyph space = GlyphCreator.makeGlyph(' ', svgFont);
            if (space == null) space = DefaultGlyphs.spaceGlyph();
            writer.addCharacterMapping(TTUnicodeRange.k_cr, writer.addGlyph(space));
            // At position 3, we want the SPACE character
            writer.addCharacterMapping(TTUnicodeRange.k_space, writer.addGlyph(space));
            // Now put the basic Latin ones
            for (char c = 0x20; c < 0x80; c++) {
                addGlyph(c, svgFont, writer);
            }
            // Now put all the other characters in order
            for (char c : svgFont.characters()) {
                // Basic Latin is already there
                if (c >= 0x20 && c < 0x80) continue;
                addGlyph(c, svgFont, writer);
            }
            // Add all the kernings
            for (char c1 : svgFont.kernings()) {
                for (char c2 : svgFont.kerningsOf(c1)) {
                    // Watch out, these have to be negative!
                    writer.addKern(c1, c2, svgFont.kerning(c1, c2).intValue() * -scale);
                }
            }
            writer.write();
        }
        System.out.println("done");
    }

    /**
     * Converts a given SVG font file to TTF
     */
    public static void main(String[] args) throws Exception {
        /*for (String name : new String[] { "Fabiana-bold-font.svg", "Fabiana-font.svg", "Fabiana-italic-font.svg" }) {
            File file = new File("C:\\Users\\Fabian\\Sync\\Homepage\\programs\\fabiana\\Fabiana\\", name);
            convert(file, "Fabian M. Suchanek", "https://suchanek.name");
        }*/
        System.exit(0);
        if (args == null || args.length != 3) {
            System.out.println("Converts an SVG font file generated by PowerLine to TTF\n");
            System.out.println("Call: Svg2ttf file.svg designerName designerUrl");
            System.exit(63);
        }
        convert(new File(args[0]));
    }
}
