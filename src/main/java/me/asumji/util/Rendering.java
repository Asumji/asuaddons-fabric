package me.asumji.util;

import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class Rendering {
    public static void renderWaypoint(AABB box, Color color) {
        Gizmos.cuboid(box, GizmoStyle.fill(ARGB.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue())));
    }

    public static void renderLine(float width, Vec3 p1, Vec3 p2, Color color) {
        Gizmos.line(p1, p2, ARGB.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue()), width);
    }
}