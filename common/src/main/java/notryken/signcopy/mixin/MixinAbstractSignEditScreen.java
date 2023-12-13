package notryken.signcopy.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.network.chat.Component;
import notryken.signcopy.Constants;
import notryken.signcopy.SignCopy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen {
    protected MixinAbstractSignEditScreen(Component title) {
        super(title);
    }

    @Shadow
    private SignText text;

    @Shadow
    @Final
    private String[] messages;

    @Shadow
    public abstract void onClose();

    @Inject(method = "init", at = @At("RETURN"))
    private void addEditButtons(CallbackInfo ci) {
        Button copyButton = Button.builder(Component.nullToEmpty("Copy"),
                        (button) -> signCopy$copyText())
                .bounds(this.width / 2 - 100, this.height / 4 + 119, 60, 20)
                .build();
        this.addRenderableWidget(copyButton);

        Button insertButton = Button.builder(Component.nullToEmpty("Insert"),
                        (button) -> signCopy$insertText())
                .bounds(this.width / 2 - 30, this.height / 4 + 119, 60, 20)
                .build();
        this.addRenderableWidget(insertButton);

        Button eraseButton = Button.builder(Component.nullToEmpty("Erase"),
                        (button) -> signCopy$eraseText())
                .bounds(this.width / 2 + 40, this.height / 4 + 119, 60, 20)
                .build();
        this.addRenderableWidget(eraseButton);
    }

    @Unique
    private void signCopy$copyText() {
        if (this.text.hasMessage(minecraft.player)) {
            SignCopy.copiedText = this.text;
            this.onClose();
        }
    }

    @Unique
    private void signCopy$insertText() {
        if (SignCopy.copiedText != null) {
            for(int i = 0; i < this.messages.length; i++) {
                this.messages[i] = SignCopy.copiedText.getMessage(i, false).getString();
            }
            this.onClose();
        }
    }

    @Unique
    private void signCopy$eraseText() {
        if (!this.signCopy$isBlank()) {
            Arrays.fill(this.messages, "");
            this.onClose();
        }
    }

    @Unique
    private boolean signCopy$isBlank() {
        for (String s : this.messages) {
            if (!s.isBlank()) {
                return false;
            }
        }
        return true;
    }
}
