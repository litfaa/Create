package com.simibubi.create.foundation.config;

public class CTrains extends ConfigBase {

	public final ConfigBool trainsCauseDamage = b(true, "trainsCauseDamage", Comments.trainsCauseDamage);
	public final ConfigInt maxTrackPlacementLength = i(32, 16, "maxTrackPlacementLength", Comments.maxTrackPlacementLength);
	public final ConfigInt maxAssemblyLength = i(128, 5, "maxAssemblyLength", Comments.maxAssemblyLength);
	public final ConfigInt maxBogeyCount = i(20, 1, "maxBogeyCount", Comments.maxBogeyCount);
	public final ConfigFloat manualTrainSpeedModifier = f(.75f, 0, "manualTrainSpeedModifier", Comments.manualTrainSpeedModifier);
	
	// public final ConfigInt trainStressAmount = i(4, 0, 128, "trainStressAmount", Comments.trainStressAmount);

	public final ConfigGroup trainStats = group(1, "trainStats", "Standard Trains");
	public final ConfigFloat trainTopSpeed = f(28, 0, "trainTopSpeed", Comments.mps, Comments.trainTopSpeed);
	public final ConfigFloat trainTurningTopSpeed = f(14, 0, "trainTurningTopSpeed", Comments.mps, Comments.trainTurningTopSpeed);
	public final ConfigFloat trainAcceleration = f(3, 0, "trainAcceleration", Comments.acc, Comments.trainAcceleration);
	
	public final ConfigGroup poweredTrainStats = group(1, "poweredTrainStats", "Powered Trains");
	public final ConfigFloat poweredTrainTopSpeed = f(40, 0, "poweredTrainTopSpeed", Comments.mps, Comments.poweredTrainTopSpeed);
	public final ConfigFloat poweredTrainTurningTopSpeed = f(20, 0, "poweredTrainTurningTopSpeed", Comments.mps, Comments.poweredTrainTurningTopSpeed);
	public final ConfigFloat poweredTrainAcceleration = f(3, 0, "poweredTrainAcceleration", Comments.acc, Comments.poweredTrainAcceleration);

	public final ConfigGroup notrainlimits = group(1, "notrainlimits", "No Train Limits");
	public final ConfigBool disableTrackPlacementLimits = b(false, "disableTrackPlacementLimits", Comments.disableTrackPlacementLimits);
	public final ConfigInt trainStressAmount = i(4, 0, 128, "trainStressAmount", Comments.trainStressAmount);
	public final ConfigInt wrenchMoveDistance = i(16, 1, 128, "wrenchMoveDistance", Comments.wrenchMoveDistance);
	

	@Override
	public String getName() {
		return "trains";
	}

	private static class Comments {
	static String mps = "[in Blocks/Second] [方块/秒]";
	static String acc = "[in Blocks/Second²] [方块/秒²]";
	static String trainTopSpeed = "The top speed of any assembled Train. 组装火车的最高速度。";
	static String trainTurningTopSpeed = "The top speed of Trains during a turn. 火车在拐弯时的最高速度。";
	static String trainAcceleration = "The acceleration of any assembled Train. 组装火车的加速度。";
	static String poweredTrainTopSpeed = "The top speed of powered Trains. 电力火车的最高速度。";
	static String poweredTrainTurningTopSpeed = "The top speed of powered Trains during a turn. 电力火车在拐弯时的最高速度。";
	static String poweredTrainAcceleration = "The acceleration of powered Trains. 电力火车的加速度。";
	static String trainsCauseDamage = "Whether moving Trains can hurt colliding mobs and players. 移动中的火车是否会伤害相撞的生物和玩家。";
	static String maxTrackPlacementLength = "Maximum length of track that can be placed as one batch or turn. 可以一次性或批量放置的轨道的最大长度。";
	static String maxAssemblyLength = "Maximum length of a Train Stations' assembly track. 火车站组装轨道的最大长度。";
	static String maxBogeyCount = "Maximum amount of bogeys assembled as a single Train. 单个火车组装的轮架数量上限。";
	static String manualTrainSpeedModifier = "Relative speed of a manually controlled Train compared to a Scheduled one. 手动控制的火车相对于自动控制的火车的速度比例。";
	static String trainStressAmount = "Amount of stress Train couplings can handle. 火车耐受压力耦合装置的数量。";
	static String disableTrackPlacementLimits = "Disable the checks when placing tracks. 禁用放置轨道时的检查。";
	static String wrenchMoveDistance = "Maximum distance a wrench can move a train. 扳手移动火车的最大距离。";
	}

}
