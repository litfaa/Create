package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.config.ui.ConfigAnnotations;
import com.simibubi.create.foundation.utility.ContraptionData;

public class CKinetics extends ConfigBase {

	public final ConfigBool disableStress = b(false, "disableStress", Comments.disableStress);
	public final ConfigInt maxBeltLength = i(20, 5, "maxBeltLength", Comments.maxBeltLength);
	public final ConfigInt crushingDamage = i(4, 0, "crushingDamage", Comments.crushingDamage);
	public final ConfigInt maxMotorSpeed = i(256, 64, "maxMotorSpeed", Comments.rpm, Comments.maxMotorSpeed, ConfigAnnotations.RequiresRestart.BOTH.asComment());
	public final ConfigInt waterWheelBaseSpeed = i(4, 1, "waterWheelBaseSpeed", Comments.rpm, Comments.waterWheelBaseSpeed);
	public final ConfigInt waterWheelFlowSpeed = i(4, 1, "waterWheelFlowSpeed", Comments.rpm, Comments.waterWheelFlowSpeed);
	public final ConfigInt maxRotationSpeed = i(256, 64, "maxRotationSpeed", Comments.rpm, Comments.maxRotationSpeed);
	public final ConfigEnum<DeployerAggroSetting> ignoreDeployerAttacks =
		e(DeployerAggroSetting.CREEPERS, "ignoreDeployerAttacks", Comments.ignoreDeployerAttacks);
	public final ConfigInt kineticValidationFrequency =
		i(60, 5, "kineticValidationFrequency", Comments.kineticValidationFrequency);
	public final ConfigFloat crankHungerMultiplier = f(.01f, 0, 1, "crankHungerMultiplier", Comments.crankHungerMultiplier);
	public final ConfigInt minimumWindmillSails = i(8, 0, "minimumWindmillSails", Comments.minimumWindmillSails);
	public final ConfigInt windmillSailsPerRPM = i(8, 1, "windmillSailsPerRPM", Comments.windmillSailsPerRPM);
	public final ConfigInt maxEjectorDistance = i(32, 0, "maxEjectorDistance", Comments.maxEjectorDistance);
	public final ConfigInt ejectorScanInterval = i(120, 10, "ejectorScanInterval", Comments.ejectorScanInterval);

	public final ConfigGroup fan = group(1, "encasedFan", "Encased Fan");
	public final ConfigInt fanPushDistance = i(20, 5, "fanPushDistance", Comments.fanPushDistance);
	public final ConfigInt fanPullDistance = i(20, 5, "fanPullDistance", Comments.fanPullDistance);
	public final ConfigInt fanBlockCheckRate = i(30, 10, "fanBlockCheckRate", Comments.fanBlockCheckRate);
	public final ConfigInt fanRotationArgmax = i(256, 64, "fanRotationArgmax", Comments.rpm, Comments.fanRotationArgmax);
	public final ConfigInt inWorldProcessingTime = i(150, 0, "inWorldProcessingTime", Comments.inWorldProcessingTime);

	public final ConfigGroup contraptions = group(1, "contraptions", "Moving Contraptions");
	public final ConfigInt maxBlocksMoved = i(2048, 1, "maxBlocksMoved", Comments.maxBlocksMoved);
	public final ConfigInt maxDataSize =
		i(ContraptionData.DEFAULT_MAX, 0, "maxDataSize", Comments.bytes, Comments.maxDataDisable, Comments.maxDataSize, Comments.maxDataSize2);
	public final ConfigInt maxChassisRange = i(16, 1, "maxChassisRange", Comments.maxChassisRange);
	public final ConfigInt maxPistonPoles = i(64, 1, "maxPistonPoles", Comments.maxPistonPoles);
	public final ConfigInt maxRopeLength = i(256, 1, "maxRopeLength", Comments.maxRopeLength);
	public final ConfigInt maxGluePlacementRange = i(24, 1, "maxGluePlacementRange", Comments.maxGluePlacementRange);
	public final ConfigBool glueMustBeConnected = b(true, "glueMustBeConnected", Comments.glueMustBeConnected);
	public final ConfigInt maxCartCouplingLength = i(32, 1, "maxCartCouplingLength", Comments.maxCartCouplingLength);
	public final ConfigBool survivalContraptionPickup = b(true, "survivalContraptionPickup", Comments.survivalContraptionPickup);
	public final ConfigEnum<ContraptionMovementSetting> spawnerMovement =
		e(ContraptionMovementSetting.NO_PICKUP, "movableSpawners", Comments.spawnerMovement);
	public final ConfigEnum<ContraptionMovementSetting> amethystMovement =
		e(ContraptionMovementSetting.NO_PICKUP, "amethystMovement", Comments.amethystMovement);
	public final ConfigEnum<ContraptionMovementSetting> obsidianMovement =
		e(ContraptionMovementSetting.UNMOVABLE, "movableObsidian", Comments.obsidianMovement);
	public final ConfigEnum<ContraptionMovementSetting> reinforcedDeepslateMovement =
		e(ContraptionMovementSetting.UNMOVABLE, "movableReinforcedDeepslate", Comments.reinforcedDeepslateMovement);
	public final ConfigBool moveItemsToStorage = b(true, "moveItemsToStorage", Comments.moveItemsToStorage);
	public final ConfigBool harvestPartiallyGrown = b(false, "harvestPartiallyGrown", Comments.harvestPartiallyGrown);
	public final ConfigBool harvesterReplants = b(true, "harvesterReplants", Comments.harvesterReplants);

