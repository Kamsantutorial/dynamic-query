package com.example.crudrestapi.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javax.imageio.ImageIO;
import org.springframework.core.io.ClassPathResource;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@UtilityClass
@Slf4j
public class QRCodeGenerateUtil {

    private final String DIR = "./static/khqr/uploads/test/";
    private final String ext = ".png";
    private final String DOLLAR_LOGO = "./static/khqr/logos/dollar.png";
    private final String KHQR_LOGO = "./static/khqr/logos/khqr-logo.png";
    private final String CONTENT = "8465d722d7d5065f2886f0a474a4d34dc6a7855355b611836f7b6111228893e9";
    private final int WIDTH = 400;
    private final int HEIGHT = 400;
    private final int HEADER_LABEL_HEIGHT = (HEIGHT * 15) / 100;
    private final int HEADER_HEIGHT = HEADER_LABEL_HEIGHT * 3;
    private final  String TITLE = "KHQR";

    public void generate() {
        // Create new configuration that specifies the error correction
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter writer = new QRCodeWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {

            createNewDirectory();

            BitMatrix bitMatrix = writer.encode(CONTENT, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            // Load QR image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, getMatrixConfig());

            // Load logo image
            BufferedImage dollarLogo = getOverlyLogo(DOLLAR_LOGO, 50, 50);
            // Load KHQR logo image
            BufferedImage khdollarLogo = getOverlyLogo(KHQR_LOGO, 90, HEADER_LABEL_HEIGHT / 3);

            int deltaHeight = qrImage.getHeight() - dollarLogo.getHeight();
            int deltaWidth = qrImage.getWidth() - dollarLogo.getWidth();

            // Initialize combined image
            BufferedImage combined = new BufferedImage(qrImage.getWidth(), qrImage.getHeight() + HEADER_HEIGHT,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();

            g.setColor(new Color(225, 35, 46));
            g.fillRect(0, 0, qrImage.getWidth(), qrImage.getHeight() +
                    HEADER_LABEL_HEIGHT);

            g.setColor(Color.WHITE);
            g.fillRect(0, HEADER_LABEL_HEIGHT, qrImage.getWidth(), qrImage.getHeight() +
                    HEADER_HEIGHT);

            // Triangle
            int[] xPoints = { qrImage.getWidth(), qrImage.getWidth(), qrImage.getWidth() - 35 };
            int[] yPoints = { 96, HEADER_LABEL_HEIGHT, HEADER_LABEL_HEIGHT };
            g.setColor(new Color(225, 35, 46));
            g.fillPolygon(xPoints, yPoints, 3);

            // Register Font Nunito
            registerFont();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Nunito Sans", Font.PLAIN, 18));
            for (String fontName : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
                //check available font
                log.info("font name: {}", fontName);
            }
            g.setColor(new Color(0, 0, 0));
            // Draw Hello World String
            g.drawString("My Merchant", 50, (HEADER_HEIGHT / 2) + 30);


            g.drawString(String.format("%s %s", "100.00", Currency.USD), 50, (HEADER_HEIGHT / 2) + 60);

            float[] dashingPattern1 = { 8f, 2f };
            Stroke stroke1 = new BasicStroke(2f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 1.0f);

            g.setColor(new Color(0, 0, 0));
            g.setStroke(stroke1);
            g.drawLine(0, HEADER_HEIGHT, qrImage.getWidth(), HEADER_HEIGHT);

            // Write QR code to new image at position 0/0
            g.drawImage(qrImage, 0, HEADER_HEIGHT, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));


            g.drawImage(khdollarLogo, (int) Math.round((qrImage.getWidth() - khdollarLogo.getWidth()) / 2),
                    (int) (Math.round((HEADER_LABEL_HEIGHT - khdollarLogo.getHeight()) / 2)),
                    null);
            g.drawImage(dollarLogo, (int) Math.round(deltaWidth / 2), (int) (Math.round(deltaHeight / 2) + HEADER_HEIGHT),
                    null);

            BufferedImage rounded = makeRoundedCorner(combined, 50);

            // Write combined image as PNG to OutputStream
            ImageIO.write(rounded, "PNG", os);
            // Store Image
            Files.copy(new ByteArrayInputStream(os.toByteArray()),
                    Paths.get(DIR, TITLE + ext), StandardCopyOption.REPLACE_EXISTING);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = output.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        g.setComposite(AlphaComposite.SrcAtop);
        g.drawImage(image, 0, 0, null);

        g.dispose();

        return output;
    }

    private BufferedImage getOverlyLogo(String LOGO, int newW, int newH) throws IOException {
        ClassPathResource res = new ClassPathResource(LOGO);
        return resize(ImageIO.read(res.getFile()), newW, newH);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = dimg.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        g.dispose();

        return dimg;
    }

    public void registerFont() {
        ClassPathResource res = new ClassPathResource("./static/khqr/fonts/Nunito_Sans/nunito-sans.regular.ttf");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, res.getFile()));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewDirectory() throws IOException {
        Files.createDirectories(Paths.get(QRCodeGenerateUtil.DIR));
    }

    private void cleanDirectory() {
        try {
            Files.walk(Paths.get(QRCodeGenerateUtil.DIR), FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            // Directory does not exist, Do nothing
        }
    }

    private MatrixToImageConfig getMatrixConfig() {
        return new MatrixToImageConfig(Colors.BLACK.getArgb(), Colors.WHITE.getArgb());
    }

    private String generateRandoTitle(Random random, int length) {
        return random.ints(48, 122).filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97)).mapToObj(i -> (char) i)
                .limit(length).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    public enum Colors {

        RED(0xE1232E), WHITE(0xFFFFFFFF), BLACK(0xFF000000);

        private final int argb;

        Colors(final int argb) {
            this.argb = argb;
        }

        public int getArgb() {
            return argb;
        }
    }

    public enum Currency {
        USD
    }

}

