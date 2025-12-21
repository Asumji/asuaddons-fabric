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
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
    static Vector4f waypointColor = new Vector4f(0,0,0,1);

    public static void renderWaypoint(WorldRenderContext context, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline, float x1, float y1, float z1, float x2, float y2, float z2, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        MatrixStack matrices = context.matrices();
        Vec3d camera = context.worldState().cameraRenderState.pos;

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
        waypointColor.set(r,g,b,alpha);
        draw(MinecraftClient.getInstance(), pipeline, builtBuffer, drawParameters, vertices, waypointColor);

        vertexBuffer.rotate();
        buffer = null;
    }

    public static void renderLine(WorldRenderContext context, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline, double width, float x1, float y1, float z1, float x2, float y2, float z2, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        MatrixStack matrices = context.matrices();
        Vec3d camera = context.worldState().cameraRenderState.pos;

        assert matrices != null;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        if (buffer == null) {
            buffer = new BufferBuilder(allocator, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        }

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;

        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length == 0) return;

        dx /= length;
        dy /= length;
        dz /= length;

        float upX = 0, upY = 1, upZ = 0;
        if (Math.abs(dx * upX + dy * upY + dz * upZ) > 0.99f) {
            upX = 1;
            upY = 0;
            upZ = 0;
        }

        float rx = dy * upZ - dz * upY;
        float ry = dz * upX - dx * upZ;
        float rz = dx * upY - dy * upX;

        float rLen = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
        rx = (float) ((rx / rLen) * (width/2));
        ry = (float) ((ry / rLen) * (width/2));
        rz = (float) ((rz / rLen) * (width/2));

        float ux = ry * dz - rz * dy;
        float uy = rz * dx - rx * dz;
        float uz = rx * dy - ry * dx;

        float uLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        ux = (float) ((ux / uLen) * (width/2));
        uy = (float) ((uy / uLen) * (width/2));
        uz = (float) ((uz / uLen) * (width/2));

        float x0 = x1 + rx + ux, y0 = y1 + ry + uy, z0 = z1 + rz + uz;
        float x1v = x1 - rx + ux, y1v = y1 - ry + uy, z1v = z1 - rz + uz;
        float x2v = x1 - rx - ux, y2v = y1 - ry - uy, z2v = z1 - rz - uz;
        float x3 = x1 + rx - ux, y3 = y1 + ry - uy, z3 = z1 + rz - uz;

        float x4 = x2 + rx + ux, y4 = y2 + ry + uy, z4 = z2 + rz + uz;
        float x5 = x2 - rx + ux, y5 = y2 - ry + uy, z5 = z2 - rz + uz;
        float x6 = x2 - rx - ux, y6 = y2 - ry - uy, z6 = z2 - rz - uz;
        float x7 = x2 + rx - ux, y7 = y2 + ry - uy, z7 = z2 + rz - uz;

        // FRONT FACE
        buffer.vertex(matrix4f, x0, y0, z0).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x1v, y1v, z1v).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x2v, y2v, z2v).color(r,g,b,alpha);

        buffer.vertex(matrix4f, x0, y0, z0).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x2v, y2v, z2v).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x3, y3, z3).color(r,g,b,alpha);

        // BACK FACE
        buffer.vertex(matrix4f, x4, y4, z4).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x7, y7, z7).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x6, y6, z6).color(r,g,b,alpha);

        buffer.vertex(matrix4f, x4, y4, z4).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x6, y6, z6).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x5, y5, z5).color(r,g,b,alpha);

        // LEFT FACE
        buffer.vertex(matrix4f, x0, y0, z0).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x3, y3, z3).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x7, y7, z7).color(r,g,b,alpha);

        buffer.vertex(matrix4f, x0, y0, z0).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x7, y7, z7).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x4, y4, z4).color(r,g,b,alpha);

        // RIGHT FACE
        buffer.vertex(matrix4f, x1v, y1v, z1v).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x5, y5, z5).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x6, y6, z6).color(r,g,b,alpha);

        buffer.vertex(matrix4f, x1v, y1v, z1v).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x6, y6, z6).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x2v, y2v, z2v).color(r,g,b,alpha);

        // TOP FACE
        buffer.vertex(matrix4f, x0, y0, z0).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x4, y4, z4).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x5, y5, z5).color(r,g,b,alpha);

        buffer.vertex(matrix4f, x0, y0, z0).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x5, y5, z5).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x1v, y1v, z1v).color(r,g,b,alpha);

        // BOTTOM FACE
        buffer.vertex(matrix4f, x3, y3, z3).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x2v, y2v, z2v).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x6, y6, z6).color(r,g,b,alpha);

        buffer.vertex(matrix4f, x3, y3, z3).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x6, y6, z6).color(r,g,b,alpha);
        buffer.vertex(matrix4f, x7, y7, z7).color(r,g,b,alpha);

        matrices.pop();

        BuiltBuffer builtBuffer = buffer.end();
        BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);
        waypointColor.set(r,g,b,alpha);
        draw(MinecraftClient.getInstance(), pipeline, builtBuffer, drawParameters, vertices, waypointColor);

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
                .write(RenderSystem.getModelViewMatrix(), color, new Vector3f(), RenderSystem.getTextureMatrix(), 1f);
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