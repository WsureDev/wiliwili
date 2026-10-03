package cn.xfangfang.wiliwili;

import android.os.Bundle;

import org.libsdl.app.BorealisHandler;
import org.libsdl.app.PlatformUtils;
import org.libsdl.app.SDLActivity;

/** Android entry point for the wiliwili native SDL2 application. */
public class WiliwiliActivity extends SDLActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlatformUtils.borealisHandler = new BorealisHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // SDL2 keeps process-global state. A fresh process is required after an
        // Activity recreation so the native application can initialize cleanly.
        System.exit(0);
    }

    @Override
    protected String[] getLibraries() {
        return new String[] {"curl", "SDL2", "wiliwili"};
    }
}
