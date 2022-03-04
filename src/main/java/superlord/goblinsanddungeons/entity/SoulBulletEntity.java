package superlord.goblinsanddungeons.entity;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import superlord.goblinsanddungeons.init.EntityInit;
import superlord.goblinsanddungeons.init.ItemInit;
import superlord.goblinsanddungeons.init.ParticleInit;
import superlord.goblinsanddungeons.init.SoundInit;

public class SoulBulletEntity extends ThrowableItemProjectile {
	public SoulBulletEntity(EntityType<? extends SoulBulletEntity> p_i50159_1_, Level p_i50159_2_) {
		super(p_i50159_1_, p_i50159_2_);
	}

	public SoulBulletEntity(Level worldIn, LivingEntity throwerIn) {
		super(EntityInit.SOUL_BULLET.get(), throwerIn, worldIn);
	}

	public SoulBulletEntity(Level worldIn, double x, double y, double z) {
		super(EntityInit.SOUL_BULLET.get(), x, y, z, worldIn);
	}

	protected Item getDefaultItem() {
		return ItemInit.SOUL_BULLET.get();
	}

	public void tick() {
		super.tick();
		if (this.level.isClientSide) {
			this.spawnParticles(8);
			if (this.tickCount >= 300) {
				this.remove(RemovalReason.DISCARDED);
			}
		}
	}

	private void spawnParticles(int particleCount) {
		this.level.addParticle(ParticleInit.SOUL_BULLET, true, this.getX() - 0.1, this.getY() - 1, this.getZ() - 0.1 , 0, 0, 0);
	}

	@Override
	protected float getGravity() {
		return 0.0F;
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		Entity hitEntity = result.getEntity();
		if (hitEntity instanceof LivingEntity) {
			hitEntity.hurt(DamageSource.indirectMagic(this, this.getOwner()), 4);
			((LivingEntity)hitEntity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
		}
		level.playSound((Player) null, new BlockPos(this.getX(), this.getY(), this.getZ()), SoundInit.SOUL_BULLET_COLLISION, SoundSource.NEUTRAL, 1, 1);
		this.playSound(SoundInit.SOUL_BULLET_COLLISION, 1, 1);
	}

	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level.isClientSide) {
			level.playSound((Player) null, new BlockPos(this.getX(), this.getY(), this.getZ()), SoundInit.SOUL_BULLET_COLLISION, SoundSource.NEUTRAL, 1, 1);
			this.playSound(SoundInit.SOUL_BULLET_COLLISION, 1, 1);
			this.remove(RemovalReason.DISCARDED);
		}

	}
}
