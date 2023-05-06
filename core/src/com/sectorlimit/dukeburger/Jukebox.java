package com.sectorlimit.dukeburger;

import java.util.Arrays;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Jukebox {

	private Track m_currentTrack;
	private Music m_currentMusic;
	private float m_volume;
	private boolean m_enabled;
	private Vector<Music> m_musicTracks;

	private static final float DEFAULT_MUSIC_VOLUME = 0.13f;
	private static boolean DEFAULT_MUSIC_ENABLED = true;

	public enum Track {
		PixelDuke,
		City,
		Subway,
		BurgerPeople,
		AstroLounge
	}

	public Jukebox() {
		m_currentTrack = Track.PixelDuke;
		m_volume = DEFAULT_MUSIC_VOLUME;
		m_enabled = DEFAULT_MUSIC_ENABLED;

		m_musicTracks = new Vector<Music>(Arrays.asList(
			Gdx.audio.newMusic(Gdx.files.internal("music/pixelduke.mp3")),
			Gdx.audio.newMusic(Gdx.files.internal("music/city.mp3")),
			Gdx.audio.newMusic(Gdx.files.internal("music/subway.mp3")),
			Gdx.audio.newMusic(Gdx.files.internal("music/burger_people.mp3")),
			Gdx.audio.newMusic(Gdx.files.internal("music/astro_lounge.mp3"))
		));

		m_currentMusic = m_musicTracks.elementAt(m_currentTrack.ordinal());
	}

	public Track getCurrentTrack() {
		return m_currentTrack;
	}

	public boolean isPlaying() {
		return m_currentMusic.isPlaying();
	}

	public float getVolume() {
		return m_volume;
	}

	public void setVolume(float volume) {
		if(volume < 0.0f || volume > 1.0f) {
			return;
		}

		m_volume = volume;
	}

	public boolean isEnabled() {
		return m_enabled;
	}

	public void setEnabled(boolean enabled) {
		if(m_enabled == enabled) {
			return;
		}

		m_enabled = enabled;
	}

	public void enable() {
		setEnabled(true);
	}

	public void disable() {
		setEnabled(false);
	}

	public void play(Track track) {
		play(track, true);
	}

	public void play(Track track, boolean loop) {
		stop();

		m_currentTrack = track;
		m_currentMusic = m_musicTracks.elementAt(track.ordinal());
		m_currentMusic.setLooping(true);
		m_currentMusic.setVolume(m_volume);
		m_currentMusic.play();
	}

	public static Track getTrack(String trackName) {
		if(trackName.equalsIgnoreCase("pixel_duke") || trackName.equalsIgnoreCase("theme")) {
			return Track.PixelDuke;
		}
		else if(trackName.equalsIgnoreCase("city")) {
			return Track.City;
		}
		else if(trackName.equalsIgnoreCase("subway")) {
			return Track.Subway;
		}
		else if(trackName.equalsIgnoreCase("burger_people")) {
			return Track.BurgerPeople;
		}
		else if(trackName.equalsIgnoreCase("astro_lounge")) {
			return Track.AstroLounge;
		}

		return null;
	}

	public boolean play(String trackName) {
		return play(trackName, true);
	}

	public boolean play(String trackName, boolean looping) {
		Track track = getTrack(trackName);

		if(track == null) {
			return false;
		}

		play(track, looping);

		return true;
	}

	public void stop() {
		m_currentMusic.stop();
	}

	public void dispose() {
		for(Music music : m_musicTracks) {
			music.dispose();
		}
	}

}