	public final CStress stressValues = nested(1, CStress::new, Comments.stress);

	public final ConfigGroup state = group(1, "stats", Comments.stats);
	public final ConfigFloat mediumSpeed = f(30, 0, 4096, "mediumSpeed", Comments.rpm, Comments.mediumSpeed);
	public final ConfigFloat fastSpeed = f(100, 0, 65535, "fastSpeed", Comments.rpm, Comments.fastSpeed);
	public final ConfigFloat mediumStressImpact =
		f(4, 0, 4096, "mediumStressImpact", Comments.su, Comments.mediumStressImpact);
	public final ConfigFloat highStressImpact = f(8, 0, 65535, "highStressImpact", Comments.su, Comments.highStressImpact);
	public final ConfigFloat mediumCapacity = f(128, 0, 4096, "mediumCapacity", Comments.su, Comments.mediumCapacity);
	public final ConfigFloat highCapacity = f(1024, 0, 65535, "highCapacity", Comments.su, Comments.highCapacity);

	@Override
	public String getName() {
		return "kinetics";
	}

	private static class Comments {
		static String maxBeltLength = "Maximum length in blocks of mechanical belts.机械传送带的最大长度，以方块为单位";
		static String crushingDamage = "Damage dealt by active Crushing Wheels.活动的碾压车轮造成的伤害";
		static String maxMotorSpeed = "Maximum allowed speed of a configurable motor.可配置电机的最大允许速度";
		static String maxRotationSpeed = "Maximum allowed rotation speed for any Kinetic Tile.任何动力块的最大允许旋转速度";
		static String fanPushDistance = "Maximum distance in blocks Fans can push entities.风扇能够推动实体的最大距离，以方块为单位";
		static String fanPullDistance = "Maximum distance in blocks from where Fans can pull entities.风扇能够吸引实体的最大距离，以方块为单位";
		static String fanBlockCheckRate = "Game ticks between Fans checking for anything blocking their air flow.风扇检查阻塞其空气流动的时间间隔，以游戏刻为单位";
		static String fanRotationArgmax = "Rotation speed at which the maximum stats of fans are reached.风扇达到最大性能的旋转速度";
		static String inWorldProcessingTime = "Game ticks required for a Fan-based processing recipe to take effect.以游戏刻计算，需要风扇进行加工处理的配方生效的时间";
		static String crankHungerMultiplier = "multiplier used for calculating exhaustion from speed when a crank is turned.当手摇曲柄被转动时，用于计算疲劳度的乘数";
		static String maxBlocksMoved = "Maximum amount of blocks in a structure movable by Pistons, Bearings or other means.由活塞、轴承或其他手段移动的结构中最大可移动方块数量";
		static String maxDataSize = "Maximum amount of data a contraption can have before it can't be synced with players.与玩家同步之前，一个装置可包含的最大数据量";
		static String maxDataSize2 = "Un-synced contraptions will not be visible and will not have collision.未与玩家同步的装置将不可见，也不会有碰撞";
		static String maxDataDisable = "[0 to disable this limit][0表示禁用此限制]";
		static String maxChassisRange = "Maximum value of a chassis attachment range.底盘附加范围的最大值";
		static String maxPistonPoles = "Maximum amount of extension poles behind a Mechanical Piston.机械活塞后方可扩展的杆数的最大值";
		static String maxRopeLength = "Max length of rope available off a Rope Pulley.绳索滑轮提供的绳索最大长度";
		static String maxCartCouplingLength = "Maximum allowed distance of two coupled minecarts.连接在一起的两个矿车允许的最大距离";
		static String moveItemsToStorage =
			"Whether items mined or harvested by contraptions should be placed in their mounted storage.是否应该将被机器设备开采或收获的物品放置在其安装的存储设备中";
		static String maxGluePlacementRange = "Maximum distance in blocks from where Glue can be placed.可以放置胶水的距离上限（以方块为单位）";
		static String glueMustBeConnected = "Whether Glue can only be placed on blocks that are connected to each other.胶水是否只能放置在相互连接的方块上";
		static String harvestPartiallyGrown = "Whether harvesters should break crops that aren't fully grown.收割机是否应该收割未完全生长的作物";
		static String harvesterReplants = "Whether harvesters should replant crops after harvesting.是否应该在收获后重新种植庄稼";
		static String stats = "Configure speed/capacity levels for requirements and indicators.将要求和指标的速度/容量级别进行配置";
		static String rpm = "[in Revolutions per Minute]";
		static String su = "[in Stress Units]";
		static String bytes = "[in Bytes]";
		static String mediumSpeed = "Minimum speed of rotation to be considered 'medium'被视为“中等”旋转速度的最低值";
		static String fastSpeed = "Minimum speed of rotation to be considered 'fast'被视为“快速”旋转速度的最低值";
		static String mediumStressImpact = "Minimum stress impact to be considered 'medium'被视为“中等”应力影响的最低值";
		static String highStressImpact = "Minimum stress impact to be considered 'high'被视为“高级”应力影响的最低值";
		static String mediumCapacity = "Minimum added Capacity by sources to be considered 'medium'被视为“中等”容量的最低值";
		static String highCapacity = "Minimum added Capacity by sources to be considered 'high'被视为“高级”容量的最低值";
		static String stress = "Fine tune the kinetic stats of individual components微调单个组件的动力统计数据";
		static String ignoreDeployerAttacks = "Select what mobs should ignore Deployers when attacked by them.选择何时忽略部署者攻击的生物";
		static String waterWheelBaseSpeed = "Added rotation speed by a water wheel when at least one flow is present.至少存在一个水流时，水车添加的旋转速度";
		static String waterWheelFlowSpeed = "Rotation speed gained by a water wheel for each side with running fluids. (halved if not against blades)每个具有运行流体的侧面增加的水车旋转速度(如果不针对叶片，则减半)";
		static String disableStress = "Disable the Stress mechanic altogether.完全禁用应力机制";
		static String kineticValidationFrequency = "Game ticks between Kinetic Blocks checking whether their source is still valid.动力方块检查其源是否仍然有效之间的游戏刻";
		static String minimumWindmillSails = "Amount of sail-type blocks required for a windmill to assemble successfully.成功组装风车所需的帆类型方块数量";
		static String windmillSailsPerRPM = "Number of sail-type blocks required to increase windmill speed by 1RPM.将风车速度提高1RPM所需的帆类型方块数";
		static String maxEjectorDistance = "Max Distance in blocks a Weighted Ejector can throw加权弹射器可以抛出的最大距离以方块为单位";
		static String ejectorScanInterval = "Time in ticks until the next item launched by an ejector scans blocks for potential collisions弹射器发射的下一个物品扫描潜在碰撞的刻数";
		static String survivalContraptionPickup = "Whether minecart contraptions can be picked up in survival mode.是否可以在生存模式下拾取矿车装置";
		static String spawnerMovement = "Configure how Spawner blocks can be moved by contraptions.配置Spawner块如何被装置移动";
		static String amethystMovement = "Configure how Budding Amethyst can be moved by contraptions.配置如何通过装置移动新生紫水晶"; 
		static String obsidianMovement = "Configure how Obsidian blocks can be moved by contraptions.配置如何通过装置移动黑曜石块"; 
		static String reinforcedDeepslateMovement = "Configure how Reinforced Deepslate blocks can be moved by contraptions.配置如何通过装置移动强化深板岩块"; 
	}

	public enum DeployerAggroSetting {
		ALL, CREEPERS, NONE
	}

}
