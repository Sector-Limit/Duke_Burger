package com.sectorlimit.dukeburger;

public class CollisionCategories {

	public static final short GROUND           = 1;
	public static final short OBJECT           = 1 << 1;
	public static final short DUKE             = 1 << 2;
	public static final short DUKE_FEET_SENSOR = 1 << 3;
	public static final short DUKE_SIDE_SENSOR = 1 << 4;
	public static final short ENEMY            = 1 << 5;
	public static final short ENEMY_SENSOR     = 1 << 6;
	public static final short ENEMY_BOUNDARY   = 1 << 7;
	public static final short BURGER           = 1 << 8;
	public static final short DOOR             = 1 << 9;
	public static final short PROJECTILE       = 1 << 10;
	public static final short DEATH            = 1 << 11;

}
