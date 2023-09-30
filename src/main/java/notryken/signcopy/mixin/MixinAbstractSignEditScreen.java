package notryken.signcopy.mixin;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import notryken.signcopy.SignData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen
{
    protected MixinAbstractSignEditScreen(Text title) {
        super(title);
    }

    @Shadow
    private SignText text;

    @Shadow @Final private String[] messages;

    @Shadow public abstract void close();

    @Inject(method = "init", at = @At("RETURN"))
    private void addEditButtons(CallbackInfo ci)
    {
        ButtonWidget copyButton = ButtonWidget.builder(Text.of("Copy"),
                        (button) -> copyText())
                .dimensions(this.width / 2 - 100, this.height / 4 + 119, 60, 20)
                .build();
        this.addDrawableChild(copyButton);

        ButtonWidget insertButton = ButtonWidget.builder(Text.of("Insert"),
                        (button) -> insertText())
                .dimensions(this.width / 2 - 30, this.height / 4 + 119, 60, 20)
                .build();
        this.addDrawableChild(insertButton);

        ButtonWidget eraseButton = ButtonWidget.builder(Text.of("Erase"),
                        (button) -> eraseText())
                .dimensions(this.width / 2 + 40, this.height / 4 + 119, 60, 20)
                .build();
        this.addDrawableChild(eraseButton);
    }

    @Unique
    private void copyText()
    {
        if (this.text.hasText(client.player)) {
            SignData.copiedText = this.text;
            client.setScreen(null);
            client.player.sendMessage(Text.literal("Text copied from sign!"), true);
        }
        this.close();
    }

    @Unique
    private void insertText()
    {
        if (SignData.copiedText != null) {
            for(int i = 0; i < this.messages.length; i++) {
                this.messages[i] = SignData.copiedText.getMessage(i, false).getString();
            }
        }
        this.close();
    }

    @Unique
    private void eraseText()
    {
        Arrays.fill(this.messages, "");
        this.close();
    }
}
