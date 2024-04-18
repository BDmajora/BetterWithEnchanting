package googy.betterwithenchanting.inventory;

import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.Block;
import net.minecraft.core.crafting.ICrafting;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import turing.enchantmentlib.EnchantmentLib;
import turing.enchantmentlib.api.EnchantmentData;
import turing.enchantmentlib.api.IEnchant;

import java.util.List;
import java.util.Random;

public class ContainerEnchantmentTable extends Container
{
	public TileEntityEnchantmentTable enchantmentTable;
	public int[] enchantCost = new int[3];

	private final Random random = new Random();

	public ContainerEnchantmentTable(InventoryPlayer inventoryplayer, TileEntityEnchantmentTable enchantmentTable)
	{
		this.enchantmentTable = enchantmentTable;

		addSlot(new Slot(enchantmentTable, 0, 15, 47));
		addSlot(new EnchantFuelSlot(enchantmentTable, 1, 35, 47));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryplayer, i, 8 + i * 18, 142));
		}

		updateEnchantmentsCosts();
	}


	public boolean enchantItem(EntityPlayer player, int enchantOption)
	{
		if (!playerCanEnchant(player, enchantOption)) return false;

		int cost = enchantCost[enchantOption];
		if (player.gamemode != Gamemode.creative)
		{
			player.score -= cost;
			if (getSlot(1).hasStack())
				getSlot(1).getStack().stackSize -= enchantOption + 1;
		}

		ItemStack stack = getSlot(0).getStack();
		List<EnchantmentData> enchantments = EnchantmentUtils.generateEnchantmentsList(random, stack, cost);
		if (enchantments == null) return false;

		for (EnchantmentData enchantment : enchantments) {
			if (!EnchantmentLib.enchantItem(stack, enchantment)) {
				forceUpdateInventory();
				return false;
			}
		}

		forceUpdateInventory();

		return true;
	}


	@Override
	public void onCraftMatrixChanged(IInventory iinventory)
	{
		updateEnchantmentsCosts();
		super.onCraftMatrixChanged(iinventory);
	}

	void updateEnchantmentsCosts()
	{
		World world = enchantmentTable.worldObj;
		if (world == null) return;

		ItemStack stack = getSlot(0).getStack();

		if (stack == null) return;

		List<IEnchant> pool = EnchantmentUtils.getAllPossible(stack);
		if (pool.isEmpty()) return;


		int posX = enchantmentTable.x;
		int posY = enchantmentTable.y;
		int posZ = enchantmentTable.z;

		int bookshelfs = 0;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {

				if (x == 0 && z == 0) continue;

				if (!world.isAirBlock(posX + x, posY, posZ + z) || !world.isAirBlock(posX + x, posY + 1, posZ + z)) continue; // something obstructing the bookshelf

				int cornerBottom = world.getBlockId(posX + x * 2, posY, posZ + z * 2);
				int cornerTop = world.getBlockId(posX + x * 2, posY + 1, posZ + z * 2);

				if (cornerBottom == Block.bookshelfPlanksOak.id) bookshelfs++;
				if (cornerTop == Block.bookshelfPlanksOak.id) bookshelfs++;

				if (x == 0 || z == 0) continue;

				int sideXBottom = world.getBlockId(posX + x * 2, posY, posZ + z);
				int sideXTop = world.getBlockId(posX + x * 2, posY + 1, posZ + z);
				int sideZBottom = world.getBlockId(posX + x, posY, posZ + z * 2);
				int sideZTop = world.getBlockId(posX + x, posY + 1, posZ + z * 2);

				if (sideZBottom == Block.bookshelfPlanksOak.id) bookshelfs++;
				if (sideZTop == Block.bookshelfPlanksOak.id) bookshelfs++;
				if (sideXBottom == Block.bookshelfPlanksOak.id) bookshelfs++;
				if (sideXTop == Block.bookshelfPlanksOak.id) bookshelfs++;
			}
		}

		if (bookshelfs > 15)
			bookshelfs = 15;

		for (int i = 0; i < 3; i++)
		{
			enchantCost[i] = EnchantmentUtils.calcEnchantmentCost(i, bookshelfs);
		}
	}

	@Override
	public void updateInventory()
	{
		super.updateInventory();

		for (ICrafting crafting : crafters)
		{
			for (int i = 0; i < enchantCost.length; i++)
			{
				crafting.updateCraftingInventoryInfo(this, i, enchantCost[i]);
			}
		}
	}

	public void forceUpdateInventory()
	{
		for (int i = 0; i < this.inventorySlots.size(); i++)
		{
			ItemStack stack = inventorySlots.get(i).getStack();

			ItemStack stackCopy = stack != null ? stack.copy() : null;
			inventoryItemStacks.set(i, stackCopy);

			for (ICrafting crafter : this.crafters)
			{
				crafter.updateInventorySlot(this, i, stackCopy);
			}
		}

		updateInventory();
	}

	@Override
	public void updateClientProgressBar(int id, int value)
	{
		if (id >= 0 && id < enchantCost.length)
			enchantCost[id] = value;
	}

	public boolean playerCanEnchant(EntityPlayer player, int option)
	{
		return getSlot(0).hasStack() &&
			!EnchantmentLib.isItemEnchanted(getSlot(0).getStack()) &&
			(player.score >= enchantCost[option] || player.gamemode == Gamemode.creative) &&
			(getFuelAmount() > option || player.gamemode == Gamemode.creative);
	}

	public int getFuelAmount()
	{
		if (!getSlot(1).hasStack()) return 0;
		return getSlot(1).getStack().stackSize;
	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, EntityPlayer entityPlayer)
	{
		return null;
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, EntityPlayer entityPlayer)
	{
		return null;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return enchantmentTable.canInteractWith(player);
	}
}
