package com.invoiceme.utiles;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

public class AppFontFactory {
	public static Font getOpenSansFont(String type, float size) {
		String fuente = "OpenSans_" + type;
		return FontFactory.getFont(fuente, "Identity-H", true, size);
	}

	static {
		FontFactory.register("font/Open_Sans/OpenSans-Regular.ttf", "OpenSans_regular");
		FontFactory.register("font/Open_Sans/OpenSans-Light.ttf", "OpenSans_light");
	}
}