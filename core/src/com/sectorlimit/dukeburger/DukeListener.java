package com.sectorlimit.dukeburger;

public interface DukeListener {

	public abstract void onKilled();
	public abstract void onDead();
	public abstract void onLevelCompleted();
	public abstract void onLevelEnded();
	public abstract void onGameOver();
	public abstract void onLevelWarpRequested(int levelNumber);
	public abstract void onDebugCameraEnableRequested();

}
