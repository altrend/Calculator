package sonar.calculator.mod.common.tileentity.machines;

import java.util.Map;
import java.util.Random;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.api.IResearchStore;
import sonar.calculator.mod.api.IStability;
import sonar.calculator.mod.api.ISyncTile;
import sonar.calculator.mod.api.SyncData;
import sonar.calculator.mod.api.SyncType;
import sonar.calculator.mod.common.recipes.crafting.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.crafting.CalculatorRecipes;
import sonar.calculator.mod.common.tileentity.misc.TileEntityCalculator;
import sonar.calculator.mod.common.tileentity.misc.TileEntityCalculator.Dynamic;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.common.tileentity.TileEntityInventoryReceiver;
import sonar.core.utils.IDropTile;

public class TileEntityResearchChamber extends TileEntityInventory implements ISyncTile, IDropTile {

	public int ticks;
	public int researchSpeed = 100;
	public int lastResearch;
	public int[] unblocked, lastUnblocked;
	public Random rand = new Random();
	public int maxRecipes, storedRecipes;

	public TileEntityResearchChamber() {
		super.slots = new ItemStack[2];
		this.unblocked = this.lastUnblocked = new int[CalculatorRecipes.recipes().getIDList().size() + 1];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		beginResearch();
		this.markDirty();
	}
	public void beginResearch() {
		if (slots[0] == null) {
			ticks = 0;
			this.lastResearch = 0;
		}

		if (slots[0] != null
				&& (CalculatorRecipes.recipes().getID(slots[0]) != 0 && unblocked[CalculatorRecipes.recipes().getID(slots[0])]==0 || slots[0].getItem() == Calculator.circuitBoard && slots[0].getItem() instanceof IStability
						&& ((IStability) slots[0].getItem()).getStability(slots[0]))) {
			if (ticks == 0) {
				ticks = 1;
			}

			if (ticks > 0) {
				if (ticks != researchSpeed) {
					ticks++;
				} else {
					if(!this.worldObj.isRemote){
					addResearch(slots[0]);
					syncResearch();
					}
					ticks = -1;
				}
			}

		}
	}

	public void syncResearch() {
		if (slots[1] != null && slots[1].getItem() instanceof IResearchStore) {
			int[] storedResearch = ((IResearchStore) slots[1].getItem()).getResearch(slots[1]);
			int[] syncResearch = new int[CalculatorRecipes.recipes().getIDList().size() + 1];
			for (int s = 0; s < storedResearch.length; s++) {
				if (storedResearch[s] == 1) {
					syncResearch[s] = 1;
				}
			}
			for (int u = 0; u < unblocked.length; u++) {
				if (unblocked[u] == 1) {
					syncResearch[u] = 1;
				}
			}
		
			((IResearchStore) slots[1].getItem()).setResearch(slots[1], syncResearch, storedRecipes, maxRecipes);
			this.unblocked = syncResearch;
		}

	}

	public void addResearch(ItemStack stack) {
		if (stack != null) {
			if (stack.getItem() != Calculator.circuitBoard) {
				lastUnblocked = unblocked;
				if (stack.getItem() == Item.getItemFromBlock(Blocks.log) || stack.getItem() == Item.getItemFromBlock(Blocks.log2) || stack.getItem() == Item.getItemFromBlock(Blocks.carpet)
						|| stack.getItem() == Item.getItemFromBlock(Blocks.wool) || stack.getItem() == Item.getItemFromBlock(Blocks.stained_hardened_clay)
						|| stack.getItem() == Item.getItemFromBlock(Blocks.sapling) || stack.getItem() == Item.getItemFromBlock(Blocks.planks)
						|| stack.getItem() == Item.getItemFromBlock(Blocks.leaves) || stack.getItem() == Item.getItemFromBlock(Blocks.leaves2)) {
					unlock(stack);
				} else {
					lastResearch = CalculatorRecipes.recipes().getID(stack);
					unblocked[lastResearch] = 1;
				}
			} else {
				lastUnblocked = unblocked;
				stack.stackTagCompound.setInteger("Stable", 0);
				lastResearch = rand.nextInt(CalculatorRecipes.recipes().getIDList().size() - 1);
				ItemStack target = CalculatorRecipes.recipes().getRegisteredStack(lastResearch);
				if (target.getItem() == Item.getItemFromBlock(Blocks.log) || target.getItem() == Item.getItemFromBlock(Blocks.log2) || target.getItem() == Item.getItemFromBlock(Blocks.carpet)
						|| target.getItem() == Item.getItemFromBlock(Blocks.wool) || target.getItem() == Item.getItemFromBlock(Blocks.stained_hardened_clay)
						|| target.getItem() == Item.getItemFromBlock(Blocks.sapling) || target.getItem() == Item.getItemFromBlock(Blocks.planks)
						|| target.getItem() == Item.getItemFromBlock(Blocks.leaves) || target.getItem() == Item.getItemFromBlock(Blocks.leaves2)) {
					unlock(target);
				} else {
					unblocked[lastResearch] = 1;
				}
			}
			Map<Integer, CalculatorRecipe> recipes = CalculatorRecipes.recipes().getStandardList();
			maxRecipes = 0;
			storedRecipes = 0;
			for (Map.Entry<Integer, CalculatorRecipe> recipe : recipes.entrySet()) {
				maxRecipes++;
				if (!recipe.getValue().hidden) {
					if (CalculatorConfig.isEnabled(((CalculatorRecipe) recipe.getValue()).output)) {
						storedRecipes++;
					}
				} else if (unblocked != null && unblocked.length >= 1) {
					if (recipe.getValue().hidden && unblocked[CalculatorRecipes.recipes().getID(recipe.getValue().input)] != 0
							&& unblocked[CalculatorRecipes.recipes().getID(recipe.getValue().input2)] != 0 || unblocked[CalculatorRecipes.recipes().getID(recipe.getValue().output)] != 0) {
						if (CalculatorConfig.isEnabled(((CalculatorRecipe) recipe.getValue()).output)) {
							storedRecipes++;
						}
					}
				}

			}
		}
		sendResearch();
	}

