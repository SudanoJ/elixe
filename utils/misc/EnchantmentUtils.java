package elixe.utils.misc;

import net.minecraft.nbt.NBTBase;

public class EnchantmentUtils {
	
		public String abreviation;
		public int level;
		
		public EnchantmentUtils(NBTBase nbtBase) {
			String[] ench;
			ench = translate(nbtBase.toString());
			this.abreviation = ench[1];
			this.level = Integer.valueOf(ench[0]);
		}
		
		public EnchantmentUtils(String abreviation, int level) {
			this.abreviation = abreviation;
			this.level = level;
		}
		
		public EnchantmentUtils(String abreviation) {
			this.abreviation = abreviation;
			this.level = 1;
		}
		
		String[] translate(String input) {
			input = input.replace("{lvl:", "").replace("id:", "").replace("s", "").replace("}", "");
			String[] ench = input.split(",");
			ench[1] = decode(ench[1]);
			return ench;
		}
		
		String decode(String input) {
			// {lvl:Xs,IDs} - formato que o nbt retorna
			if(input.equals("18"))
				return "bane";
			if(input.equals("20"))
				return "fire";
			if(input.equals("35"))
				return "fort";
			if(input.equals("19"))
				return "kb";
			if(input.equals("21"))
				return "loot";
			if(input.equals("16"))
				return "s";
			if(input.equals("34"))
				return "unb";
			return "NR";
		}
		
		public String getAbreviation() {
			return abreviation;
		}
		
		public int getLevel() {
			return level;
		}
		
		@Override
		public String toString() {
			return abreviation + level;
		}

}
