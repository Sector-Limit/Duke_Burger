package com.sectorlimit.dukeburger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TextRenderer {

	private Texture[] m_numberTextures;
	private Texture m_dotTexture;

	private static final float DIGIT_SPACING = 8.0f;
	private static final float DOT_SPACING = 2.0f;

	public TextRenderer() {
		m_numberTextures = new Texture[10];

		for(int i = 0; i < 10; i++) {
			m_numberTextures[i] = new Texture(Gdx.files.internal("sprites/" + i + ".png"));
		}

		Pixmap attackedOverlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		attackedOverlayPixmap.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		attackedOverlayPixmap.fill();
		m_dotTexture = new Texture(attackedOverlayPixmap);
	}

	public float getRenderedTextWidth(String text) {
		float textWidth = 0;

		for(int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);

			if(letter >= '0' && letter <= '9') {
				textWidth += DIGIT_SPACING;
			}
			else if(letter == '.') {
				textWidth += DOT_SPACING;
			}
		}

		return textWidth - 1.0f;
	}

	public float getRenderedTextHeight() {
		return m_numberTextures[0].getHeight();
	}

	public void renderText(SpriteBatch spriteBatch, String text, Vector2 position) {
		Vector2 digitPosition = new Vector2(position);

		for(int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);

			if(letter >= '0' && letter <= '9') {
				renderDigit(spriteBatch, (int) (letter - '0'), digitPosition);
				digitPosition.add(new Vector2(DIGIT_SPACING, 0.0f));
			}
			else if(letter == '.') {
				renderDot(spriteBatch, digitPosition);
				digitPosition.add(new Vector2(DOT_SPACING, 0.0f));
			}
		}
	}

	public void renderNumber(SpriteBatch spriteBatch, int number, Vector2 position) {
		renderNumber(spriteBatch, number, position, 0);
	}

	public void renderNumber(SpriteBatch spriteBatch, int number, Vector2 position, int minLength) {
		String numberString = Integer.toString(number); 
		int numberLength = numberString.length();
		Vector2 digitPosition = new Vector2(position);

		for(int i = 0; i < minLength - numberLength; i++) {
			renderDigit(spriteBatch, 0, digitPosition);
			digitPosition.add(new Vector2(DIGIT_SPACING, 0.0f));
		}

		for(int i = 0; i < numberLength; i++) {
			renderDigit(spriteBatch, Integer.parseInt("" + numberString.charAt(i)), digitPosition);
			digitPosition.add(new Vector2(DIGIT_SPACING, 0.0f));
		}
	}

	public void renderDigit(SpriteBatch spriteBatch, int digit, Vector2 position) {
		if(digit >= m_numberTextures.length) {
			return;
		}

		Texture digitTexture = m_numberTextures[digit];

		spriteBatch.draw(digitTexture, position.x, position.y, 0.0f, 0.0f, digitTexture.getWidth(), digitTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, digitTexture.getWidth(), digitTexture.getHeight(), false, false);
	}

	public void renderDot(SpriteBatch spriteBatch, Vector2 position) {
		spriteBatch.draw(m_dotTexture, position.x, position.y, 0.0f, 0.0f, m_dotTexture.getWidth(), m_dotTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, m_dotTexture.getWidth(), m_dotTexture.getHeight(), false, false);
	}

}
