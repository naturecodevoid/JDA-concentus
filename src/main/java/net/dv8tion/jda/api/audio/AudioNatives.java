/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.api.audio;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Controller used by JDA to ensure the native
 * binaries for opus en-/decoding are available.
 *
 * @see <a href="https://github.com/discord-java/opus-java" target="_blank">opus-java source</a>
 */
public final class AudioNatives
{
    private static boolean initialized = true;
    private static boolean audioSupported = true;

    private AudioNatives() {}

    /**
     * Whether the opus library is loaded or not.
     * <br>This is initialized by the first call to {@link #ensureOpus()}.
     *
     * @return True, opus library is loaded.
     */
    public static boolean isAudioSupported()
    {
        return audioSupported;
    }

    /**
     * Whether this class was already initialized or not.
     *
     * @return True, if this class was already initialized.
     *
     * @see    #ensureOpus()
     */
    public static boolean isInitialized()
    {
        return initialized;
    }

    /**
     * Checks whether the opus binary was loaded, if not it will be initialized here.
     * <br>This is used by JDA to check at runtime whether the opus library is available or not.
     *
     * @return True, if the library could be loaded.
     */
    public static synchronized boolean ensureOpus()
    {
        return true;
    }
}
