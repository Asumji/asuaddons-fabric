package me.asumji.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.asumji.AsuAddons;
import me.asumji.features.WitherHitbox;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class Rendering {
    public static final RenderPipeline FILLED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(Identifier.of(AsuAddons.MOD_ID, "pipeline/filled_box"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
            .build()
    );


    private static final BufferAllocator allocator = new BufferAllocator(RenderLayer.CUTOUT_BUFFER_SIZE);
    private static BufferBuilder buffer;
    private static MappableRingBuffer vertexBuffer;

    public static void renderWaypoint(WorldRenderContext context, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline, float x1, float y1, float z1, float x2, float y2, float z2, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        assert matrices != null;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        if (buffer == null) {
            buffer = new BufferBuilder(allocator, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        }

        VertexRendering.drawFilledBox(matrices, buffer, x1, y1, z1, x2, y2, z2, r, g, b, alpha);

        matrices.pop();

        BuiltBuffer builtBuffer = buffer.end();
        BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);
        draw(MinecraftClient.getInstance(), pipeline, builtBuffer, drawParameters, vertices, new Vector4f(r,g,b,alpha));

        vertexBuffer.rotate();
        buffer = null;
    }

    private static GpuBuffer upload(BuiltBuffer.DrawParameters drawParameters, VertexFormat format, BuiltBuffer builtBuffer) {
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            vertexBuffer = new MappableRingBuffer(() -> AsuAddons.MOD_ID + " example render pipeline", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }

        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.getBlocking().slice(0, builtBuffer.getBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.getBuffer(), mappedView.data());
        }

        return vertexBuffer.getBlocking();
    }

    private static void draw(MinecraftClient client, RenderPipeline pipeline, BuiltBuffer builtBuffer, BuiltBuffer.DrawParameters drawParameters, GpuBuffer vertices, Vector4f color) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.DrawMode.QUADS) {
            builtBuffer.sortQuads(allocator, RenderSystem.getProjectionType().getVertexSorter());
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.getSortedBuffer());
            indexType = builtBuffer.getDrawParameters().indexType();
        } else {
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getIndexBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.getIndexType();
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .write(RenderSystem.getModelViewMatrix(), color, RenderSystem.getModelOffset(), RenderSystem.getTextureMatrix(), 1f);
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> AsuAddons.MOD_ID + " waypointRenderPipeline", client.getFramebuffer().getColorAttachmentView(), OptionalInt.empty(), client.getFramebuffer().getDepthAttachmentView(), OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);

            renderPass.drawIndexed(0, 0, drawParameters.indexCount(), 1);
        }

        builtBuffer.close();
    }

    public static void close() {
        allocator.close();

        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
    }
}
