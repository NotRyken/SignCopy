package notryken.signcopy.mixin;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import notryken.signcopy.SignCopy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public class MixinAbstractSignBlock
{
    @Inject(method = "onUse", at = @At("HEAD"))
    public void onSignUse(BlockState state, World world, BlockPos pos,
                          PlayerEntity player, Hand hand, BlockHitResult hit,
                          CallbackInfoReturnable<ActionResult> cir)
    {
        if (world.getBlockEntity(pos) instanceof SignBlockEntity sign
                && player.getMainHandStack().isEmpty()) {
            Text[] texts = sign.getTexts(player.shouldFilterText());
            String[] strings = new String[texts.length];
            boolean blank = true;
            for (int i = 0; i < texts.length; i++) {
                strings[i] = texts[i].getString();
                if (!strings[i].isBlank()) {
                    blank = false;
                }
            }
            if (!blank) {
                SignCopy.copiedText = strings;
                player.sendMessage(Text.literal("Text copied from sign!"), true);
            }
        }
    }
}
