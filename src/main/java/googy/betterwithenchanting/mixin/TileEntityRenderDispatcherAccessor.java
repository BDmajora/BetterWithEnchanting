package googy.betterwithenchanting.mixin;

import net.minecraft.client.render.tileentity.TileEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(targets = "net.minecraft.client.render.TileEntityRenderDispatcher", remap = false)
public interface TileEntityRenderDispatcherAccessor {
	@Accessor
	Map<Class<?>, TileEntityRenderer<?>> getRenderers();
}
