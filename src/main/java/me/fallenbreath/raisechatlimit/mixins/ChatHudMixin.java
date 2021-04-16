package me.fallenbreath.raisechatlimit.mixins;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin
{
	@Mutable
	@Shadow @Final private List<ChatHudLine> messages;

	@Mutable
	@Shadow @Final private List<ChatHudLine> visibleMessages;

	@ModifyConstant(
			method = "addMessage(Lnet/minecraft/text/Text;IIZ)V",
			constant = @Constant(intValue = 100),
			require = 2
	)
	private int raiseChatLimitTo5000(int value)
	{
		return 5000;
	}

	@ModifyArgs(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V"
			)
	)
	private void makeSureTheScrollBarIsVisible(Args args)
	{
		int y1 = args.get(1);
		int y2 = args.get(3);
		// it's too short (length = 0)
		if (y1 == y2)
		{
			if (y1 < 0)
			{
				y1++;
			}
			else
			{
				y2--;
			}
			args.set(1, y1);
			args.set(3, y2);
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void replaceMessageContainerWithLinkedList(CallbackInfo ci)
	{
		this.messages = Lists.newLinkedList(this.messages);
		this.visibleMessages = Lists.newLinkedList(this.visibleMessages);
	}
}
