package notryken.signcopy.mixin;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin
{

    @Inject(method = "onUse", at = @At("HEAD"))
    public void onSignUse(BlockState state, World world, BlockPos pos,
                          PlayerEntity player, Hand hand, BlockHitResult hit,
                          CallbackInfoReturnable<ActionResult> cir)
    {
        if (player.getMainHandStack().isEmpty()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {

                /* Using an accessor here to get the lines, didn't want to
                hardcode the number of lines for possible mod compatibility
                 */
                Text[] lines = ((SignBlockEntityAccessor)signBlockEntity).getTexts();

                StringBuilder textToCopy = new StringBuilder();

                for (Text text : lines) {

                    String rowString = text.getString();

                    if (!rowString.isEmpty()) {
                        textToCopy.append(rowString);
                        textToCopy.append("\n");
                    }

                }

                MinecraftClient.getInstance().keyboard.setClipboard(
                        textToCopy.toString());

                player.sendMessage(Text.literal(
                        "The text from the sign was copied to your clipboard!"),
                        true);
            }
        }
    }
}
