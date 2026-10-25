package com.spacecasino.colonyterminal.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.spacecasino.colonyterminal.model.BgmMode
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sin

object SoundManager {

    private var audioTrack: AudioTrack? = null
    private var synthThread: Thread? = null
    private val isRunning = AtomicBoolean(false)
    private var currentMode = BgmMode.OFF

    private const val SAMPLE_RATE = 22050

    fun setBgmMode(mode: BgmMode) {
        if (currentMode == mode) return
        currentMode = mode
        stopBgm()

        if (mode != BgmMode.OFF) {
            startBgm(mode)
        }
    }

    private fun startBgm(mode: BgmMode) {
        isRunning.set(true)
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        synthThread = Thread {
            val chunk = ShortArray(1024)
            var sampleIndex = 0L

            // 8bit chiptune メロディ周波数 (C4, E4, G4, B4, C5, G4, E4, D4)
            val chipFrequencies = doubleArrayOf(
                261.63, 329.63, 392.00, 493.88, 523.25, 392.00, 329.63, 293.66
            )
            // SFチック ドローン低周波 (A2, E3, A3, D4)
            val scifiFrequencies = doubleArrayOf(
                110.0, 164.81, 220.0, 293.66
            )

            val noteDurationSamples = (SAMPLE_RATE * 0.22).toInt() // 約0.22秒毎に音符切替

            while (isRunning.get()) {
                val currentTrack = audioTrack ?: break
                for (i in chunk.indices) {
                    val time = sampleIndex.toDouble() / SAMPLE_RATE

                    val sampleValue: Double = if (mode == BgmMode.CHIPTUNE) {
                        // 8-bit 矩形波 (Square Wave) + ベースライン
                        val noteIdx = ((sampleIndex / noteDurationSamples) % chipFrequencies.size).toInt()
                        val freq = chipFrequencies[noteIdx]
                        val phase = (time * freq) % 1.0
                        val melody = if (phase < 0.5) 0.18 else -0.18

                        // サブベース (1オクターブ下)
                        val bassFreq = freq * 0.5
                        val bassPhase = (time * bassFreq) % 1.0
                        val bass = if (bassPhase < 0.5) 0.10 else -0.10

                        melody + bass
                    } else {
                        // SFチック サイン波 + FM変調 (Sci-Fi Ambient Drone)
                        val chordIdx = ((sampleIndex / (SAMPLE_RATE * 2)) % scifiFrequencies.size).toInt()
                        val baseFreq = scifiFrequencies[chordIdx]
                        // LFO (低周波変調)
                        val lfo = sin(2.0 * Math.PI * 0.8 * time) * 15.0
                        val carrier = sin(2.0 * Math.PI * (baseFreq + lfo) * time) * 0.16
                        // 高音の宇宙的キラキラ倍音
                        val harmonic = sin(2.0 * Math.PI * (baseFreq * 2.5) * time) * 0.08
                        carrier + harmonic
                    }

                    // 16bit PCMクリッピング防止
                    chunk[i] = (sampleValue.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
                    sampleIndex++
                }

                currentTrack.write(chunk, 0, chunk.size)
            }
        }.apply {
            isDaemon = true
            name = "SpaceColony-SoundSynth"
            start()
        }
    }

    fun stopBgm() {
        isRunning.set(false)
        try {
            synthThread?.join(300)
        } catch (_: Exception) {}
        synthThread = null

        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