	public void unlock(ItemStack stack) {
		for (int i = 0; i < 16; i++) {
			ItemStack unlock = new ItemStack(stack.getItem(), 1, i);
			if (unlock != null) {
				lastResearch = CalculatorRecipes.recipes().getID(unlock);
				unblocked[lastResearch] = 1;
			}
		}
	}
	public void sendResearch(){
		for(int X=-3; X<=3; X++){
			for(int Y=-3; Y<=3; Y++){
				for(int Z=-3; Z<=3; Z++){
					TileEntity target = this.worldObj.getTileEntity(xCoord+X, yCoord+Y, zCoord+Z);
					if(target!=null && target instanceof TileEntityCalculator.Dynamic){
						TileEntityCalculator.Dynamic dynamic = (Dynamic) target;
						dynamic.setUnblocked(unblocked);
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.unblocked = nbt.getIntArray("Unblocked");
		if (this.unblocked == null) {
			this.unblocked = new int[CalculatorRecipes.recipes().getIDList().size() + 1];
		}
		this.lastUnblocked = nbt.getIntArray("LastUnblocked");
		if (this.lastUnblocked == null) {
			this.lastUnblocked = new int[CalculatorRecipes.recipes().getIDList().size() + 1];
		}
		this.lastResearch = nbt.getInteger("Research");
		this.maxRecipes = nbt.getInteger("Max");
		this.storedRecipes = nbt.getInteger("Stored");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setIntArray("Unblocked", unblocked);
		nbt.setIntArray("LastUnblocked", lastUnblocked);
		nbt.setInteger("Research", lastResearch);
		nbt.setInteger("Max", maxRecipes);
		nbt.setInteger("Stored", storedRecipes);
	}

	public boolean receiveClientEvent(int action, int param) {
		if (action == 1) {
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return true;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		super.setInventorySlotContents(i, itemstack);
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.worldObj.addBlockEvent(xCoord, yCoord, zCoord, blockType, 1, 0);
	}

	public boolean isBlocked(ItemStack stack) {
		if (stack == null) {
			return false;
		}
		int stackID = CalculatorRecipes.recipes().getID(stack);
		if (stackID == 0) {
			return true;
		}
		return unblocked[stackID] == 0;

	}

	public boolean isBlocked(int stack) {
		if (stack == 0) {
			return true;
		}
		return unblocked[stack] == 0;

	}

	public void unblockItem(int stack) {
		unblocked[stack] = 0;
	}

	public void blockItem(int stack) {
		unblocked[stack] = 1;
	}

	public int[] unblockedList() {
		return unblocked;
	}
	@Override
	public void onSync(int data, int id) {
		switch (id) {
		case SyncType.SPECIAL1:
			this.maxRecipes = data;
			break;
		case SyncType.SPECIAL2:
			this.storedRecipes = data;
			break;
		}
	}

	@Override
	public SyncData getSyncData(int id) {
		switch (id) {
		case SyncType.SPECIAL1:
			return new SyncData(true, maxRecipes);
		case SyncType.SPECIAL2:
			return new SyncData(true, storedRecipes);
		}
		return new SyncData(false, 0);
	}

	@Override
	public void readInfo(NBTTagCompound tag) {
		this.unblocked = tag.getIntArray("Unblocked");
		if (this.unblocked == null) {
			this.unblocked = new int[CalculatorRecipes.recipes().getIDList().size() + 1];
		}
		this.lastUnblocked = tag.getIntArray("LastUnblocked");
		if (this.lastUnblocked == null) {
			this.lastUnblocked = new int[CalculatorRecipes.recipes().getIDList().size() + 1];
		}
		this.lastResearch = tag.getInteger("Research");
		this.maxRecipes = tag.getInteger("Max");
		this.storedRecipes = tag.getInteger("Stored");
		
	}

	@Override
	public void writeInfo(NBTTagCompound tag) {
		tag.setIntArray("Unblocked", unblocked);
		tag.setIntArray("LastUnblocked", lastUnblocked);
		tag.setInteger("Research", lastResearch);
		tag.setInteger("Max", maxRecipes);
		tag.setInteger("Stored", storedRecipes);
		
	}
}