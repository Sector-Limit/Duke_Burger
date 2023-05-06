package com.sectorlimit.dukeburger;

public interface DukeListener {

	public void onKilled();
	public void onDead();
	public void onLevelCompleted();
	public void onLevelEnded();
	public void onGameOver();
	public void onLevelWarpRequested(int levelNumber);
	public void onTestLevelWarpRequested(int levelNumber);
	public void onDebugCameraEnableRequested();

}
