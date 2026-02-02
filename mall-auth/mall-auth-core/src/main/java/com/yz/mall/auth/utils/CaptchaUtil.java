package com.yz.mall.auth.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码工具类
 *
 * @author yunze
 * @date 2025/01/20
 */
public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String NUMBER_STRING = "0123456789";

    /**
     * 生成验证码图片和验证码值
     *
     * @return CaptchaResult 包含验证码图片和验证码值
     */
    public static CaptchaResult generateCaptcha() {
        // 生成验证码字符串
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(NUMBER_STRING.charAt(random.nextInt(NUMBER_STRING.length())));
        }

        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置背景色（浅色背景）
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制验证码字符
        for (int i = 0; i < CODE_LENGTH; i++) {
            String text = String.valueOf(code.charAt(i));
            int fontSize = random.nextInt(23) + 18; // 18-41
            int deg = random.nextInt(60) - 30; // -30到30度
            
            // 使用系统默认字体，确保字体存在
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
            g.setFont(font);
            // 使用深色文字，确保与浅色背景对比明显
            g.setColor(getRandomColor(50, 120));
            
            // 计算字符位置（每个字符间隔约25像素）
            int x = 15 + i * 25;
            int y = HEIGHT / 2 + fontSize / 3; // 垂直居中
            
            // 保存当前变换
            AffineTransform oldTransform = g.getTransform();
            // 移动到字符绘制位置
            g.translate(x, y);
            // 旋转
            g.rotate(Math.toRadians(deg));
            // 绘制文字（drawString的y坐标是基线位置，所以从0开始绘制）
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            // 从中心点绘制，y=0是基线位置
            g.drawString(text, -textWidth / 2, 0);
            // 恢复变换
            g.setTransform(oldTransform);
        }

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(getRandomColor(180, 230));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制干扰点
        for (int i = 0; i < 41; i++) {
            g.setColor(getRandomColor(150, 200));
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillOval(x, y, 2, 2);
        }

        g.dispose();

        return new CaptchaResult(image, code.toString());
    }

    /**
     * 获取随机颜色
     *
     * @param min 最小值
     * @param max 最大值
     * @return 颜色
     */
    private static Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = random.nextInt(max - min) + min;
        int g = random.nextInt(max - min) + min;
        int b = random.nextInt(max - min) + min;
        return new Color(r, g, b);
    }

    /**
     * 验证码结果
     */
    public static class CaptchaResult {
        private final BufferedImage image;
        private final String code;

        public CaptchaResult(BufferedImage image, String code) {
            this.image = image;
            this.code = code;
        }

        public BufferedImage getImage() {
            return image;
        }

        public String getCode() {
            return code;
        }
    }
}
