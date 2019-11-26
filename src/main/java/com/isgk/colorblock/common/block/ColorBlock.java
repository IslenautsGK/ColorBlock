package com.isgk.colorblock.common.block;

import com.isgk.colorblock.ColorBlockMain;
import com.isgk.colorblock.common.tile.TileColor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ColorBlock extends Block {

	public static final PropertyInteger STATE_MARKER = PropertyInteger.create("marked_face", 0, 6);

	public ColorBlock() {
		super(Material.IRON);
		this.setUnlocalizedName("color_block");
		this.setCreativeTab(ColorBlockMain.CREATIVE_TAB);
		this.setLightOpacity(0);
		this.setLightLevel(1.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(STATE_MARKER, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STATE_MARKER });
	}

	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STATE_MARKER, meta);
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(STATE_MARKER);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileColor();
	}

}