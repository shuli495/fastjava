package com.fastjavaframework.util;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

/**
 * 图片工具类
 */
public class ImageUtil {
	
	/**
	 * 文字长度
	 * @param graphics	Graphics2D
	 * @param font		字体信息(字号/字体)
	 * @param context	文字内容
	 * @return 文字像素长度
	 */
	public static int getFontWidth(Graphics2D graphics,Font font,String context) {
		Rectangle2D bounds = font.getStringBounds(context, graphics.getFontRenderContext());
		return (int)bounds.getWidth();
	}
	
	/**
	 * 文字嵌入图片
	 * @param graphics Graphics2D
	 * @param font	     字体
	 * @param context 内容
	 * @param x	x坐标
	 * @param y y坐标
	 */
	public static void drawFont(Graphics2D graphics,Font font, String context, int x, int y) {
		//不透明
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		graphics.setComposite(ac);
		graphics.setFont(font);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.drawString(context, x, y);
	}
	
	/**
	 * 按大小缩放图片
	 * @param img	图片源
	 * @param width  缩放宽度
	 * @param height 缩放高度
	 * @param model 
	 * 			image.SCALE_SMOOTH //平滑优先
	 * 			image.SCALE_FAST//速度优先
	 * @return
	 */
	public static Image zoom(Image img, int width, int height, int model) {
		model = model == 0 ? Image.SCALE_SMOOTH : model;
		img = img.getScaledInstance(width, height, model);
		return new ImageIcon(img).getImage();
	}
	
	/**
	 * 按比例缩放图片
	 * @param img	图片源
	 * @param percent 缩放比例
	 * @param model 
	 * 			image.SCALE_SMOOTH //平滑优先
	 * 			image.SCALE_FAST//速度优先
	 * @return
	 */
	public static Image zoom(Image img, float percent, int model) {
		model = model == 0 ? Image.SCALE_SMOOTH : model;
		int width = (int)(img.getWidth(null)*percent);
		int height = (int)(img.getHeight(null)*percent);
		img = img.getScaledInstance(width, height, model);
		return new ImageIcon(img).getImage();
	}
	
}
