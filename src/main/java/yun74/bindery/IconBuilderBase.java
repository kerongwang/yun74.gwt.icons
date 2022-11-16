package yun74.bindery;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public abstract class IconBuilderBase implements IconBuilder {
	public static class rawSVG {
		String view;
		String path;
	}

	abstract public void init();

	private final Map<String, rawSVG> rmap = new HashMap<>();

	public void add(String key, String view, String path) {
		key = key.replace(".svg", "");
		int index = key.lastIndexOf("/");
		key = key.substring(index + 1);
		GWT.log("add: " + key + " view: " + view + " path: " + path);
		rawSVG r = new rawSVG();
		r.view = view;
		r.path = path;
		rmap.put(key, r);
	}

	public IconBuilderBase() {
		add("account-box-multiple", "0 0 24 24",
				"M4,6H2V20A2,2 0 0,0 4,22H18V20H4V6M20,2A2,2 0 0,1 22,4V16A2,2 0 0,1 20,18H8A2,2 0 0,1 6,16V4A2,2 0 0,1 8,2H20M17,7A3,3 0 0,0 14,4A3,3 0 0,0 11,7A3,3 0 0,0 14,10A3,3 0 0,0 17,7M8,15V16H20V15C20,13 16,11.9 14,11.9C12,11.9 8,13 8,15Z");

		init();
	}

	@Override
	public Widget build(String name) {
		GWT.log("test20:" + name);

		rawSVG r = rmap.get(name);
		if (null != r)
			return new SVGIcon(r.view, r.path);
		else
			GWT.log("no, you miss: " + name);

		return build("account-box-multiple");
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
