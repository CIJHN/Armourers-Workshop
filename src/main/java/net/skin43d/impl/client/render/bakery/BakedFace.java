package net.skin43d.impl.client.render.bakery;

import net.minecraft.util.math.MathHelper;
import net.skin43d.impl.client.render.nbake.BakeSkinPart;
import net.skin43d.skin3d.ISkinDye;

public class BakedFace {
    private final byte x;
    private final byte y;
    private final byte z;

    private final byte r;
    private final byte g;
    private final byte b;
    private final byte a;

    private final byte t;
    private final byte face;
    private final byte lodLevel;

    public BakedFace(byte x, byte y, byte z, byte r, byte g, byte b, byte a, byte paintType, byte face, byte lodLevel) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        this.t = paintType;
        this.face = face;
        this.lodLevel = lodLevel;
    }

    public void render(ISkinDye skinDye, byte[] extraColour, BakeSkinPart data, boolean useTexture) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        int type = t & 0xFF;
        if (type != 0) {
            //Dye
            if (type >= 1 && type <= 8) {
                //Is a dye paint
                if (skinDye != null && skinDye.haveDyeInSlot(type - 1)) {
                    byte[] dye = skinDye.getDyeColour(type - 1);
                    if (dye.length == 4) {
                        if ((dye[3] & 0xFF) == 0)
                            return;
                        int dyeType = dye[3] & 0xFF;
                        int[] averageRGB = data.getAverageDyeColour(type - 1);
                        byte[] dyedColour;
                        if (dyeType == 253 & extraColour != null) {
                            dyedColour = dyeColour(r, g, b, new byte[]{extraColour[0], extraColour[1], extraColour[2]}, averageRGB);
                        } else if (dyeType == 254 & extraColour != null) {
                            dyedColour = dyeColour(r, g, b, new byte[]{extraColour[3], extraColour[4], extraColour[5]}, averageRGB);
                        } else {
                            dyedColour = dyeColour(r, g, b, dye, averageRGB);
                        }
                        r = dyedColour[0];
                        g = dyedColour[1];
                        b = dyedColour[2];
                    }
                }
            }
            //Skin
            if (type == 253 & extraColour != null) {
                int[] averageRGB = data.getAverageDyeColour(8);
                byte[] dyedColour = dyeColour(r, g, b, new byte[]{extraColour[0], extraColour[1], extraColour[2]}, averageRGB);
                r = dyedColour[0];
                g = dyedColour[1];
                b = dyedColour[2];
            }
            //Hair
            if (type == 254 & extraColour != null) {
                int[] averageRGB = data.getAverageDyeColour(9);
                byte[] dyedColour = dyeColour(r, g, b, new byte[]{extraColour[3], extraColour[4], extraColour[5]}, averageRGB);
                r = dyedColour[0];
                g = dyedColour[1];
                b = dyedColour[2];
            }
            BakedFaceRenderer.renderFace(x, y, z, r, g, b, a, face, useTexture, lodLevel);
        }
    }

    public void render(ISkinDye skinDye, byte[] extraColour, BakedPart data, boolean useTexture) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        int type = t & 0xFF;
        if (type != 0) {
            //Dye
            if (type >= 1 && type <= 8) {
                //Is a dye paint
                if (skinDye != null && skinDye.haveDyeInSlot(type - 1)) {
                    byte[] dye = skinDye.getDyeColour(type - 1);
                    if (dye.length == 4) {
                        if ((dye[3] & 0xFF) == 0) {
                            return;
                        }
                        int dyeType = dye[3] & 0xFF;
                        int[] averageRGB = data.getAverageDyeColour(type - 1);
                        byte[] dyedColour = null;
                        if (dyeType == 253 & extraColour != null) {
                            dyedColour = dyeColour(r, g, b, new byte[]{extraColour[0], extraColour[1], extraColour[2]}, averageRGB);
                        } else if (dyeType == 254 & extraColour != null) {
                            dyedColour = dyeColour(r, g, b, new byte[]{extraColour[3], extraColour[4], extraColour[5]}, averageRGB);
                        } else {
                            dyedColour = dyeColour(r, g, b, dye, averageRGB);
                        }
                        r = dyedColour[0];
                        g = dyedColour[1];
                        b = dyedColour[2];
                    }
                }
            }
            //Skin
            if (type == 253 & extraColour != null) {
                int[] averageRGB = data.getAverageDyeColour(8);
                byte[] dyedColour = dyeColour(r, g, b, new byte[]{extraColour[0], extraColour[1], extraColour[2]}, averageRGB);
                r = dyedColour[0];
                g = dyedColour[1];
                b = dyedColour[2];
            }
            //Hair
            if (type == 254 & extraColour != null) {
                int[] averageRGB = data.getAverageDyeColour(9);
                byte[] dyedColour = dyeColour(r, g, b, new byte[]{extraColour[3], extraColour[4], extraColour[5]}, averageRGB);
                r = dyedColour[0];
                g = dyedColour[1];
                b = dyedColour[2];
            }
            BakedFaceRenderer.renderFace(x, y, z, r, g, b, a, face, useTexture, lodLevel);
        }
    }

    /**
     * Create a new colour for a dyed vertex.
     *
     * @param dyeColour          RGB byte array.
     * @param modelAverageColour RGB int array.
     */
    public static byte[] dyeColour(byte r, byte g, byte b, byte[] dyeColour, int[] modelAverageColour) {
        int average = ((r & 0xFF) + (g & 0xFF) + (b & 0xFF)) / 3;
        int modelAverage = (modelAverageColour[0] + modelAverageColour[1] + modelAverageColour[2]) / 3;

        int nR = average + (dyeColour[0] & 0xFF) - modelAverage;
        int nG = average + (dyeColour[1] & 0xFF) - modelAverage;
        int nB = average + (dyeColour[2] & 0xFF) - modelAverage;

        nR = MathHelper.clamp_int(nR, 0, 255);
        nG = MathHelper.clamp_int(nG, 0, 255);
        nB = MathHelper.clamp_int(nB, 0, 255);
        return new byte[]{(byte) nR, (byte) nG, (byte) nB};
    }
}
