package main;

public class ClayConstants
{
	//--Interface Constants--
	public static final int TILE_X = 35;
	public static final int TILE_Y = 35;

	public static final int DEFAULT_MAP_WIDTH = 40 * ClayConstants.TILE_X;
	public static final int DEFAULT_MAP_HEIGHT = 25 * ClayConstants.TILE_Y;
	
	public static final int DEFAULT_INTERFACE_WIDTH = 300;
	
	
	
	// Golem types
	public static final String GOLEM_CLAY = "clay-golem";
	
	
	
	//--Behavior Constants--
	// Behavior Commands
	public static final String BEHAVIOR_COMMAND_ADD_CLAY = "add-clay";
	public static final String BEHAVIOR_COMMAND_ADD_MANA = "add-mana";
	public static final String BEHAVIOR_COMMAND_BUILD = "build";
	public static final String BEHAVIOR_COMMAND_CLAIM_BUILDING = "claim-building";
	public static final String BEHAVIOR_COMMAND_CLAIM_CONSTRUCTION_ITEMS = "claim-construction-items";
	public static final String BEHAVIOR_COMMAND_CLAIM_GENERIC_BUILDING = "claim-generic-building";
	public static final String BEHAVIOR_COMMAND_CLAIM_HOUSE = "claim-house";
	public static final String BEHAVIOR_COMMAND_CLAIM_ITEMS = "claim-items";
	public static final String BEHAVIOR_COMMAND_CREATE_GOLEM = "create-golem";
	public static final String BEHAVIOR_COMMAND_CONSUME_CLAIMED = "consume-claimed";
	public static final String BEHAVIOR_COMMAND_CONSUME_CLAIMED_CONSTRUCTION = "consume-claimed-construction";
	public static final String BEHAVIOR_COMMAND_ENTITY_NOT_HOLDING_ITEM = "entity-not-holding-item";
	public static final String BEHAVIOR_COMMAND_ENTITY_NOT_HOLDING_UNNECESSARY_ITEM = "entity-not-holding-unnecessary-item";
	public static final String BEHAVIOR_COMMAND_HIDE = "hide";
	public static final String BEHAVIOR_COMMAND_PRODUCE_ITEM_ON_GOLEM = "produce-item-on-golem";
	public static final String BEHAVIOR_COMMAND_SEEK = "seek";
	public static final String BEHAVIOR_COMMAND_SEEK_CLAIMED_CONSTRUCTION_ITEMS = "seek-claimed-construction-items";
	public static final String BEHAVIOR_COMMAND_SEEK_CLAIMED_ITEMS = "seek-claimed-items";
	public static final String BEHAVIOR_COMMAND_SEEK_GENERIC_BUILDING = "seek-generic-building";
	public static final String BEHAVIOR_COMMAND_SEEK_STORAGE = "seek-storage";
	public static final String BEHAVIOR_COMMAND_SHOW = "show";
	public static final String BEHAVIOR_COMMAND_STORAGE_EXISTS_FROM_ENTITY = "storage-exists-from-entity";
	public static final String BEHAVIOR_COMMAND_STORAGE_EXISTS_FROM_GOLEM = "storage-exists-from-golem";
	public static final String BEHAVIOR_COMMAND_STORE_ALL = "store-all";
	public static final String BEHAVIOR_COMMAND_STORE_ITEM = "store-item";
	public static final String BEHAVIOR_COMMAND_TAKE_ITEM = "take-item";
	public static final String BEHAVIOR_COMMAND_TICK= "tick";

	// Golem Brain Behavior
	public static final String PERSONAL_BEHAVIOR_BUILD_CLAY_GOLEM = "build-clay-golem";
	public static final String PERSONAL_BEHAVIOR_STORE_ITEMS = "store-items";
	public static final String PERSONAL_BEHAVIOR_REPAIR = "repair";
	public static final String PERSONAL_BEHAVIOR_POWER_OBELISK = "power-obelisk";

