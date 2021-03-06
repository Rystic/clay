package org.rystic.main;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;

public class ClayConstants
{
	// --Interface Constants--
	public static final int TILE_X = 32;
	public static final int TILE_Y = 32;

	public static final int DEFAULT_MAP_WIDTH = 40 * ClayConstants.TILE_X;
	public static final int DEFAULT_MAP_HEIGHT = 25 * ClayConstants.TILE_Y;

	public static final int DEFAULT_INTERFACE_WIDTH = 300;
	
	public static final String DEFAULT_BUILDING_MENU_CATEGORY = "Architecture";

	// Default Tile

	public static final String DEFAULT_TILE_TYPE = "clay-block";
	public static final String FOUNDATION_BLOCK = "clay-block-foundation";


	// Golem types
	public static final String GOLEM_CLAY = "clay-golem";
	public static final String GOLEM_PEARLCLAY = "pearlclay-golem";
	public static final String GOLEM_STONEWARE = "stoneware-golem";
	public static final String GOLEM_EARTHENWARE = "earthenware-golem";
	public static final String GOLEM_WARRENS = "warrens-golem";

	// --Behavior Constants--
	// Behavior Commands
	public static final String BEHAVIOR_COMMAND_ADD_CLAY = "add-clay";
	public static final String BEHAVIOR_COMMAND_ADD_MANA = "add-mana";
	public static final String BEHAVIOR_COMMAND_BUILD = "build";
	public static final String BEHAVIOR_COMMAND_CLAIM_BUILDING = "claim-building";
	public static final String BEHAVIOR_COMMAND_CLAIM_CONSTRUCTION_ITEMS = "claim-construction-items";
	public static final String BEHAVIOR_COMMAND_CLAIM_GENERIC_BUILDING = "claim-generic-building";
	public static final String BEHAVIOR_COMMAND_CLAIM_HOUSE = "claim-house";
	public static final String BEHAVIOR_COMMAND_CLAIM_ITEMS = "claim-items";
	public static final String BEHAVIOR_COMMAND_CLAIM_PARAMETER_ITEMS = "claim-parameter-items";
	public static final String BEHAVIOR_COMMAND_CREATE_CLAY_GOLEM = "create-clay-golem";
	public static final String BEHAVIOR_COMMAND_CREATE_PEARLCLAY_GOLEM = "create-pearlclay-golem";
	public static final String BEHAVIOR_COMMAND_CREATE_STONEWARE_GOLEM = "create-stoneware-golem";
	public static final String BEHAVIOR_COMMAND_CREATE_EARTHENWARE_GOLEM = "create-earthenware-golem";
	public static final String BEHAVIOR_COMMAND_CREATE_WARRENS_GOLEM = "create-warrens-golem";
	public static final String BEHAVIOR_COMMAND_CONSUME_CLAIMED = "consume-claimed";
	public static final String BEHAVIOR_COMMAND_CONSUME_CLAIMED_CONSTRUCTION = "consume-claimed-construction";
	public static final String BEHAVIOR_COMMAND_DECONSTRUCT = "deconstruct";
	public static final String BEHAVIOR_COMMAND_ENTITY_NOT_HOLDING_ITEM = "entity-not-holding-item";
	public static final String BEHAVIOR_COMMAND_ENTITY_NOT_HOLDING_UNNECESSARY_ITEM = "entity-not-holding-unnecessary-item";
	public static final String BEHAVIOR_COMMAND_GENERATE_HEAT = "generate-heat";
	public static final String BEHAVIOR_COMMAND_HARVEST_ITEMS_ON_BUILDING = "harvest-items-on-building";
	public static final String BEHAVIOR_COMMAND_HARVEST_ITEMS_ON_CLAIMED_BUILDING = "harvest-items-on-claimed-building";
	public static final String BEHAVIOR_COMMAND_HIDE = "hide";
	public static final String BEHAVIOR_COMMAND_KILL_GOLEM = "kill-golem";
	public static final String BEHAVIOR_COMMAND_PRODUCE_ITEMS_ON_BUILDING = "produce-items-on-building";
	public static final String BEHAVIOR_COMMAND_PRODUCE_ITEM_ON_GOLEM = "produce-item-on-golem";
	public static final String BEHAVIOR_COMMAND_REPAIR_HEAT_DAMAGE = "repair-heat-damage";
	public static final String BEHAVIOR_COMMAND_SEEK = "seek";
	public static final String BEHAVIOR_COMMAND_SEEK_CLAIMED_BUILDING = "seek-claimed-building";
	public static final String BEHAVIOR_COMMAND_SEEK_CLAIMED_CONSTRUCTION_ITEMS = "seek-claimed-construction-items";
	public static final String BEHAVIOR_COMMAND_SEEK_CLAIMED_ITEMS = "seek-claimed-items";
	public static final String BEHAVIOR_COMMAND_SEEK_CONSTRUCTION_BUILDING = "seek-construction-building";
	public static final String BEHAVIOR_COMMAND_SEEK_ENTITIES = "seek-entities";
	public static final String BEHAVIOR_COMMAND_SEEK_GENERIC_BUILDING = "seek-generic-building";
	public static final String BEHAVIOR_COMMAND_SEEK_STORAGE = "seek-storage";
	public static final String BEHAVIOR_COMMAND_SET_CONSTRUCTION_BUILDING = "set-construction-building";
	public static final String BEHAVIOR_COMMAND_SHOW = "show";
	public static final String BEHAVIOR_COMMAND_STORAGE_EXISTS_FROM_ENTITY = "storage-exists-from-entity";
	public static final String BEHAVIOR_COMMAND_STORAGE_EXISTS_FROM_GOLEM = "storage-exists-from-golem";
	public static final String BEHAVIOR_COMMAND_STORE_ALL = "store-all";
	public static final String BEHAVIOR_COMMAND_STORE_ITEM = "store-item";
	public static final String BEHAVIOR_COMMAND_TAKE_ITEM = "take-item";
	public static final String BEHAVIOR_COMMAND_TICK = "tick";

