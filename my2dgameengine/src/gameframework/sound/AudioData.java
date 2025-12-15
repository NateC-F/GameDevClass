package gameframework.sound;

import javax.sound.sampled.AudioFormat;

public class AudioData
{
    public final byte[]  audioBytes;
    public final AudioFormat format;

    public AudioData(byte[] audioBytes, AudioFormat format)
    {
        this.audioBytes = audioBytes;
        this.format = format;
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public AudioFormat getFormat() {
        return format;
    }
}
