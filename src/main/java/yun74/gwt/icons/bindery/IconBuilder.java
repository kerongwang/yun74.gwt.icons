package yun74.gwt.icons.bindery;

import java.util.function.BiConsumer;

import com.google.gwt.user.client.ui.Widget;

public interface IconBuilder {
	IconBuilder name(String name);
	Widget build();
	void dump(BiConsumer<String, Widget> conmuser);
}
