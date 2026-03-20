package me.asumji.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.asumji.AsuAddons;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class Rendering {
    public static final RenderPipeline FILLED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(AsuAddons.MOD_ID, "pipeline/filled_box"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
            .build()
    );


    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private static BufferBuilder buffer;
    private static MappableRingBuffer vertexBuffer;
    static Vector4f waypointColor = new Vector4f(0,0,0,1);

    private static void renderFilledBox(Matrix4f positionMatrix, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        // Front Face
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Back face
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // Left face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Right face
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Top face
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Bottom face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
    }

    public static void renderWaypoint(WorldRenderContext context, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline, float x1, float y1, float z1, float x2, float y2, float z2, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        PoseStack matrices = context.matrices();
        Vec3 camera = context.worldState().cameraRenderState.pos;

        assert matrices != null;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        if (buffer == null) {
            buffer = new BufferBuilder(allocator, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        }

        renderFilledBox(matrices.last().pose(), buffer, x1, y1, z1, x2, y2, z2, r, g, b, alpha);

        matrices.popPose();

        MeshData builtBuffer = buffer.buildOrThrow();
        MeshData.DrawState drawParameters = builtBuffer.drawState();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);
        waypointColor.set(r,g,b,alpha);

        draw(Minecraft.getInstance(), pipeline, builtBuffer, drawParameters, vertices, waypointColor);

        vertexBuffer.rotate();
        buffer = null;
    }

    public static void renderLine(WorldRenderContext context, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline, double width, float x1, float y1, float z1, float x2, float y2, float z2, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        PoseStack matrices = context.matrices();
        Vec3 camera = context.worldState().cameraRenderState.pos;

        assert matrices != null;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        if (buffer == null) {
            buffer = new BufferBuilder(allocator, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        }

        Matrix4f matrix4f = matrices.last().pose();

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
        buffer.addVertex(matrix4f, x0, y0, z0).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x1v, y1v, z1v).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x2v, y2v, z2v).setColor(r,g,b,alpha);

        buffer.addVertex(matrix4f, x0, y0, z0).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x2v, y2v, z2v).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x3, y3, z3).setColor(r,g,b,alpha);

        // BACK FACE
        buffer.addVertex(matrix4f, x4, y4, z4).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x7, y7, z7).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x6, y6, z6).setColor(r,g,b,alpha);

        buffer.addVertex(matrix4f, x4, y4, z4).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x6, y6, z6).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x5, y5, z5).setColor(r,g,b,alpha);

        // LEFT FACE
        buffer.addVertex(matrix4f, x0, y0, z0).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x3, y3, z3).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x7, y7, z7).setColor(r,g,b,alpha);

        buffer.addVertex(matrix4f, x0, y0, z0).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x7, y7, z7).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x4, y4, z4).setColor(r,g,b,alpha);

        // RIGHT FACE
        buffer.addVertex(matrix4f, x1v, y1v, z1v).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x5, y5, z5).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x6, y6, z6).setColor(r,g,b,alpha);

        buffer.addVertex(matrix4f, x1v, y1v, z1v).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x6, y6, z6).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x2v, y2v, z2v).setColor(r,g,b,alpha);

        // TOP FACE
        buffer.addVertex(matrix4f, x0, y0, z0).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x4, y4, z4).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x5, y5, z5).setColor(r,g,b,alpha);

        buffer.addVertex(matrix4f, x0, y0, z0).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x5, y5, z5).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x1v, y1v, z1v).setColor(r,g,b,alpha);

        // BOTTOM FACE
        buffer.addVertex(matrix4f, x3, y3, z3).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x2v, y2v, z2v).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x6, y6, z6).setColor(r,g,b,alpha);

        buffer.addVertex(matrix4f, x3, y3, z3).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x6, y6, z6).setColor(r,g,b,alpha);
        buffer.addVertex(matrix4f, x7, y7, z7).setColor(r,g,b,alpha);

        matrices.popPose();

        MeshData builtBuffer = buffer.buildOrThrow();
        MeshData.DrawState drawParameters = builtBuffer.drawState();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);
        waypointColor.set(r,g,b,alpha);
        draw(Minecraft.getInstance(), pipeline, builtBuffer, drawParameters, vertices, waypointColor);

        vertexBuffer.rotate();
        buffer = null;
    }

    private static GpuBuffer upload(MeshData.DrawState drawParameters, VertexFormat format, MeshData builtBuffer) {
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            vertexBuffer = new MappableRingBuffer(() -> AsuAddons.MOD_ID + " example render pipeline", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }

        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), mappedView.data());
        }

        return vertexBuffer.currentBuffer();
    }

    private static void draw(Minecraft client, RenderPipeline pipeline, MeshData builtBuffer, MeshData.DrawState drawParameters, GpuBuffer vertices, Vector4f color) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.Mode.QUADS) {
            builtBuffer.sortQuads(allocator, RenderSystem.getProjectionType().vertexSorting());
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.indexBuffer());
            indexType = builtBuffer.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.type();
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrix(), color, new Vector3f(), new Matrix4f());
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> AsuAddons.MOD_ID + " waypointRenderPipeline", client.getMainRenderTarget().getColorTextureView(), OptionalInt.empty(), client.getMainRenderTarget().getDepthTextureView(), OptionalDouble.empty())) {
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