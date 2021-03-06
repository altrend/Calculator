package sonar.calculator.mod.utils.helpers;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.calculator.mod.common.tileentity.misc.TileEntityTeleporter;
import sonar.calculator.mod.utils.CalculatorTeleporter;
import sonar.core.api.utils.BlockCoords;
import sonar.core.common.block.SonarBlock;
import sonar.core.helpers.SonarHelper;

public class TeleporterHelper {

	public static void travelToDimension(List<EntityPlayer> players, TileEntityTeleporter tile) {
		for (EntityPlayer entity : players) {
			int currentDimension = entity.worldObj.provider.getDimension();
			BlockCoords coords = tile.getCoords();
			if (coords.getDimension() != currentDimension) {
				if (!tile.getWorld().isRemote && !entity.isDead) {

					EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entity;
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					WorldServer worldServer = server.worldServerForDimension(coords.getDimension());
					server.getPlayerList().transferPlayerToDimension(entityPlayerMP, tile.getCoords().getDimension(), new CalculatorTeleporter(worldServer, coords.getX() + 0.5, coords.getY() - 2, coords.getZ() + 0.5));
					if (currentDimension == 1) {
						((EntityPlayerMP) entity).connection.setPlayerLocation(coords.getX() + 0.5, coords.getY() - 2, coords.getZ() + 0.5, SonarHelper.getAngleFromMeta(worldServer.getBlockState(coords.getBlockPos()).getValue(SonarBlock.FACING).getIndex()), 0);
						worldServer.spawnEntityInWorld(entity);
						worldServer.updateEntityWithOptionalForce(entity, false);
					} else {
						((EntityPlayerMP) entity).connection.setPlayerLocation(coords.getX() + 0.5, coords.getY() - 2, coords.getZ() + 0.5, SonarHelper.getAngleFromMeta(worldServer.getBlockState(coords.getBlockPos()).getValue(SonarBlock.FACING).getIndex()), 0);
					}

				}
			} else {
				((EntityPlayerMP) entity).connection.setPlayerLocation(coords.getX() + 0.5, coords.getY() - 2, coords.getZ() + 0.5, SonarHelper.getAngleFromMeta(entity.worldObj.getBlockState(coords.getBlockPos()).getValue(SonarBlock.FACING).getIndex()), 0);
			}
			tile.coolDown = true;
			tile.coolDownTicks = 100;
		}
	}

	public static boolean canTeleport(TileEntityTeleporter target, TileEntityTeleporter current) {
		if (!target.getCoords().equals(current.getCoords()) && target.canTeleportPlayer()) {
			if ((target.password.getObject() == null || target.password.getObject().equals("") || current.linkPassword.getObject().equals(target.password.getObject()))) {
				return true;
			}
		}
		return false;
	}
}
