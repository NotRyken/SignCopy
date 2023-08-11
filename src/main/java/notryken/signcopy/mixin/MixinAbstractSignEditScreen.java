package notryken.signcopy.mixin;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import notryken.signcopy.SignData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen
{
    protected MixinAbstractSignEditScreen(Text title) {
        super(title);
    }

    @Shadow
    private SignText text;

    @Final
    @Shadow
    private String[] messages;

    @Inject(method = "init", at = @At("RETURN"))
    private void addTextButton(CallbackInfo ci)
    {
        if (SignData.frontText != null) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Insert Copied Text"),
                            (button) -> insertText())
                    .dimensions(this.width / 2 - 100, this.height / 4 + 169, 200, 20)
                    .build());
        }
    }

    @Unique
    private void insertText()
    {
        this.text = SignData.frontText.withColor(DyeColor.BLACK);

        for(int i = 0; i < this.messages.length; i++) {
            this.messages[i] = SignData.frontText.getMessage(i, false).getString();
        }
    }
}
