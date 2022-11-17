package yun74.bindery.generator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

public class GWTIconSVGParser {
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private static final String svgTagName = "svg";

	static void debugElement(Element e) {
		System.out.println("e is: " + e);
		NamedNodeMap attributes = e.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			System.out.println("attribute[" + i + "]: " + attributes.item(i) + " class: "
					+ attributes.item(i).getClass().getName());
		}
	}

	public static GWTIconSVGParser parser(InputStream in) {
		GWTIconSVGParser svg = new GWTIconSVGParser();
		try {
			Document doc = dbf.newDocumentBuilder().parse(in);
			doc.getDocumentElement().normalize();
			Optional.ofNullable(doc.getElementsByTagName(svgTagName))
					.ifPresent(nodes -> Optional.ofNullable((nodes.item(0))).ifPresent(e -> {
						// process svg attribute viewBpx
						Optional.ofNullable(((Element) (e)).getAttribute("viewBox")).ifPresent(v -> svg.view = v);
						// process path node.
						Optional.ofNullable(((Element) (e)).getElementsByTagName("path")).ifPresent(p -> {
							Element el = (Element) p.item(0);
//						debugElement(el);
							svg.path = el.getAttribute("d");
						});
					}));
			return svg;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	String path;
	String view;

	@Override
	public String toString() {
		return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"" + view + "\"><path d=\"" + path + "\"/></svg>";
	}
}
