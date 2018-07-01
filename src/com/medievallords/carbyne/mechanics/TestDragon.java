package com.medievallords.carbyne.mechanics;

import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;

public class TestDragon extends EntityEnderDragon {

    //bn head
    //bo body
    //bp tail
    //bq tail
    //br tail
    //bs wing
    //bt wing

    private float pit = 0, ya = 0;


    public TestDragon(World world) {
        super(world);
    }

    @Override
    public void m() {
        setPosition(locX, locY, locZ);

        Bukkit.broadcastMessage("X: " + locX + " Y: " + locY + " Z: " + locZ);

        this.bn.width = this.bn.length = 3.0F;
        this.bp.width = this.bp.length = 2.0F;
        this.bq.width = this.bq.length = 2.0F;
        this.br.width = this.br.length = 2.0F;
        this.bo.length = 3.0F;
        this.bo.width = 5.0F;
        this.bs.length = 2.0F;
        this.bs.width = 4.0F;
        this.bt.length = 3.0F;
        this.bt.width = 4.0F;

        pit++;
        ya++;
        this.bn.t_();
        this.bo.t_();
        this.bp.t_();
        this.bq.t_();
        this.br.t_();
        this.bs.t_();
        this.bt.t_();


        this.bn.setPositionRotation(locX, locY, locZ, pit, ya);
        this.bo.setPositionRotation(locX, locY, locZ, pit, ya);
        this.bp.setPositionRotation(locX, locY, locZ, 0f, 1f);
        this.bq.setPositionRotation(locX, locY, locZ, 90f, 90f);
        this.br.setPositionRotation(locX, locY, locZ, 0f, -90f);
        this.bs.setPositionRotation(locX, locY, locZ, 0f, -90f);
        this.bt.setPositionRotation(locX, locY, locZ, 0f, -90f);
    }

