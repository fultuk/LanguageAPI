/*
 * MIT License
 *
 * Copyright (c) 2021 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2021 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.tentact.languageapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateNotifier {

    private static final Pattern VERSION_PATTERN = Pattern.compile("([1-9\\.]+)-\\b(SNAPSHOT|RELEASE)-([a-zA-Z0-9]{7})$");

    private boolean hasUpdate = false;
    private String pluginVersion;
    private String onlineVersion;

    public void checkForUpdates(String pluginVersion, String pluginName, Logger logger) {
        Matcher matcher = VERSION_PATTERN.matcher(pluginVersion);
        if (!matcher.matches()) {
            return;
        }

        try {
            String onlineVersion = this.getOnlineVersion(pluginName);
            int parsedOnlineVersion = Integer.parseInt(onlineVersion.replace(".", ""));
            int parsedLocalVersion = Integer.parseInt(matcher.group(1).replace(".", ""));

            if (parsedLocalVersion > parsedOnlineVersion) {
                logger.info("You are running a snapshot version of the LanguageAPI (" + pluginVersion + ")");
            }
            if (parsedLocalVersion < parsedOnlineVersion) {
                logger.info("There is a new version available. Current version: " + pluginVersion + ", newest version: " + onlineVersion);

                this.hasUpdate = true;
                this.pluginVersion = pluginVersion;
                this.onlineVersion = onlineVersion;
            }
        } catch (IOException exception) {
            logger.warning("While creating connection to the webserver an error occurred.");
        }

    }

    private String getOnlineVersion(String pluginName) throws IOException {
        URL url = new URL("https://tentact.de/plugins?" + pluginName);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }

    public boolean hasUpdate() {
        return this.hasUpdate;
    }

    public String getOnlineVersion() {
        return this.onlineVersion;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }
}
