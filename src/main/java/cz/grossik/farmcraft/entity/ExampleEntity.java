package cz.grossik.farmcraft.entity;

import java.util.Random;
import javax.annotation.Nullable;

import cz.grossik.farmcraft.init.BlockInit;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.*;

public class ExampleEntity extends AmbientEntity {
   private static final DataParameter<Byte> HANGING = EntityDataManager.createKey(ExampleEntity.class, DataSerializers.BYTE);
   private static final EntityPredicate field_213813_c = (new EntityPredicate()).setDistance(4.0D).allowFriendlyFire();
   private BlockPos spawnPosition;
   private BlockPos cropPosition;
   private boolean isCrop;
   private boolean moveToCrop = false;
   private int actualTick = 0;
   private int maxTick = 0;

   public ExampleEntity(EntityType<? extends ExampleEntity> p_i50290_1_, World p_i50290_2_) {
      super(p_i50290_1_, p_i50290_2_);
      this.setIsBatHanging(true);
   }

   protected void registerGoals() {
	   this.goalSelector.addGoal(1, new ExampleEntity.MorningGiftGoal(this));
   }
   
   protected void registerData() {
      super.registerData();
      this.dataManager.register(HANGING, (byte)0);
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 0.1F;
   }

   /**
    * Gets the pitch of living sounds in living entities.
    */
   protected float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_BAT_DEATH;
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   protected void collideWithNearbyEntities() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
   }

   public boolean getIsBatHanging() {
      return (this.dataManager.get(HANGING) & 1) != 0;
   }

   public void setIsBatHanging(boolean isHanging) {
      byte b0 = this.dataManager.get(HANGING);
      if (isHanging) {
         this.dataManager.set(HANGING, (byte)(b0 | 1));
      } else {
         this.dataManager.set(HANGING, (byte)(b0 & -2));
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.getIsBatHanging()) {
         this.setMotion(Vec3d.ZERO);
         this.setRawPosition(this.getPosX(), (double)MathHelper.floor(this.getPosY()) + 1.0D - (double)this.getHeight(), this.getPosZ());
      } else {
         this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D));
      }

   }

   protected void updateAITasks() {
      super.updateAITasks();
      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.up();
      if (this.getIsBatHanging()) {
         if (this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos)) {
            if (this.rand.nextInt(200) == 0) {
               this.rotationYawHead = (float)this.rand.nextInt(360);
            }

            if (this.world.getClosestPlayer(field_213813_c, this) != null) {
               this.setIsBatHanging(false);
               this.world.playEvent((PlayerEntity)null, 1025, blockpos, 0);
            }
         } else {
            this.setIsBatHanging(false);
            this.world.playEvent((PlayerEntity)null, 1025, blockpos, 0);
         }
      } else {
         if (this.spawnPosition != null && (!this.world.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
            this.spawnPosition = null;
         }

         if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.withinDistance(this.getPositionVec(), 2.0D)) {
            this.spawnPosition = new BlockPos(this.getPosX() + (double)this.rand.nextInt(7) - (double)this.rand.nextInt(7), this.getPosY() + (double)this.rand.nextInt(6) - 2.0D, this.getPosZ() + (double)this.rand.nextInt(7) - (double)this.rand.nextInt(7));
         }
         
         if (this.cropPosition == null && isCrop == false) {
        	 this.cropPosition = null;
        	 this.moveToCrop = false;
        	 this.actualTick = 0;
        	 this.maxTick = 0;
         }
         
         if (this.cropPosition == null || this.cropPosition.withinDistance(this.getPositionVec(), 100.0D) && isCrop == false) {
        	 this.cropPosition = new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ());
        	 if(this.world.getBlockState(this.cropPosition).getBlock().isIn(BlockTags.CROPS) && hasScarecrow(this.cropPosition) == false) {
        		 isCrop = true;
        	 }
    	 }
         
         if(this.cropPosition != null && this.isCrop) {
        	 if(!this.world.getBlockState(this.cropPosition).getBlock().isIn(BlockTags.CROPS)) {
        		 isCrop = false;
        		 this.actualTick = 0;
        		 this.maxTick = 0;
        	 }
         }
         
         if(this.cropPosition != null && this.isCrop && this.moveToCrop) {
        	 if(this.world.getBlockState(blockpos) == this.world.getBlockState(this.cropPosition)) {
        		 if(this.maxTick == 0) {
        			 Random random = new Random();
        			 //this.maxTick = random.nextInt((500 - 200) + 1) + 200;
        			 this.maxTick = random.nextInt((200 - 5) + 1) + 5;
        		 }
        	 }
         }
         
         if(this.maxTick != 0) {
        	 this.actualTick++;
        	 
        	 if(this.actualTick == this.maxTick) {
        		 this.world.setBlockState(this.cropPosition, Blocks.AIR.getDefaultState());
        	 }
         }
         
         if(this.hasScarecrow(blockpos)) {
		   	  if (this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos)) {
		          if(this.rand.nextInt(50) == 0) {
		        	  this.rotationYawHead = (float)this.rand.nextInt(360);
		          }  
			  }
         }

         /*if(isCrop) {
        	 System.err.println(isCrop);
        	 System.err.println(cropPosition);
         }*/
         
         if(!this.moveToCrop) {
	         double d0 = (double)this.spawnPosition.getX() + 0.5D - this.getPosX();
	         double d1 = (double)this.spawnPosition.getY() + 0.1D - this.getPosY();
	         double d2 = (double)this.spawnPosition.getZ() + 0.5D - this.getPosZ();
	         Vec3d vec3d = this.getMotion();
	         Vec3d vec3d1 = vec3d.add((Math.signum(d0) * 0.5D - vec3d.x) * (double)0.1F, (Math.signum(d1) * (double)0.7F - vec3d.y) * (double)0.1F, (Math.signum(d2) * 0.5D - vec3d.z) * (double)0.1F);
	         this.setMotion(vec3d1);
	         float f = (float)(MathHelper.atan2(vec3d1.z, vec3d1.x) * (double)(180F / (float)Math.PI)) - 90.0F;
	         float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
	         this.moveForward = 0.5F;
	         this.rotationYaw += f1;
	         if (this.rand.nextInt(100) == 0) {
	            this.setIsBatHanging(true);
	         }
         }
      }

   }
   
   public boolean hasScarecrow(BlockPos pos) {
	   for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
		   if(this.world.getBlockState(blockpos).getBlock() == BlockInit.scarecrow.get()) {
			   return true;
		   }
	   }
	   
	   return false;
   }
   
   public boolean getEating() {
	   if(this.actualTick > 0) {
		   return true;
	   }
	   
	   return false;
   }
   
   protected boolean canTriggerWalking() {
      return false;
   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      return false;
   }

   protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   /**
    * Return whether this entity should NOT trigger a pressure plate or a tripwire.
    */
   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         if (!this.world.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
         }

         return super.attackEntityFrom(source, amount);
      }
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.dataManager.set(HANGING, compound.getByte("BatFlags"));
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putByte("BatFlags", this.dataManager.get(HANGING));
   }

   public static boolean func_223369_b(EntityType<ExampleEntity> p_223369_0_, IWorld p_223369_1_, SpawnReason reason, BlockPos p_223369_3_, Random p_223369_4_) {
      if (p_223369_3_.getY() >= p_223369_1_.getSeaLevel()) {
         return false;
      } else {
    	  System.err.println(p_223369_3_);
		  for(BlockPos blockpos : BlockPos.getAllInBoxMutable(p_223369_3_.add(-5, -5, -5), p_223369_3_.add(5, 5, 5))) {
	   		   if(p_223369_1_.getBlockState(blockpos).getBlock() == BlockInit.scarecrow.get()) {
	   			   return false;
	   		   }
	   	   }
         return true;
      }
   }
   
   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return sizeIn.height * .6F;
   }
   
   static class MorningGiftGoal extends Goal {
	      private final ExampleEntity cat;
	      private int tickCounter;

	      public MorningGiftGoal(ExampleEntity catIn) {
	         this.cat = catIn;
	      }

	      /**
	       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	       * method as well.
	       */
	      public boolean shouldExecute() {	   
	    	  if(this.cat.isCrop) {
		    	  return !this.func_220805_g();
	    	  } else {
	    		  return false;
	    	  }
	      }

	      private boolean func_220805_g() {
	         for(ExampleEntity catentity : this.cat.world.getEntitiesWithinAABB(ExampleEntity.class, (new AxisAlignedBB(this.cat.cropPosition)).grow(2.0D))) {
	            if (catentity != this.cat) {
	               return true;
	            }
	         }

	         return false;
	      }

	      /**
	       * Returns whether an in-progress EntityAIBase should continue executing
	       */
	      public boolean shouldContinueExecuting() {
	         return this.cat.isCrop && this.cat.cropPosition != null && !this.func_220805_g();
	      }

	      /**
	       * Execute a one shot task or start executing a continuous task
	       */
	      public void startExecuting() {
	         if (this.cat.cropPosition != null) {
	            this.cat.getNavigator().tryMoveToXYZ((double)this.cat.cropPosition.getX(), (double)this.cat.cropPosition.getY(), (double)this.cat.cropPosition.getZ(), (double)0.5F);
	         }

	      }

	      /**
	       * Reset the task's internal state. Called when this task is interrupted by another one
	       */
	      public void resetTask() {
	         float f = this.cat.world.getCelestialAngle(1.0F);
	         if ((double)f > 0.77D && (double)f < 0.8D && (double)this.cat.world.getRandom().nextFloat() < 0.7D) {
	            this.func_220804_h();
	         }

	         this.tickCounter = 0;
	         this.cat.moveToCrop = false;
	         this.cat.getNavigator().clearPath();
	      }

	      private void func_220804_h() {
	         Random random = this.cat.getRNG();
	         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
	         blockpos$mutable.setPos(this.cat);
	         this.cat.attemptTeleport((double)(blockpos$mutable.getX() + random.nextInt(11) - 5), (double)(blockpos$mutable.getY() + random.nextInt(5) - 2), (double)(blockpos$mutable.getZ() + random.nextInt(11) - 5), false);
	         blockpos$mutable.setPos(this.cat);
	         LootTable loottable = this.cat.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_CAT_MORNING_GIFT);
	         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.cat.world)).withParameter(LootParameters.POSITION, blockpos$mutable).withParameter(LootParameters.THIS_ENTITY, this.cat).withRandom(random);

	         for(ItemStack itemstack : loottable.generate(lootcontext$builder.build(LootParameterSets.GIFT))) {
	            this.cat.world.addEntity(new ItemEntity(this.cat.world, (double)((float)blockpos$mutable.getX() - MathHelper.sin(this.cat.renderYawOffset * ((float)Math.PI / 180F))), (double)blockpos$mutable.getY(), (double)((float)blockpos$mutable.getZ() + MathHelper.cos(this.cat.renderYawOffset * ((float)Math.PI / 180F))), itemstack));
	         }

	      }

	      /**
	       * Keep ticking a continuous task that has already been started
	       */
	      public void tick() {
	         if (this.cat.cropPosition != null) {
	            this.cat.getNavigator().tryMoveToXYZ((double)this.cat.cropPosition.getX(), (double)this.cat.cropPosition.getY(), (double)this.cat.cropPosition.getZ(), (double)0.5F);
	            this.cat.moveToCrop = true;
	            /*if (this.cat.getDistanceSq(this.owner) < 2.5D) {
	               ++this.tickCounter;
	               if (this.tickCounter > 16) {
	                  //this.cat.func_213419_u(true);
	                  //this.cat.func_213415_v(false);
	               } else {
	                  this.cat.faceEntity(this.owner, 45.0F, 45.0F);
	                  //this.cat.func_213415_v(true);
	               }
	            } else {
	               //this.cat.func_213419_u(false);
	            }*/
	         }

	      }
	   }

}