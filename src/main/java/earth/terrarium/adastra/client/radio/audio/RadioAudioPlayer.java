package earth.terrarium.adastra.client.radio.audio;

import earth.terrarium.adastra.AdAstraReborn;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

@SideOnly(Side.CLIENT)
final class RadioAudioPlayer {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final String url;
    private volatile float volume = 1.0F;
    private volatile boolean finished;
    private Thread thread;
    private InputStream input;
    private SourceDataLine line;

    RadioAudioPlayer(String url) {
        this.url = url;
    }

    void start() {
        thread = new Thread(this::run, "Ad Astra Radio Stream");
        thread.setDaemon(true);
        thread.start();
    }

    void stop() {
        running.set(false);
        closeInput();
        closeLine();
    }

    void setVolume(float volume) {
        this.volume = Math.max(0.0F, Math.min(volume, 1.0F));
        SourceDataLine currentLine = line;
        if (currentLine != null) {
            applyVolume(currentLine, this.volume);
        }
    }

    boolean isFinished() {
        return finished;
    }

    private void run() {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            input = connection.getInputStream();
            decode(input);
        } catch (Throwable throwable) {
            if (running.get()) {
                AdAstraReborn.LOGGER.warn("Failed to play radio stream: {}", url, throwable);
            }
        } finally {
            finished = true;
            closeInput();
            closeLine();
        }
    }

    private void decode(InputStream stream) throws Exception {
        Bitstream bitstream = new Bitstream(stream);
        Decoder decoder = new Decoder();
        try {
            while (running.get()) {
                Header header = bitstream.readFrame();
                if (header == null) {
                    break;
                }
                try {
                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);
                    SourceDataLine currentLine = ensureLine(output);
                    byte[] bytes = toBytes(output);
                    if (bytes.length > 0) {
                        currentLine.write(bytes, 0, bytes.length);
                    }
                } finally {
                    bitstream.closeFrame();
                }
            }
        } finally {
            bitstream.close();
        }
    }

    private SourceDataLine ensureLine(SampleBuffer output) throws LineUnavailableException {
        if (line != null) {
            return line;
        }

        AudioFormat format = new AudioFormat(output.getSampleFrequency(), 16, output.getChannelCount(), true, false);
        SourceDataLine createdLine = AudioSystem.getSourceDataLine(format);
        createdLine.open(format);
        applyVolume(createdLine, volume);
        createdLine.start();
        line = createdLine;
        return createdLine;
    }

    private static byte[] toBytes(SampleBuffer output) {
        short[] samples = output.getBuffer();
        int length = output.getBufferLength();
        byte[] bytes = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            short sample = samples[i];
            bytes[i * 2] = (byte) (sample & 0xFF);
            bytes[i * 2 + 1] = (byte) ((sample >>> 8) & 0xFF);
        }
        return bytes;
    }

    private static void applyVolume(SourceDataLine line, float volume) {
        if (!line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        float clamped = Math.max(0.0001F, Math.min(volume, 1.0F));
        float gain = 20.0F * (float) Math.log10(clamped);
        control.setValue(Math.max(control.getMinimum(), Math.min(gain, control.getMaximum())));
    }

    private void closeInput() {
        InputStream currentInput = input;
        input = null;
        if (currentInput != null) {
            try {
                currentInput.close();
            } catch (IOException ignored) {
                // Closing is best-effort during playback shutdown.
            }
        }
    }

    private void closeLine() {
        SourceDataLine currentLine = line;
        line = null;
        if (currentLine != null) {
            currentLine.stop();
            currentLine.flush();
            currentLine.close();
        }
    }
}
