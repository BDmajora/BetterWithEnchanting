package googy.betterwithenchanting;

import googy.betterwithenchanting.block.BlockEnchantmentTable;
import googy.betterwithenchanting.block.client.EnchantmentTableRenderer;
import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import googy.betterwithenchanting.network.packet.PacketEnchantItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.item.Item;
import net.minecraft.core.sound.BlockSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class BetterWithEnchanting implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint, PreLaunchEntrypoint, ClientStartEntrypoint {
    public static final String MOD_ID = "betterwithenchanting";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	public static final Block enchantmentTable = new BlockBuilder(MOD_ID)
		.setBlockSound(BlockSounds.STONE)
		.setHardness(5)
		.setResistance(1200)
		.setLuminance(7)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE)
		.setBottomTexture("betterwithenchanting:block/enchantment_table_bottom")
		.setSideTextures("betterwithenchanting:block/enchantment_table_side")
		.setTopTexture("betterwithenchanting:block/enchantment_table_top")
		.build(new BlockEnchantmentTable("enchantmenttable", Global.config.getInt("enchantment_table_id")));

    @Override
    public void onInitialize() {
		LOG.info("BetterWithEnchanting initialized!");
    }

	@Override
	public void beforeGameStart() {
		EntityHelper.createSpecialTileEntity(TileEntityEnchantmentTable.class, "EnchantmentTable", () -> new EnchantmentTableRenderer());
	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void beforeClientStart() {
	}

	@Override
	public void afterClientStart() {

	}

	@Override
	public void onPreLaunch() {
		NetworkHelper.register(PacketEnchantItem.class, true, false);
	}

	@Override
	public void initNamespaces() {
		RecipeNamespace namespace = new RecipeNamespace();
		namespace.register("workbench", Registries.RECIPES.WORKBENCH);
		Registries.RECIPES.register(MOD_ID, namespace);
	}

	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID,
			" B ",
			"DCD",
			"CCC"
		)
			.addInput('B', Item.book)
			.addInput('C', "minecraft:cobblestones")
			.addInput('D', Global.config.getBoolean("expensive_crafting") ? Block.blockDiamond : Item.diamond)
			.create("enchantment_table", enchantmentTable.getDefaultStack());
	}
}
