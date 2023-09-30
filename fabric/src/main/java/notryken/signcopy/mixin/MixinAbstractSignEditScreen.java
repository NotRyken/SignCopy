package notryken.signcopy.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import notryken.signcopy.SignCopy;
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

    @Shadow @Final protected String[] text;

    @Shadow public abstract void close();

    @Inject(method = "init", at = @At("RETURN"))
    private void addEditButtons(CallbackInfo ci)
    {
        ButtonWidget copyButton = ButtonWidget.builder(Text.of("Copy"),
                        (button) -> copyText())
                .dimensions(this.width / 2 - 100, this.height / 4 + 94, 95, 20)
                .build();
        this.addDrawableChild(copyButton);

        ButtonWidget insertButton = ButtonWidget.builder(Text.of("Insert"),
                        (button) -> insertText())
                .dimensions(this.width / 2 + 5, this.height / 4 + 94, 95, 20)
                .build();
        insertButton.active = SignCopy.copiedText != null;
        this.addDrawableChild(insertButton);
    }

    @Unique
    private void copyText()
    {
        if (!this.isBlank()) {
            SignCopy.copiedText = this.text;
        }
    }

    @Unique
    private void insertText()
    {
        if (SignCopy.copiedText != null) {
            System.arraycopy(SignCopy.copiedText, 0, this.text, 0, this.text.length);
            this.close();
        }
    }

    @Unique
    private boolean isBlank()
    {
        for (String s : this.text) {
            if (!s.isBlank()) {
                return false;
            }
        }
        return true;
    }
}
