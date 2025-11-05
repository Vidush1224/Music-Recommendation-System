package main.java.ui;

import main.java.model.Song;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer extends JPanel {

    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_MUTED = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private Clip audioClip;
    private Song currentSong;
    private boolean isPlaying = false;
    private long clipTimePosition = 0;
    private FloatControl volumeControl;

    private JLabel songTitleLabel;
    private JLabel artistLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;
    private JButton playPauseButton;
    private JButton stopButton;
    private JSlider volumeSlider;
    private JSlider progressSlider;
    private Timer progressTimer;

    public MusicPlayer() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);

        JPanel progressPanel = createProgressPanel();
        add(progressPanel, BorderLayout.CENTER);

        JPanel controlsPanel = createControlsPanel();
        add(controlsPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(BG_WHITE);

        songTitleLabel = new JLabel("No Song Playing");
        songTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        songTitleLabel.setForeground(TEXT_DARK);
        panel.add(songTitleLabel, BorderLayout.NORTH);

        artistLabel = new JLabel("Select a song to play");
        artistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        artistLabel.setForeground(TEXT_MUTED);
        panel.add(artistLabel, BorderLayout.CENTER);

        statusLabel = new JLabel("⏸ Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(SUCCESS);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        timeLabel = new JLabel("0:00 / 0:00");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(TEXT_MUTED);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(timeLabel, BorderLayout.NORTH);

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(BG_WHITE);
        progressSlider.setEnabled(false);
        panel.add(progressSlider, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BG_WHITE);

        JPanel playbackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        playbackPanel.setBackground(BG_WHITE);

        playPauseButton = createRoundButton("Play/Pause");
        playPauseButton.setBackground(PRIMARY);
        playPauseButton.setPreferredSize(new Dimension(50, 50));
        playPauseButton.addActionListener(e -> togglePlayPause());
        playbackPanel.add(playPauseButton);

        stopButton = createRoundButton("Stop");
        stopButton.setBackground(DANGER);
        stopButton.setPreferredSize(new Dimension(50, 50));
        stopButton.addActionListener(e -> stopMusic());
        playbackPanel.add(stopButton);

        panel.add(playbackPanel, BorderLayout.WEST);

        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        volumePanel.setBackground(BG_WHITE);

        JLabel volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        volumePanel.add(volumeLabel);

        volumeSlider = new JSlider(0, 100, 80);
        volumeSlider.setPreferredSize(new Dimension(120, 30));
        volumeSlider.setBackground(BG_WHITE);
        volumeSlider.addChangeListener(e -> setVolume(volumeSlider.getValue()));
        volumePanel.add(volumeSlider);

        panel.add(volumePanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createRoundButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void playSong(Song song) {
        if (song == null || song.getFilePath() == null || song.getFilePath().isEmpty()) {
            updateStatus("❌ Error: No file path", DANGER);
            JOptionPane.showMessageDialog(this,
                    "Song file path not available!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (audioClip != null && audioClip.isRunning()) {
            stopMusic();
        }

        try {
            File audioFile = new File(song.getFilePath());

            System.out.println("=== AUDIO DEBUG ===");
            System.out.println("Trying to play: " + song.getTitle());
            System.out.println("File path: " + song.getFilePath());
            System.out.println("Absolute path: " + audioFile.getAbsolutePath());
            System.out.println("File exists: " + audioFile.exists());
            System.out.println("Can read: " + audioFile.canRead());

            if (!audioFile.exists()) {
                updateStatus("❌ File not found", DANGER);
                JOptionPane.showMessageDialog(this,
                        "Audio file not found at:\n" + audioFile.getAbsolutePath() +
                                "\n\nPlease check:\n" +
                                "1. File exists in music/ folder\n" +
                                "2. File path in database is correct\n" +
                                "3. File name matches exactly (case-sensitive)",
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check file format
            String fileName = audioFile.getName().toLowerCase();
            if (fileName.endsWith(".mp3")) {
                updateStatus("⚠ MP3 detected", PRIMARY);
                int result = JOptionPane.showConfirmDialog(this,
                        "MP3 format detected!\n\n" +
                                "Java Sound API has limited MP3 support.\n" +
                                "If you don't hear sound:\n" +
                                "1. Convert MP3 to WAV format\n" +
                                "2. Or add JLayer library for MP3 support\n\n" +
                                "Try to play anyway?",
                        "MP3 Format Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            updateStatus("⏳ Loading...", PRIMARY);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();

            System.out.println("Audio format: " + format);
            System.out.println("Sample rate: " + format.getSampleRate());
            System.out.println("Channels: " + format.getChannels());

            // Convert if needed
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false);
                audioStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
            }

            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);

            // Get volume control
            try {
                volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(volumeSlider.getValue());
                System.out.println("Volume control available: YES");
            } catch (Exception e) {
                volumeControl = null;
                System.out.println("Volume control available: NO");
            }

            currentSong = song;
            updateSongInfo();

            audioClip.start();
            isPlaying = true;
            playPauseButton.setText("Pause");
            progressSlider.setEnabled(true);
            updateStatus("Playing", SUCCESS);

            startProgressTimer();

            System.out.println("Playback started successfully!");
            System.out.println("==================");

        } catch (UnsupportedAudioFileException e) {
            updateStatus("❌ Unsupported format", DANGER);
            System.err.println("Unsupported audio format: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Unsupported audio format!\n\n" +
                            "Supported formats: WAV, AU, AIFF\n" +
                            "For MP3: Convert to WAV or add JLayer library\n\n" +
                            "Error: " + e.getMessage(),
                    "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            updateStatus("❌ Read error", DANGER);
            System.err.println("IO Error: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error reading audio file:\n" + e.getMessage(),
                    "IO Error", JOptionPane.ERROR_MESSAGE);
        } catch (LineUnavailableException e) {
            updateStatus("❌ Audio line error", DANGER);
            System.err.println("Line unavailable: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Audio system error:\n" + e.getMessage() +
                            "\n\nTry:\n1. Restart the application\n2. Check system audio",
                    "Audio System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void togglePlayPause() {
        if (audioClip == null) {
            updateStatus("⚠ No song loaded", PRIMARY);
            JOptionPane.showMessageDialog(this,
                    "No song loaded! Please select a song to play.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (isPlaying) {
            clipTimePosition = audioClip.getMicrosecondPosition();
            audioClip.stop();
            isPlaying = false;
            playPauseButton.setText("Pause");
            updateStatus("Paused", PRIMARY);
            if (progressTimer != null)
                progressTimer.stop();
        } else {
            audioClip.setMicrosecondPosition(clipTimePosition);
            audioClip.start();
            isPlaying = true;
            playPauseButton.setText("Play");
            updateStatus("Playing", SUCCESS);
            startProgressTimer();
        }
    }

    private void stopMusic() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.setMicrosecondPosition(0);
            clipTimePosition = 0;
        }

        if (progressTimer != null) {
            progressTimer.stop();
        }

        isPlaying = false;
        playPauseButton.setText("▶");
        progressSlider.setValue(0);
        progressSlider.setEnabled(false);

        songTitleLabel.setText("No Song Playing");
        artistLabel.setText("Select a song to play");
        timeLabel.setText("0:00 / 0:00");
        updateStatus("⏹ Stopped", TEXT_MUTED);
    }

    private void setVolume(int value) {
        if (volumeControl != null) {
            try {
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float volume = min + (max - min) * (value / 100.0f);
                volumeControl.setValue(volume);
                System.out.println("Volume set to: " + value + "% (" + volume + " dB)");
            } catch (Exception e) {
                System.err.println("Failed to set volume: " + e.getMessage());
            }
        }
    }

    private void updateSongInfo() {
        if (currentSong != null) {
            songTitleLabel.setText("🎵 " + currentSong.getTitle());
            artistLabel.setText("🎤 " + currentSong.getArtist() + " • " + currentSong.getGenre());
        }
    }

    private void updateStatus(String status, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        }
    }

    private void startProgressTimer() {
        if (progressTimer != null) {
            progressTimer.stop();
        }

        progressTimer = new Timer(100, e -> {
            if (audioClip != null && audioClip.isRunning()) {
                long current = audioClip.getMicrosecondPosition();
                long total = audioClip.getMicrosecondLength();

                int progress = (int) ((current * 100.0) / total);
                progressSlider.setValue(progress);

                int currentSeconds = (int) (current / 1_000_000);
                int totalSeconds = (int) (total / 1_000_000);
                updateTimeLabel(currentSeconds, totalSeconds);
            }
        });
        progressTimer.start();
    }

    private void updateTimeLabel(int currentSeconds, int totalSeconds) {
        String current = String.format("%d:%02d", currentSeconds / 60, currentSeconds % 60);
        String total = String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
        timeLabel.setText(current + " / " + total);
    }

    public void cleanup() {
        if (progressTimer != null) {
            progressTimer.stop();
        }
        if (audioClip != null) {
            audioClip.close();
        }
    }
}