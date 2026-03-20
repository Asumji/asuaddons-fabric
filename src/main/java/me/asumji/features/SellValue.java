package me.asumji.features;

import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.Number;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SellValue {
    public static int totalValue = 0;

    public static void init() {
        ScreenEvents.AFTER_INIT.register(SellValue::screenOpen);
    }

    private static void screenOpen(Minecraft minecraftClient, Screen screen, int i, int i1) {
        totalValue = 0;
        if (screen instanceof ContainerScreen containerScreen) {
            AsuAddons.SCHEDULER.schedule(() -> {
                if (screen.getTitle().getString().equals("Manage Auctions") && ConfigManager.getConfig().miscCategory.financeAccordion.sellValueAH) {
                    for (ItemStack item : containerScreen.getMenu().getContainer()) {
                        if (item.getComponents().get(DataComponents.LORE) == null) continue;
                        for (Component line : item.getComponents().get(DataComponents.LORE).lines()) {
                            String str = line.getString();
                            Matcher matcher = Pattern.compile("(?:Buy it now|Starting Bid|Top Bid|Sold for): (\\S*) coins").matcher(str);
                            if (!matcher.find()) continue;
                            if (matcher.group(1) != null)
                                totalValue += Integer.parseInt(matcher.group(1).replace(",", ""));
                        }
                    }
                    Screens.getButtons(containerScreen).add(new CustomWidget(ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudX, ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudY, 130, 27, Component.empty(), Minecraft.getInstance().font));
                } else if (screen.getTitle().getString().matches("(?:Co-op )?Bazaar Orders") && ConfigManager.getConfig().miscCategory.financeAccordion.sellValueBZ) {
                    for (ItemStack item : containerScreen.getMenu().getContainer()) {
                        if (!item.getHoverName().getString().startsWith("SELL") || item.getComponents().get(DataComponents.LORE) == null) continue;
                        for (Component line : item.getComponents().get(DataComponents.LORE).lines()) {
                            String str = line.getString();
                            Matcher matcher = Pattern.compile("Worth (\\S*) coins").matcher(str);
                            if (!matcher.find()) continue;
                            if (matcher.group(1) != null)
                                totalValue += Number.expandNumber(matcher.group(1)).intValue();
                        }
                    }
                    Screens.getButtons(containerScreen).add(new CustomWidget(ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudX, ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudY, 130, 27, Component.empty(), Minecraft.getInstance().font));
                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    private static class CustomWidget extends AbstractStringWidget {

        public CustomWidget(int x, int y, int width, int height, Component message, Font textRenderer) {
            super(x, y, width, height, message, textRenderer);
        }

        @Override
        public void visitLines(ActiveTextCollector activeTextCollector) {
            return;
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            if (totalValue <= 0) return;
            context.pose().pushMatrix();
            context.pose().scale(ConfigManager.getConfig().miscCategory.financeAccordion.sellValueHudScale);

            Font textRenderer = Minecraft.getInstance().font;
            int width = textRenderer.width("§6Total Value: " + Number.shortenNumber(BigDecimal.valueOf(totalValue))) + 18;

            context.fill((getX() - width / 2), getY(), (getX() - width / 2) + width, getY() + this.height, 0xFF5C5C5C);
            context.fill((getX() - width / 2), getY(), (getX() - width / 2) + width, getY() + 4, 0xFF444445);
            context.fill((getX() - width / 2), getY(), (getX() - width / 2) + 4, getY() + this.height, 0xFF444445);
            context.fill((getX() - width / 2), getY() + this.height, (getX() - width / 2) + width, getY() + this.height - 4, 0xFF444445);
            context.fill((getX() - width / 2) + width, getY(), (getX() - width / 2) + width - 4, getY() + this.height, 0xFF444445);
            context.drawString(textRenderer, Component.literal("§6Total Value: " + Number.shortenNumber(BigDecimal.valueOf(totalValue))), (getX() - width / 2) + 7, getY() + 18 - textRenderer.lineHeight, 0xFFFFFFFF);

            context.pose().popMatrix();
        }
    }
}