    /*@Override
    public void m() {
        float f;
        float f1;
        if (this.world.isClientSide) {
            f = MathHelper.cos(this.bv * 3.1415927F * 2.0F);
            f1 = MathHelper.cos(this.bu * 3.1415927F * 2.0F);
            if (f1 <= -0.3F && f >= -0.3F && !this.R()) {
                this.world.a(this.locX, this.locY, this.locZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
            }
        }

        this.bu = this.bv;
        float f2;
        if (this.getHealth() <= 0.0F) {
            f = (this.random.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.locX + (double)f, this.locY + 2.0D + (double)f1, this.locZ + (double)f2, 0.0D, 0.0D, 0.0D, new int[0]);
        } else {
            //this.n();
            f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
            f *= (float)Math.pow(2.0D, this.motY);
            if (this.bx) {
                this.bv += f * 0.5F;
            } else {
                this.bv += f;
            }

            this.yaw = MathHelper.g(this.yaw);
            if (this.ce()) {
                this.bv = 0.5F;
            } else {
                if (this.bl < 0) {
                    for(int i = 0; i < this.bk.length; ++i) {
                        this.bk[i][0] = (double)this.yaw;
                        this.bk[i][1] = this.locY;
                    }
                }

                if (++this.bl == this.bk.length) {
                    this.bl = 0;
                }

                this.bk[this.bl][0] = (double)this.yaw;
                this.bk[this.bl][1] = this.locY;
                double d3;
                double d0;
                double d1;
                double d2;
                float f3;
                float f6;
                float f7;
                if (this.world.isClientSide) {
                    if (this.bc > 0) {
                        d3 = this.locX + (this.bd - this.locX) / (double)this.bc;
                        d0 = this.locY + (this.be - this.locY) / (double)this.bc;
                        d1 = this.locZ + (this.bf - this.locZ) / (double)this.bc;
                        d2 = MathHelper.g(this.bg - (double)this.yaw);
                        this.yaw = (float)((double)this.yaw + d2 / (double)this.bc);
                        this.pitch = (float)((double)this.pitch + (this.bh - (double)this.pitch) / (double)this.bc);
                        --this.bc;
                        this.setPosition(d3, d0, d1);
                        this.setYawPitch(this.yaw, this.pitch);
                    }
                } else {
                    d3 = this.a - this.locX;
                    d0 = this.b - this.locY;
                    d1 = this.c - this.locZ;
                    d2 = d3 * d3 + d0 * d0 + d1 * d1;
                    double d8;
                    double d9;
                    double d4;
                    if (this.target != null) {
                        this.a = this.target.locX;
                        this.c = this.target.locZ;
                        d8 = this.a - this.locX;
                        d9 = this.c - this.locZ;
                        double d7 = Math.sqrt(d8 * d8 + d9 * d9);
                        d4 = 0.4000000059604645D + d7 / 80.0D - 1.0D;
                        if (d4 > 10.0D) {
                            d4 = 10.0D;
                        }

                        this.b = this.target.getBoundingBox().b + d4;
                    } else {
                        this.a += this.random.nextGaussian() * 2.0D;
                        this.c += this.random.nextGaussian() * 2.0D;
                    }

                    if (this.bw || d2 < 100.0D || d2 > 22500.0D || this.positionChanged || this.E) {
                        //this.cf();
                    }

                    d0 /= (double)MathHelper.sqrt(d3 * d3 + d1 * d1);
                    f3 = 0.6F;
                    d0 = MathHelper.a(d0, (double)(-f3), (double)f3);
                    this.motY += d0 * 0.10000000149011612D;
                    this.yaw = MathHelper.g(this.yaw);
                    d8 = 180.0D - MathHelper.b(d3, d1) * 180.0D / 3.1415927410125732D;
                    d9 = MathHelper.g(d8 - (double)this.yaw);
                    if (d9 > 50.0D) {
                        d9 = 50.0D;
                    }

                    if (d9 < -50.0D) {
                        d9 = -50.0D;
                    }

                    Vec3D vec3d = (new Vec3D(this.a - this.locX, this.b - this.locY, this.c - this.locZ)).a();
                    d4 = (double)(-MathHelper.cos(this.yaw * 3.1415927F / 180.0F));
                    Vec3D vec3d1 = (new Vec3D((double)MathHelper.sin(this.yaw * 3.1415927F / 180.0F), this.motY, d4)).a();
                    float f4 = ((float)vec3d1.b(vec3d) + 0.5F) / 1.5F;
                    if (f4 < 0.0F) {
                        f4 = 0.0F;
                    }

                    this.bb *= 0.8F;
                    float f5 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0F + 1.0F;
                    double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0D + 1.0D;
                    if (d10 > 40.0D) {
                        d10 = 40.0D;
                    }

                    this.bb = (float)((double)this.bb + d9 * (0.699999988079071D / d10 / (double)f5));
                    this.yaw += this.bb * 0.1F;
                    f6 = (float)(2.0D / (d10 + 1.0D));
                    f7 = 0.06F;
                    this.a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
                    if (this.bx) {
                        this.move(this.motX * 0.800000011920929D, this.motY * 0.800000011920929D, this.motZ * 0.800000011920929D);
                    } else {
                        this.move(this.motX, this.motY, this.motZ);
                    }

                    Vec3D vec3d2 = (new Vec3D(this.motX, this.motY, this.motZ)).a();
                    float f8 = ((float)vec3d2.b(vec3d1) + 1.0F) / 2.0F;
                    f8 = 0.8F + 0.15F * f8;
                    this.motX *= (double)f8;
                    this.motZ *= (double)f8;
                    this.motY *= 0.9100000262260437D;
                }

                this.aI = this.yaw;
                this.bn.width = this.bn.length = 3.0F;
                this.bp.width = this.bp.length = 2.0F;
                this.bq.width = this.bq.length = 2.0F;
                this.br.width = this.br.length = 2.0F;
                this.bo.length = 3.0F;
                this.bo.width = 5.0F;
                this.bs.length = 2.0F;
                this.bs.width = 4.0F;
                this.bt.length = 3.0F;
                this.bt.width = 4.0F;
                f1 = (float)(this.b(5, 1.0F)[1] - this.b(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
                f2 = MathHelper.cos(f1);
                float f9 = -MathHelper.sin(f1);
                float f10 = this.yaw * 3.1415927F / 180.0F;
                float f11 = MathHelper.sin(f10);
                float f12 = MathHelper.cos(f10);
                this.bo.t_();
                this.bo.setPositionRotation(this.locX + (double)(f11 * 0.5F), this.locY, this.locZ - (double)(f12 * 0.5F), 0.0F, 0.0F);
                this.bs.t_();
                this.bs.setPositionRotation(this.locX + (double)(f12 * 4.5F), this.locY + 2.0D, this.locZ + (double)(f11 * 4.5F), 0.0F, 0.0F);
                this.bt.t_();
                this.bt.setPositionRotation(this.locX - (double)(f12 * 4.5F), this.locY + 2.0D, this.locZ - (double)(f11 * 4.5F), 0.0F, 0.0F);
                if (!this.world.isClientSide && this.hurtTicks == 0) {
                    //a(getWorld().getEntities(this, this.bs.getBoundingBox().grow(4.0D, 2.0D, 4.0D).c(0.0D, -2.0D, 0.0D)));
                    //a(getWorld().getEntities(this, this.bt.getBoundingBox().grow(4.0D, 2.0D, 4.0D).c(0.0D, -2.0D, 0.0D)));
                    //b(getWorld().getEntities(this, this.bn.getBoundingBox().grow(1.0D, 1.0D, 1.0D)));
                }

                double[] adouble = this.b(5, 1.0F);
                double[] adouble1 = this.b(0, 1.0F);
                f3 = MathHelper.sin(this.yaw * 3.1415927F / 180.0F - this.bb * 0.01F);
                float f13 = MathHelper.cos(this.yaw * 3.1415927F / 180.0F - this.bb * 0.01F);
                this.bn.t_();
                this.bn.setPositionRotation(this.locX + (double)(f3 * 5.5F * f2), this.locY + (adouble1[1] - adouble[1]) * 1.0D + (double)(f9 * 5.5F), this.locZ - (double)(f13 * 5.5F * f2), 0.0F, 0.0F);

                for(int j = 0; j < 3; ++j) {
                    EntityComplexPart entitycomplexpart = null;
                    if (j == 0) {
                        entitycomplexpart = this.bp;
                    }

                    if (j == 1) {
                        entitycomplexpart = this.bq;
                    }

                    if (j == 2) {
                        entitycomplexpart = this.br;
                    }

                    double[] adouble2 = this.b(12 + j * 2, 1.0F);
                    float f14 = this.yaw * 3.1415927F / 180.0F + (float)MathHelper.g(adouble2[0] - adouble[0]) * 3.1415927F / 180.0F * 1.0F;
                    float f15 = MathHelper.sin(f14);
                    f6 = MathHelper.cos(f14);
                    f7 = 1.5F;
                    float f18 = (float)(j + 1) * 2.0F;
                    entitycomplexpart.t_();
                    entitycomplexpart.setPositionRotation(this.locX - (double)((f11 * f7 + f15 * f18) * f2), this.locY + (adouble2[1] - adouble[1]) * 1.0D - (double)((f18 + f7) * f9) + 1.5D, this.locZ + (double)((f12 * f7 + f6 * f18) * f2), 0.0F, 0.0F);
                }

                if (!this.world.isClientSide) {
                    //this.bx = this.b(this.bn.getBoundingBox()) | this.b(this.bo.getBoundingBox());
                }
            }
        }

    }*/
}
