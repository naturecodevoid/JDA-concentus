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

package net.dv8tion.jda.internal.audio;

import net.dv8tion.jda.api.audio.OpusPacket;
import org.concentus.OpusDecoder;
import org.concentus.OpusException;

import java.nio.ByteBuffer;

/**
 * Class that wraps functionality around the Opus decoder.
 */
public class Decoder
{
    protected int ssrc;
    protected char lastSeq;
    protected int lastTimestamp;
    protected OpusDecoder opusDecoder;

    protected Decoder(int ssrc)
    {
        this.ssrc = ssrc;
        this.lastSeq = (char) -1;
        this.lastTimestamp = -1;

        try
        {
            opusDecoder = new OpusDecoder(OpusPacket.OPUS_SAMPLE_RATE, OpusPacket.OPUS_CHANNEL_COUNT);
        }
        catch (OpusException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isInOrder(char newSeq)
    {
        return lastSeq == (char) -1 || newSeq > lastSeq || lastSeq - newSeq > 10;
    }

    public boolean wasPacketLost(char newSeq)
    {
        return newSeq > lastSeq + 1;
    }

    public short[] decodeFromOpus(AudioPacket decryptedPacket)
    {
        int result;
        short[] decoded = new short[4096];
        if (decryptedPacket == null)    //Flag for packet-loss
        {
            try
            {
                result = opusDecoder.decode(null, 0, 0, decoded, 0, OpusPacket.OPUS_FRAME_SIZE, false);
            }
            catch (OpusException e)
            {
                // best error handling
                throw new RuntimeException(e);
            }
            lastSeq = (char) -1;
            lastTimestamp = -1;
        }
        else
        {
            this.lastSeq = decryptedPacket.getSequence();
            this.lastTimestamp = decryptedPacket.getTimestamp();

            ByteBuffer encodedAudio = decryptedPacket.getEncodedAudio();
            int length = encodedAudio.remaining();
            int offset = encodedAudio.arrayOffset() + encodedAudio.position();
            byte[] buf = new byte[length];
            byte[] data = encodedAudio.array();
            System.arraycopy(data, offset, buf, 0, length);
            try
            {
                result = opusDecoder.decode(buf, 0, buf.length, decoded, 0, OpusPacket.OPUS_FRAME_SIZE, false);
            }
            catch (OpusException e)
            {
                throw new RuntimeException(e);
            }
        }

        // we already have better error handling

        short[] audio = new short[result];
        System.arraycopy(decoded, 0, audio, 0, result);
        return audio;
    }

    protected synchronized void close()
    {
        if (opusDecoder != null)
        {
            opusDecoder = null;
        }
    }

    @Override
    @SuppressWarnings("deprecation") /* If this was in JDK9 we would be using java.lang.ref.Cleaner instead! */
    protected void finalize() throws Throwable
    {
        super.finalize();
        close();
    }
}
