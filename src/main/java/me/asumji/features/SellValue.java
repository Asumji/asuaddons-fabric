package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Number;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SellValue {
    public static int totalValue = 0;

    public static void init() {
        ScreenEvents.AFTER_INIT.register(SellValue::screenOpen);
    }

    private static void screenOpen(MinecraftClient minecraftClient, Screen screen, int i, int i1) {
        totalValue = 0;
        if (screen instanceof GenericContainerScreen containerScreen) {
            AsuAddons.SCHEDULER.schedule(() -> {
                if (screen.getTitle().getString().equals("Manage Auctions") && ConfigManager.getConfig().miscCategory.financeAccordion.sellValueAH) {
                    for (ItemStack item : containerScreen.getScreenHandler().getInventory()) {
                        if (item.getComponents().get(DataComponentTypes.LORE) == null) continue;
                        for (Text line : item.getComponents().get(DataComponentTypes.LORE).lines()) {
                            String str = line.getString();
                            Matcher matcher = Pattern.compile("(?:Buy it now|Starting Bid|Top Bid|Sold for): (\\S*) coins").matcher(str);
                            if (!matcher.find()) continue;
                            if (matcher.group(1) != null)
                                totalValue += Integer.parseInt(matcher.group(1).replace(",", ""));
                        }
                    }
                    Screens.getButtons(containerScreen).add(new CustomWidget(ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudX, ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudY, 130, 27, Text.empty(), MinecraftClient.getInstance().textRenderer));
                } else if (screen.getTitle().getString().matches("(?:Co-op )?Bazaar Orders") && ConfigManager.getConfig().miscCategory.financeAccordion.sellValueBZ) {
                    for (ItemStack item : containerScreen.getScreenHandler().getInventory()) {
                        if (!item.getName().getString().startsWith("SELL") || item.getComponents().get(DataComponentTypes.LORE) == null) continue;
                        for (Text line : item.getComponents().get(DataComponentTypes.LORE).lines()) {
                            String str = line.getString();
                            Matcher matcher = Pattern.compile("Worth (\\S*) coins").matcher(str);
                            if (!matcher.find()) continue;
                            if (matcher.group(1) != null)
                                totalValue += Number.expandNumber(matcher.group(1)).intValue();
                        }
                    }
                    Screens.getButtons(containerScreen).add(new CustomWidget(ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudX, ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudY, 130, 27, Text.empty(), MinecraftClient.getInstance().textRenderer));
                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    private static class CustomWidget extends AbstractTextWidget {

        public CustomWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
            super(x, y, width, height, message, textRenderer);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (totalValue <= 0) return;
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudScale);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int width = textRenderer.getWidth("§6Total Value: " + Number.shortenNumber(BigDecimal.valueOf(totalValue))) + 18;

            context.fill((getX() - width / 2), getY(), (getX() - width / 2) + width, getY() + this.height, 0xFF5C5C5C);
            context.fill((getX() - width / 2), getY(), (getX() - width / 2) + width, getY() + 4, 0xFF444445);
            context.fill((getX() - width / 2), getY(), (getX() - width / 2) + 4, getY() + this.height, 0xFF444445);
            context.fill((getX() - width / 2), getY() + this.height, (getX() - width / 2) + width, getY() + this.height - 4, 0xFF444445);
            context.fill((getX() - width / 2) + width, getY(), (getX() - width / 2) + width - 4, getY() + this.height, 0xFF444445);
            context.drawTextWithShadow(textRenderer, Text.literal("§6Total Value: " + Number.shortenNumber(BigDecimal.valueOf(totalValue))), (getX() - width / 2) + 7, getY() + 18 - textRenderer.fontHeight, 0xFFFFFFFF);

            context.getMatrices().popMatrix();
        }
    }
}