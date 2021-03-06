package sonar.calculator.mod.common.block.misc;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.misc.TileEntityCalculatorScreen;
import sonar.core.common.block.SonarMaterials;

/** basically a fabrication of the BlockSign code */
public class CalculatorScreen extends BlockContainer {

	public CalculatorScreen() {
		super(SonarMaterials.machine);
		float f = 0.25F;
		float f1 = 1.0F;
		//this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}
	/*
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		int l = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
		float f = 0.28125F;
		float f1 = 0.78125F;
		float f2 = 0.0F;
		float f3 = 1.0F;
		float f4 = 0.125F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		if (l == 2) {
			this.setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
		}

		if (l == 3) {
			this.setBlockBounds(f2, f, 0.0F, f3, f1, f4);
		}

		if (l == 4) {
			this.setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
		}

		if (l == 5) {
			this.setBlockBounds(0.0F, f, f2, f4, f1, f3);
		}

	}

	public void onNeighborBlockChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		boolean flag = false;

		int l = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
		flag = true;

		if (l == 2 && world.getBlockState(pos.add(0, 0, 1)).getMaterial().isSolid()) {
			flag = false;
		}

		if (l == 3 && world.getBlockState(pos.add(0, 0, -1)).getMaterial().isSolid()) {
			flag = false;
		}

		if (l == 4 && world.getBlockState(pos.add(1, 0, 0)).getMaterial().isSolid()) {
			flag = false;
		}

		if (l == 5 && world.getBlockState(pos.add(-1, 0, 0)).getMaterial().isSolid()) {
			flag = false;
		}

		if (flag) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.onNeighborBlockChange(world, pos, state, block);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		this.setBlockBoundsBasedOnState(world, pos);
		return super.getSelectedBoundingBox(world, pos);
	}
	*/
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityCalculatorScreen();
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Calculator.calculator_screen;
	}

	public Item getItemDropped(int meta, Random rand, int par) {
		return Calculator.calculator_screen;
	}

	public int getRenderType() {
		return -1;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

}
