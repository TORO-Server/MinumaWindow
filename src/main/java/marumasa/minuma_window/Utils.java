package marumasa.minuma_window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

public class Utils {
    // ウィンドウのアイコンを設定
    public static void setWindowIcon(long window, byte[] imageBytes) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer image = ByteBuffer.allocateDirect(imageBytes.length);
            image.put(imageBytes);
            image.flip();

            // STBImageで画像をデコード
            image = STBImage.stbi_load_from_memory(image, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to decode image: " + STBImage.stbi_failure_reason());
            }

            GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
            iconBuffer.width(w.get());
            iconBuffer.height(h.get());
            iconBuffer.pixels(image);

            // ウィンドウのアイコンを設定
            GLFW.glfwSetWindowIcon(window, iconBuffer);

            // メモリを解放
            STBImage.stbi_image_free(image);
            iconBuffer.free();
        }
    }

    // BufferedImage に変換
    public static BufferedImage toBufferedImage(byte[] bytes) {
        // バイト配列を ByteArrayInputStream に変換
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        // ByteArrayInputStream を使用して BufferedImage を作成
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // リソースフォルダにあるファイルの内容を取得
    public static byte[] getResource(String path) {
        try (InputStream is = MinumaWindow.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Failed to load icon image: Resource not found " + path);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load icon image", e);
        }
    }

    // テクスチャ読み込み
    public static int loadTexture(byte[] imageBytes) {
        int width, height;
        ByteBuffer image = ByteBuffer.allocateDirect(imageBytes.length);
        image.put(imageBytes);
        image.flip();

        // 画像の読み込み
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            image = STBImage.stbi_load_from_memory(image, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        // テクスチャの作成
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        STBImage.stbi_image_free(image);

        return textureID;
    }
}
