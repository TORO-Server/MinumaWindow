package marumasa.minuma_window.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.image.BufferedImage;

import static marumasa.minuma_window.MinumaWindow.MinumaIku_Sound;
import static marumasa.minuma_window.Utils.*;
import static org.lwjgl.opengl.GL11.*;

public class MinumaWindowClient implements ClientModInitializer {

    private static long window;

    private final byte[] minumaImage = getResource("/assets/minuma_window/minuma.png");
    private final byte[] iconImage = getResource("/assets/minuma_window/icon.png");

    @Override
    public void onInitializeClient() {


        BufferedImage image = toBufferedImage(minumaImage);
        // 画像の幅と高さを取得
        int width = image.getWidth();
        int height = image.getHeight();


        // GLFWのエラコールバックを設定
        GLFWErrorCallback.createPrint(System.err).set();
        // GLFWを初期化
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        // ウィンドウを作成時にウィンドウを表示する
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        // ウィンドウのサイズを変更不可にする
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        // 見沼のウィンドウを作成
        window = GLFW.glfwCreateWindow(width / 2, height / 2, "Minuma Window", 0, 0);
        // ウィンドウのアイコンを見沼にする。
        setWindowIcon(window, iconImage);


        // OpenGLコンテキストの初期化
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // 垂直同期をオフにする
        GLFW.glfwSwapInterval(0);

        // テクスチャの準備
        int MinumaTextureID = loadTexture(minumaImage);

        // 見沼レンダリング
        renderMinuma(MinumaTextureID);


        // 毎tick 実行
        ClientTickEvents.END_CLIENT_TICK.register(context -> {

            ClientPlayerEntity player = context.player;
            if (player == null) return;

            // OpenGLコンテキストの初期化
            GLFW.glfwMakeContextCurrent(window);
            GL.createCapabilities();

            if (player.hurtTime > 0) {// もしダメージを受けた後の無敵時間の場合
                // オーバーレイを赤くする
                renderOverlay(1.0f, 0.5f, 0.5f);
                if (player.hurtTime >= player.maxHurtTime - 1) {// 現在のtickでダメージを受けていた場合
                    // 効果音を再生
                    player.playSound(MinumaIku_Sound);
                }
            } else {// 銅でない場合は オーバーレイを初期状態にする
                renderOverlay(1.0f, 1.0f, 1.0f);
            }
            // 見沼をレンダリングする
            renderMinuma(MinumaTextureID);

            // OpenGLコンテキストの初期化
            GLFW.glfwMakeContextCurrent(context.getWindow().getHandle());
            GL.createCapabilities();
        });
    }

    // 見沼のレンダリング処理
    private static void renderMinuma(int textureID) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glBegin(GL_QUADS);


        glTexCoord2f(0, 0);
        // 左上の頂点
        glVertex2f(-1, 1);

        glTexCoord2f(1, 0);
        // 右上の頂点
        glVertex2f(1, 1);

        glTexCoord2f(1, 1);
        // 右下の頂点
        glVertex2f(1, -1);

        glTexCoord2f(0, 1);
        // 左下の頂点
        glVertex2f(-1, -1);


        glEnd();
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    // オーバーレイのレンダリング処理
    private void renderOverlay(float red, float green, float blue) {
        glPushMatrix();
        glLoadIdentity();

        glBegin(GL_QUADS);
        glColor3f(red, green, blue);
        glVertex2f(-0.5f, -0.5f);
        glVertex2f(0.5f, -0.5f);
        glVertex2f(0.5f, 0.5f);
        glVertex2f(-0.5f, 0.5f);
        glEnd();

        glPopMatrix();
    }
}
