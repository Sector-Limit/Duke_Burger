package com.sectorlimit.dukeburger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class CheatCodeHandler implements InputProcessor {

	private Duke m_duke;
	private String m_currentCheatCode;
	private int m_currentCheatCodeProgress;
	private float m_lastKeyPressTimeElapsed;

	private static final float KEY_PRESS_TIMEOUT = 2.0f;
	private static final String CHEAT_CODE_PREFIX = "DB";
	private static final String[] CHEAT_CODES = {
		"KROZ",    // god mode
		"STUFF",   // full health and lives
		"SCOTTY",  // level warp
		"CASHMAN", // 99 coins
		"BURGER",  // teleport burger
		"DEBUG",   // enable debug camera
		"BRULLOV", // teleport to exit door
		"NITRO",   // toggle player flipped upside down
		"HYPER",   // toggle steroids
		"GRAV",    // toggle low gravity
		"KILL",    // suicide
		"THANOS",  // kill all enemies
		"TEST"     // warp to test level
	};

	public CheatCodeHandler(Duke duke) {
		m_duke = duke;
		m_currentCheatCode = "";
		m_currentCheatCodeProgress = 0;
		m_lastKeyPressTimeElapsed = 0.0f;

		Gdx.input.setInputProcessor(this);
	}

	public boolean handleCheatCodeInput() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		m_lastKeyPressTimeElapsed += deltaTime;

		if(m_currentCheatCodeProgress != 0 && m_lastKeyPressTimeElapsed > KEY_PRESS_TIMEOUT) {
			m_currentCheatCode = "";
			m_currentCheatCodeProgress = 0;
			m_lastKeyPressTimeElapsed = 0.0f;
		}

		return !m_currentCheatCode.isEmpty();
	}

	private void handleCheatCode(int cheatCodeNumber) {
		handleCheatCode(cheatCodeNumber, -1);
	}

	private void handleCheatCode(int cheatCode, int special) {
		m_currentCheatCode = "";
		m_currentCheatCodeProgress = 0;
		m_lastKeyPressTimeElapsed = 0.0f;

		if(cheatCode == 0) {
			m_duke.toggleGodMode();

			System.out.println("God mode " + (m_duke.isGodModeEnabled() ? "enabled" : "disabled") + ".");
		}
		else if(cheatCode == 1) {
			System.out.println("Giving max lives, health, and a barrel.");

			if(m_duke.getLives() < Duke.MAX_LIVES) {
				m_duke.setLives(Duke.MAX_LIVES);
			}

			m_duke.setHealth(Duke.MAX_HEALTH);
			m_duke.giveBarrel();
		}
		else if(cheatCode == 2) {
			System.out.println("Warping to level " + special + ".");

			m_duke.warpLevel(special);
		}
		else if(cheatCode == 3) {
			System.out.println("Setting coin counter to 99.");

			m_duke.setCoins(99);
		}
		else if(cheatCode == 4) {
			System.out.println("Picking up burger.");

			m_duke.pickupBurger();
		}
		else if(cheatCode == 5) {
			System.out.println("Enabling debug camera.");

			m_duke.enableDebugCamera();
		}
		else if(cheatCode == 6) {
			System.out.println("Teleporting to exit door.");

			m_duke.teleportToDoor();
		}
		else if(cheatCode == 7) {
			System.out.println("Toggling player flipped state.");

			m_duke.toggleFlipped();
		}
		else if(cheatCode == 8) {
			System.out.println("Toggling steroids.");

			m_duke.toggleSteroids();
		}
		else if(cheatCode == 9) {
			System.out.println("Toggling low gravity.");

			m_duke.toggleLowGravity();
		}
		else if(cheatCode == 10) {
			System.out.println("Suicided.");

			m_duke.kill();
		}
		else if(cheatCode == 11) {
			System.out.println("Killing all enemies.");

			m_duke.killAllEnemies();
		}
		else if(cheatCode == 12) {
			System.out.println("Warping to test level " + special + ".");

			m_duke.warpTestLevel(special);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(m_currentCheatCodeProgress < CHEAT_CODE_PREFIX.length()) {
			if(CHEAT_CODE_PREFIX.charAt(m_currentCheatCodeProgress) == Character.toUpperCase(character)) {
				m_lastKeyPressTimeElapsed = 0.0f;
				m_currentCheatCodeProgress++;
			}
			else {
				m_currentCheatCodeProgress = 0;
			}

			return false;
		}

		boolean isMissionWarp = m_currentCheatCode.equals(CHEAT_CODES[2]);
		boolean isTestLevelWarp = m_currentCheatCode.equals(CHEAT_CODES[12]);

		if(isMissionWarp || isTestLevelWarp) {
			if(character >= '0' && character <= '9') {
				handleCheatCode(isMissionWarp ? 2 : 12, (int) (character - '0'));
			}
			else {
				m_currentCheatCode = "";
				m_currentCheatCodeProgress = 0;
				m_lastKeyPressTimeElapsed = 0.0f;
			}

			return false;
		}

		m_lastKeyPressTimeElapsed = 0.0f;
		m_currentCheatCode += Character.toUpperCase(character);
		m_currentCheatCodeProgress++;

		boolean partialCheatCodeMatchFound = false;

		for(int i = 0; i < CHEAT_CODES.length; i++) {
			if(CHEAT_CODES[i].startsWith(m_currentCheatCode)) {
				partialCheatCodeMatchFound = true;
				break;
			}
		}

		if(!partialCheatCodeMatchFound) {
			m_currentCheatCode = "";
			m_currentCheatCodeProgress = 0;
			m_lastKeyPressTimeElapsed = 0.0f;

			return false;
		}

		for(int i = 0; i < CHEAT_CODES.length; i++) {
			if(i == 2 || i == 12) {
				continue;
			}

			if(m_currentCheatCode.equalsIgnoreCase(CHEAT_CODES[i])) {
				handleCheatCode(i);
				break;
			}
		}

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

}
