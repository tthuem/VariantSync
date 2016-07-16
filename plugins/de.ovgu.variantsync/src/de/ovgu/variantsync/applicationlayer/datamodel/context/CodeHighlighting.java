package de.ovgu.variantsync.applicationlayer.datamodel.context;

import org.eclipse.swt.graphics.RGB;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.09.2015
 */
public enum CodeHighlighting {

	// YELLOW(new RGB(255, 255, 0)), GREEN(new RGB(152, 251, 0)), BLUE(new RGB(
	// 135, 206, 235)), GREY(new RGB(179, 179, 179)), SALMON(new RGB(255,
	// 140, 105)), ANTIQUEWHITE(new RGB(250, 235, 215)), SPRINGGREEN(
	// new RGB(0, 255, 127)), DARKSLATEGRAY(new RGB(151, 255, 255)),
	// DEFAULTCONTEXT(
	// new RGB(255, 255, 255));

	DEFAULTCONTEXT(new RGB(255, 255, 255)), GREEN(new RGB(88, 174, 10)), BLUE_BRIGHT(
			new RGB(151, 255, 255)), BLUE(new RGB(135, 206, 235)), PURPLE(
			new RGB(167, 76, 242)), PINK(new RGB(205, 67, 211)), RED(new RGB(
			255, 140, 105)), ORANGE(new RGB(240, 170, 84)), YELLOW(new RGB(243,
			241, 79)), GREEN_BRIGHT(new RGB(70, 244, 69));

	private RGB color;

	CodeHighlighting(RGB color) {
		this.color = color;
	}

	public RGB getRGB() {
		return this.color;
	}

	public String getColorName() {
		return this.name().toLowerCase();
	}
}
