package com.taobao.arthas.core.shell.term.impl;


import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.taobao.arthas.core.shell.ShellServerOptions;
import com.taobao.arthas.core.util.LogUtil;
import com.taobao.middleware.logger.Logger;
import io.termd.core.readline.Keymap;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Helper {
    private static Logger logger = LogUtil.getArthasLogger();

    private static final String DEFAULT_INPUT_RC = "\"\\e[D\": backward-char\n" +
            "\"\\e[C\": forward-char\n" +
            "\"\\e[B\": next-history\n" +
            "\"\\e[A\": previous-history\n" +
            "\"\\C-?\": backward-delete-char\n" +
            "\"\\C-h\": backward-delete-char\n" +
            "\"\\C-X\\C-U\": undo\n" +
            "\"\\eb\": backward-word\n" +
            "\"\\C-e\": end-of-line\n" +
            "\"\\C-a\": beginning-of-line\n" +
            "\"\\C-D\": delete-char\n" +
            "\"\\e[3~\": delete-char\n" +
            "\"\\C-i\": complete\n" +
            "\"\\C-j\": accept-line\n" +
            "\"\\C-m\": accept-line\n" +
            "\"\\C-k\": kill-line\n" +
            "\"\\eb\": backward-word\n" +
            "\"\\ef\": forward-word\n" +
            "\"\\e\\C-?\": backward-kill-word\n" +
            "\"\\C-x[3~\": backward-kill-line\n";

    private static final ByteSource DEFAULT_BYTE_SOURCE = ByteSource.wrap(DEFAULT_INPUT_RC.getBytes(Charsets.UTF_8));

    public static Keymap loadKeymap() {
        return new Keymap(loadInputRcFile());
    }

    public static InputStream loadInputRcFile() {
        InputStream inputrc;
        // step 1: load custom keymap file
        try {
            String customInputrc = System.getProperty("user.home") + "/.arthas/conf/inputrc";
            inputrc = new FileInputStream(customInputrc);
            logger.info("Loaded custom keymap file from " + customInputrc);
            return inputrc;
        } catch (Throwable e) {
            // ignore
        }

        // step 2: load arthas default keymap file
        inputrc = getDefaultStream();
        if (inputrc != null) {
            logger.info("Loaded arthas keymap file from " + ShellServerOptions.DEFAULT_INPUTRC);
            return inputrc;
        }

        // step 3: fall back to termd default keymap file
        inputrc = Keymap.class.getResourceAsStream("inputrc");
        if (inputrc != null) {
            return inputrc;
        }

        throw new IllegalStateException("Could not load inputrc file.");
    }

    private static InputStream getDefaultStream() {
        try {
            return DEFAULT_BYTE_SOURCE.openStream();
        } catch (Exception e) {
            logger.error("", "can not load default input rc?", e);
            return null;
        }
    }

}