	// Behavior Description
	public static final String BEHAVIOR_DESCRIPTION_TEXT = "text";
	public static final String BEHAVIOR_DESCRIPTION_ITEM = "item";
	public static final String BEHAVIOR_DESCRIPTION_BUILDING = "building";

	// Golem Brain Behavior
	public static final String PERSONAL_BEHAVIOR_BUILD_CLAY_GOLEM = "build-clay-golem";
	public static final String PERSONAL_BEHAVIOR_REPAIR = "repair";
	public static final String PERSONAL_BEHAVIOR_POWER_OBELISK = "power-obelisk";

	// Personalities
	public static final byte PERSONALITY_NONE = 0;
	public static final byte PERSONALITY_AMBITIOUS = 1;
	public static final byte PERSONALITY_INGENIOUS = 2;
	public static final byte PERSONALITY_CREATIVE = 3;

	public static final byte PSYCHOLOGY_NONE = -1;
	public static final byte PSYCHOLOGY_PARAGON = 0;
	public static final byte PSYCHOLOGY_ELITIST = 1;
	public static final byte PSYCHOLOGY_HARD_WORKING = 2;
	public static final byte PSYCHOLOGY_INFLUENTIAL = 3;

	public static final Map<Integer, Byte> PERSONALITY_GOLEMANITY_MAP = new HashMap<Integer, Byte>();

	// Behavior Weight Conditions
	public static final String WC_CALC_BUILDING = "calculate-extra-building-weight";
	public static final String WC_CAN_BUILD_CLAY_GOLEM = "can-build-clay-golem";
	public static final String WC_CAN_BUILD_PEARLCLAY_GOLEM = "can-build-pearlclay-golem";
	public static final String WC_CAN_BUILD_STONEWARE_GOLEM = "can-build-stoneware-golem";
	public static final String WC_CAN_BUILD_EARTHENWARE_GOLEM = "can-build-earthenware-golem";
	public static final String WC_CAN_BUILD_WARRENS_GOLEM = "can-build-warrens-golem";

	//TODO add new construction behavior info in building documentation
	public static final String WC_CLOSEST_TO_POINT = "closest-to-point";
	public static final String WC_CLOSEST_TO_CONSTRUCTION_POINT = "closest-to-construction-point";
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
	public static final int BEHAVIOR_FAILED_ASSIGNING_BUILDING_CLAIMED = 7;
	public static final int BEHAVIOR_FAILED_NO_UNOCCUPIED_GENERIC_BUILDING = 8;

	// Specific Behaviors
	public static final String BEHAVIOR_HARVEST = "harvest";
	public static final String BEHAVIOR_CONVERSION = "conversion";

