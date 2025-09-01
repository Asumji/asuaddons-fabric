package me.asumji.util;

import me.asumji.AsuAddons;

import net.minecraft.client.MinecraftClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HTTP {
    public static CompletableFuture<HttpResponse<String>> GetRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "AsuAddons v" + AsuAddons.modVersion + " | "+ MinecraftClient.getInstance().getSession().getUsername())
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
