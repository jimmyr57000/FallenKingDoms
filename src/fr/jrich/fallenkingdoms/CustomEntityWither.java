package fr.jrich.fallenkingdoms;

import net.minecraft.server.v1_7_R4.EntityWither;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.World;

public class CustomEntityWither extends EntityWither {

    public CustomEntityWither(World world) {
        super(world);
    }

    @Override
    protected void aD() {
        super.aD();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(1000.0D);
        this.getAttributeInstance(GenericAttributes.d).setValue(0.0D);
        this.getAttributeInstance(GenericAttributes.b).setValue(0.0D);
    }

    @Override
    protected void bn() {}

    @Override
    public void g(double d1, double d2, double d3) {}

    @Override
    public int ca() {
        return 0;
    }
}
