package googy.betterwithenchanting.block;

import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import googy.betterwithenchanting.interfaces.mixins.IEntityPlayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;

public class BlockEnchantmentTable extends BlockTileEntity
{

	public BlockEnchantmentTable(String key, int id)
	{
		super(key, id, Material.stone);
		setBlockBounds(0, 0, 0, 1, 12f / 16, 1);
	}

	@Override
	public boolean isSolidRender()
	{
		return false;
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xPlaced, double yPlaced)
	{
		if (world.isClientSide) return true;

		TileEntityEnchantmentTable tile = (TileEntityEnchantmentTable) world.getBlockTileEntity(x, y, z);
		if (tile != null)
			((IEntityPlayer)player).displayGUIEnchantmentTable(tile);

		return true;
	}

	@Override
	protected TileEntity getNewBlockEntity()
	{
		return new TileEntityEnchantmentTable();
	}
}