	// Behavior Weight Conditions
	public static final String WC_CALC_BUILDING = "calculate-extra-building-weight";
	public static final String WC_CAN_BUILD_GOLEM = "can-build-golem";
	public static final String WC_CLOSEST_TO_PINT = "closest-to-point";
	public static final String WC_HOLDING_ITEM = "holding-item";
	public static final String WC_LOW_CLAY = "low-clay";
	public static final String WC_LOW_MANA = "low-mana";
	public static final String WC_NO_STORAGE = "no-storage";

	// Behavior passed/failed
	public static final int BEHAVIOR_PASSED = -1;
	public static final int BEHAVIOR_FAILED_NO_PATH = 0;
	public static final int BEHAVIOR_FAILED_NO_MATERIALS = 1;
	public static final int BEHAVIOR_FAILED_MISSING_ITEM = 2;
	public static final int BEHAVIOR_FAILED_NO_STORAGE = 3;
	public static final int BEHAVIOR_FAILED_OBSOLETE = 4;
	public static final int BEHAVIOR_FAILED_INVALID_GOLEM = 5;
	public static final int BEHAVIOR_FAILED_LIMIT_REACHED = 6;
	public static final int BEHAVIOR_FAILED_BUILDING_OCCUPIED = 7;
	
	// Specific Behaviors
	public static final String BEHAVIOR_HARVEST = "harvest";

	
	
	//--Building Constants--
	// Texture States
	public static final String T_STATE_DEFAULT = "state-default";
	public static final String T_STATE_NONE_ABOVE = "state-none-above";
	public static final String T_STATE_UNNATURAL_ABOVE = "state-unnatural-above";
	public static final String T_STATE_UNNATURAL_LEFT = "state-unnatural-left";
	public static final String T_STATE_UNNATURAL_RIGHT = "state-unnatural-right";
	public static final String T_STATE_UNNATURAL_BELOW = "state-unnatural-below";
	public static final String T_STATE_ADJACENT = "state-adjacent";
	public static final String T_STATE_TICK_FINISHED = "state-tick-finished";
	public static final String T_STATE_STORAGE_FULL = "state-storage-full";
	public static final String T_STATE_BURIED = "state-buried";
	public static final String T_STATE_BLOCKED_LEFT = "state-blocked-left";
	public static final String T_STATE_BUILDING_ABOVE = "state-building-above";
	public static final String T_STATE_BUILDING_LEFT = "state-building-left";
	public static final String T_STATE_BUILDING_RIGHT = "state-building-right";
	public static final String T_STATE_BUILDING_ABOVE_NOT_STATE = "state-building-above-not-state";
	public static final String T_STATE_BUILDING_LEFT_NOT_STATE = "state-building-left-not-state";
	public static final String T_STATE_BUILDING_RIGHT_NOT_STATE = "state-building-right-not-state";
	
	// Transform States
	public static final String TRANSFORM_FLANKED = "flanked";
	
	// Tick Results
	public static final String TICK_CODE_PRODUCE_ITEM = "item";
	public static final String TICK_CODE_QUEUE_HARVEST_BEHAVIOR = "queue-harvest-behavior";
	
	// Tick Reset Conditions
	public static final String TICK_RESET_NO_HELD_ITEMS = "no-held-items";
	
	// Building Position
	public static final String DEFAULT_BUILDING_POSITION = "base";
	public static final String EMPTY_TILE = "none";
	
	
	
	//--Event Constants--
	public static final Integer EVENT_MAP_UPDATE = 0;
	public static final Integer EVENT_ITEM_UPDATE = 1;
	public static final Integer EVENT_STORAGE_AVAILABLE_UPDATE = 2;
	
	
	
	//--Search Conditions--
	public static final int SEARCH_ENTITY = 0;
	public static final int SEARCH_GENERIC_BUILDING = 1;
	public static final int SEARCH_STORAGE = 2;
	public static final int SEARCH_GENERIC_BUILDING_GOAL_ONLY = 3;
	public static final int SEARCH_CLAIMED_ITEM = 4;
	public static final int SEARCH_ITEM_GOAL_ONLY = 5;
	public static final int SEARCH_HOUSE_GOAL_ONLY = 6;
	
	
	
	//--Misc.--
	public static final int ADDED_WEIGHT_INCREASE = 5;
	public static final int ADDED_WEIGHT_CAP = 250;
	public static final int NO_LIMIT = -1;
}
