package googy.betterwithenchanting.utils;

import googy.betterwithenchanting.Global;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import turing.enchantmentlib.EnchantmentLib;
import turing.enchantmentlib.api.EnchantmentData;
import turing.enchantmentlib.api.IEnchant;

import java.util.*;

public class EnchantmentUtils
{
	public static List<EnchantmentData> getPossible(ItemStack stack, int enchantability)
	{
		List<EnchantmentData> list = new ArrayList<>();

		for (IEnchant enchantment : EnchantmentLib.ENCHANTMENTS.values())
		{
			if (enchantment == null || !enchantment.canApplyToItem(stack)) continue;

			for (int level = 1; level <= enchantment.getMaxLevel(); level++)
			{
				if (enchantability >= level)
				{
					list.add(new EnchantmentData(enchantment, level));
				}
			}
		}


		return list;
	}

	public static List<IEnchant> getAllPossible(ItemStack stack) {
		List<IEnchant> enchants = new ArrayList<>();
		for (IEnchant enchant : EnchantmentLib.ENCHANTMENTS.values()) {
			if (enchant.canApplyToItem(stack)) {
				enchants.add(enchant);
			}
		}
		return enchants;
	}

	public static List<EnchantmentData> generateEnchantmentsList(Random random, ItemStack stack, int cost)
	{
		List<EnchantmentData> enchantments = new ArrayList<>();
		double costPercentage = (double) cost / Global.config.getInt("max_enchantment_cost");

		int itemEnchantability = Global.config.getInt("default_item_enchantability");

		int rand_enchantability = 1 + random.nextInt(itemEnchantability / 4 + 1) + random.nextInt(itemEnchantability / 4 + 1);
		int k = (int) (costPercentage * (30 - 1) + 1) + rand_enchantability;
		float rand_bonus_percent = 1 + (random.nextFloat() + random.nextFloat() - 1) * 0.15f;

		int enchantability = Math.round(k * rand_bonus_percent);
		if (enchantability < 0) enchantability = 1;

		List<EnchantmentData> possibleEnchantments = getPossible(stack, enchantability);
		if (possibleEnchantments.isEmpty()) return null;

		EnchantmentData randomEnchantment = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
		if (randomEnchantment != null)
		{
			enchantments.add(randomEnchantment);
			possibleEnchantments.remove(randomEnchantment);
		}

		for (int i = enchantability; random.nextInt(50) <= i; i >>= 1)
		{
			if (possibleEnchantments.isEmpty()) continue;

			randomEnchantment = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
			if (randomEnchantment != null)
			{
				enchantments.add(randomEnchantment);
				possibleEnchantments.remove(randomEnchantment);
			}
		}

		return enchantments;
	}

	public static int calcEnchantmentCost(int enchantOption, int bookshelves)
	{
		double percentage = (bookshelves + Global.START_COST_OFFSET) / (15.0 + Global.START_COST_OFFSET);
		percentage *= (enchantOption + 1) / 3.0;

		return (int) (Global.config.getInt("max_enchantment_cost") * percentage);
	}

}
