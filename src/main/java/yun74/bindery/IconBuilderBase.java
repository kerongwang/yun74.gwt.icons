package yun74.bindery;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public abstract class IconBuilderBase implements IconBuilder {
	public static class SVGData {
		String view;
		String path;
	}

	// For subclass load SVG data.
	abstract public void init();

	private final Map<String, SVGData> datamaps = new HashMap<>();

	public void addResource(String fullpath, String view, String svgpath) {
		// fullpath is full filename inside resources.
		fullpath = fullpath.replace(".svg", "");
		int index = fullpath.lastIndexOf("/");
		fullpath = fullpath.substring(index + 1);
		SVGData data = new SVGData();
		data.view = view;
		data.path = svgpath;
		datamaps.put(fullpath, data);
	}

	public IconBuilderBase() {
		// Default Icon in case of missing.
		addResource("skull-scan", "0 0 24 24",
				"M2 0C.9 0 0 .9 0 2V6H2V2H6V0H2M18 0V2H22V6H24V2C24 .9 23.1 0 22 0H18M12 3C7.6 3 4 6.6 4 11C4 13.5 5.2 15.8 7 17.2V21H9V18H11V21H13V18H15V21H17V17.2C18.8 15.7 20 13.5 20 11C20 6.6 16.4 3 12 3M8 14C6.9 14 6 13.1 6 12S6.9 10 8 10 10 10.9 10 12 9.1 14 8 14M10.5 16L12 13L13.5 16H10.5M16 14C14.9 14 14 13.1 14 12S14.9 10 16 10 18 10.9 18 12 17.1 14 16 14M0 18V22C0 23.1 .9 24 2 24H6V22H2V18H0M22 18V22H18V24H22C23.1 24 24 23.1 24 22V18H22Z");
		init();
	}

	public void dump(BiConsumer<String, Widget> consumer) {
		for (Entry<String, SVGData> entry : datamaps.entrySet()) {
			consumer.accept(entry.getKey(), new SVGIcon(entry.getValue().view, entry.getValue().path));
		}
	}

	private String name;

	@Override
	public IconBuilder name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public Widget build() {
		SVGData r = datamaps.get(name);
		if (null != r)
			return new SVGIcon(r.view, r.path);

		return name("skull-scan").build();
	}

	private static class SVGIcon extends Widget {
		public static native Element elementNS(String view) /*-{
	    var e = document.createElementNS("http://www.w3.org/2000/svg", "svg");
		e.setAttribute("fill-rule", "evenodd");
		e.setAttribute("viewBox", view);
		e.setAttribute("preserveAspectRatio","xMidYMid meet");
		e.setAttribute("focusable","false");
	    return e;
	}-*/;

		public static native Element elementNSPath() /*-{
	    return document.createElementNS("http://www.w3.org/2000/svg", "path");
	}-*/;

		public SVGIcon(String view, String path) {
			setElement(elementNS(view));
			Element p = elementNSPath();
			p.setAttribute("d", path);
			getElement().appendChild(p);
		}
	}

}
