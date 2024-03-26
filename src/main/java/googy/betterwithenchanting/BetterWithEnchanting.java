package googy.betterwithenchanting;

import googy.betterwithenchanting.block.BlockEnchantmentTable;
import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import googy.betterwithenchanting.mixin.PacketMixin;
import googy.betterwithenchanting.network.packet.PacketEnchantItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.NetworkHelper;
import turniplabs.halplibe.helper.RecipeHelper;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.Properties;


public class BetterWithEnchanting implements ModInitializer, RecipeEntrypoint {
    public static final String MOD_ID = "betterwithenchanting";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);



	public static final Block enchantmentTable = new BlockBuilder(MOD_ID)
		.setBlockSound(BlockSounds.STONE)
		.setHardness(5)
		.setResistance(1200)
		.setLuminance(7)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE)
		.setBottomTexture("enchantment_table/bottom.png")
		.setSideTextures("enchantment_table/side.png")
		.setTopTexture("enchantment_table/top.png")
		.build(new BlockEnchantmentTable("enchantmenttable", Global.config.getInt("enchantment_table_id")));

	public BetterWithEnchanting()
	{
		PacketMixin.callAddIdClassMapping(Global.config.getInt("packet_enchant_id"), false, true, PacketEnchantItem.class);
	}


    @Override
    public void onInitialize() {

		LOG.info("BetterWithEnchanting initialized!");



    }

	public void onRecipesReady() {
		EntityHelper.Core.createTileEntity(TileEntityEnchantmentTable.class, "EnchantmentTable");

		RecipeHelper.Crafting.createRecipe(new ItemStack(enchantmentTable), new Object[]{
			" B ",
			"DCD",
			"CCC",
			'B', Item.book,
			'C', Block.cobbleStone,
			'D', Global.config.getBoolean("expensive_crafting") ? Block.blockDiamond : Item.diamond
		});
	}


}
