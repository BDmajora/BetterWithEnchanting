package googy.betterwithenchanting.block.client;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.MathHelper;
import org.lwjgl.opengl.GL11;

public class EnchantmentTableRenderer extends TileEntityRenderer<TileEntityEnchantmentTable> {
	private static final BookModel model = new BookModel();
	private static final String texturePath = "assets/" + BetterWithEnchanting.MOD_ID + "/book/book.png";
	private final EntityItem entityItem = new EntityItem(null);
	private final ItemEntityRenderer renderer = new ItemEntityRenderer();

	{
		renderer.setRenderDispatcher(EntityRenderDispatcher.instance);
	}

	@Override
	public void doRender(Tessellator tessellator, TileEntityEnchantmentTable tileEntity, double x, double y, double z, float partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
		float t = tileEntity.ticks + partialTicks;
		float f;
		GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(t * 0.1F) * 0.01F, 0.0F);

		for (f = tileEntity.bookRot - tileEntity.prevBookRot; f >= (float)Math.PI; f -= ((float)Math.PI * 2.0F));
		while (f < -(float)Math.PI) f += ((float)Math.PI * 2.0F);

		float f2 = tileEntity.prevBookRot + f * partialTicks;
		GL11.glRotatef(-f2 * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
		this.loadTexture(texturePath);
		float f3 = tileEntity.prevPageFlip + (tileEntity.pageFlip - tileEntity.prevPageFlip) * partialTicks + 0.25F;
		float f4 = tileEntity.prevPageFlip + (tileEntity.pageFlip - tileEntity.prevPageFlip) * partialTicks + 0.75F;
		f3 = (f3 - (float)MathHelper.floor_float(f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float)MathHelper.floor_float(f4)) * 1.6F - 0.3F;

		if (f3 < 0.0F) f3 = 0.0F;
		if (f4 < 0.0F) f4 = 0.0F;
		if (f3 > 1.0F) f3 = 1.0F;
		if (f4 > 1.0F) f4 = 1.0F;

		float f5 = tileEntity.prevBookSpread + (tileEntity.bookSpread - tileEntity.prevBookSpread) * partialTicks;
		GL11.glEnable(GL11.GL_CULL_FACE);
		model.render(t, f3, f4, f5, 0.0F, 0.0625F);
		GL11.glPopMatrix();

		ItemStack stack = tileEntity.getStackInSlot(0);
		if (stack == null) return;

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
		if (Minecraft.getMinecraft(this).gameSettings.items3D.value || (stack.getItem() instanceof ItemBlock && BlockModelDispatcher.getInstance().getDispatch(((ItemBlock) stack.getItem()).getBlock()).shouldItemRender3d())) {
			GL11.glRotatef(-(tileEntity.itemRot + 0.01F * partialTicks) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		}
		entityItem.item = new ItemStack(stack.itemID, 1, stack.getMetadata(), stack.getData());
		entityItem.entityBrightness = tileEntity.worldObj.getLightBrightness(tileEntity.x, tileEntity.y, tileEntity.z);
		renderer.doRender(tessellator, entityItem, 0D, -0.3D, 0D, 0F, 0F);
		GL11.glPopMatrix();
	}
}