	// --Building Constants--
	// Texture States
	public static final String T_STATE_DEFAULT = "state-default";
	public static final String T_STATE_NONE_ABOVE = "state-none-above";
	public static final String T_STATE_UNNATURAL_ABOVE = "state-unnatural-above";
	public static final String T_STATE_UNNATURAL_LEFT = "state-unnatural-left";
	public static final String T_STATE_UNNATURAL_RIGHT = "state-unnatural-right";
	public static final String T_STATE_UNNATURAL_BELOW = "state-unnatural-below";
	public static final String T_STATE_UNNATURAL_TOP_LEFT = "state-unnatural-top-left";
	public static final String T_STATE_UNNATURAL_TOP_RIGHT = "state-unnatural-top-right";
	public static final String T_STATE_ADJACENT = "state-adjacent";
	public static final String T_STATE_TICK_FINISHED = "state-tick-finished";
	public static final String T_STATE_STORAGE_FULL = "state-storage-full";
	public static final String T_STATE_BURIED = "state-buried";
	public static final String T_STATE_BLOCKED_LEFT = "state-blocked-left";
	public static final String T_STATE_BLOCKED_LEFT_SUPPORT = "state-blocked-left-support";
	public static final String T_STATE_BUILDING_ABOVE = "state-building-above";
	public static final String T_STATE_BUILDING_LEFT = "state-building-left";
	public static final String T_STATE_BUILDING_RIGHT = "state-building-right";
	public static final String T_STATE_BUILDING_ABOVE_NOT_STATE = "state-building-above-not-state";
	public static final String T_STATE_BUILDING_LEFT_NOT_STATE = "state-building-left-not-state";
	public static final String T_STATE_BUILDING_RIGHT_NOT_STATE = "state-building-right-not-state";

	// Transform States
	public static final String TRANSFORM_FLANKED = "flanked";
	public static final String TRANSFORM_FLANKED_SUPPORT = "flanked-support";
	public static final String TRANSFORM_FLANKED_NON_SUPPORT = "flanked-non-support";
	public static final String TRANSFORM_CONSTRUCTION_COMPELTED = "construction-completed";

	// Tick Results
	public static final String TICK_CODE_PRODUCE_ITEM = "item";
	public static final String TICK_CODE_QUEUE_HARVEST_BEHAVIOR = "queue-harvest-behavior";

	// Tick Reset Conditions
	public static final String TICK_RESET_NO_HELD_ITEMS = "no-held-items";

	// Building Position
	public static final String DEFAULT_BUILDING_POSITION = "base";
	public static final String EMPTY_TILE = "none";

	// --Event Constants--
	public static final Integer EVENT_MAP_UPDATE = 0;
	public static final Integer EVENT_ITEM_UPDATE = 1;
	public static final Integer EVENT_STORAGE_AVAILABLE_UPDATE = 2;
	public static final Integer EVENT_BUILDING_UNCLAIMED = 3;

	// --Search Conditions--
	public static final int SEARCH_ENTITY = 0;
	public static final int SEARCH_GENERIC_BUILDING = 1;
	public static final int SEARCH_STORAGE = 2;
	public static final int SEARCH_GENERIC_BUILDING_GOAL_ONLY = 3;
	public static final int SEARCH_CLAIMED_ITEM = 4;
	public static final int SEARCH_ITEM_GOAL_ONLY = 5;
	public static final int SEARCH_HOUSE_GOAL_ONLY = 6;
	public static final int SEARCH_EMPTY_GENERIC_BUILDING_GOAL_ONLY = 7;
	public static final int SEARCH_ENTITIES_GOAL_ONLY = 8;

	// --Text Colors--
	public static final Color M_MENU_HEADER_COLOR = Color.cyan;
	public static final Color M_AREA_HEADER_COLOR = new Color(0, 200, 220);
	public static final Color M_INTERFACE_BORDER_COLOR = new Color(0, 150, 150);
	public static final Color M_HIGHLIGHTED_COLOR = Color.cyan;
	public static final Color M_UNHIGHLIGHTED_COLOR = new Color(0, 75, 75);
	public static final Color M_MORE_INFORMATION_COLOR = Color.green;
	public static final Color M_CLAY_RELATED = new Color(225, 175, 0);

	// --Misc.--
	public static final String ITEM_FAMILY_MANA = "Mana";
	public static final String ITEM_FAMILY_CLAY = "Clay";
	public static final int ADDED_WEIGHT_INCREASE = 10;
	public static final int ADDED_WEIGHT_CAP = 250;
	public static final int NO_LIMIT = -1;
	public static final int ITEM_RATIO_INCREMENT = 10;
}
