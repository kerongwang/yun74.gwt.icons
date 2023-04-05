package yun74.gwt.icons;

import java.util.function.BiConsumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import yun74.gwt.icons.bindery.IconBuilder;

public class Yun74Icons {
    public static Widget get(String name) {
        IconBuilder builder = GWT.create(IconBuilder.class);
        return builder.name(name).build();
    }

    public static Element getElement(String name) {
        IconBuilder builder = GWT.create(IconBuilder.class);
        return builder.name(name).build().getElement();
    }

    public static void dump(BiConsumer<String, Widget> conmuser) {
        IconBuilder builder = GWT.create(IconBuilder.class);
        builder.dump(conmuser);
    }
}
