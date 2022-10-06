package org.rima_dcbot.utils;

import net.dv8tion.jda.api.entities.Message;
import org.rima_dcbot.configuration.ConfigurationUtil;

import java.text.Normalizer;
import java.util.Locale;

public class Utils {
	public static String normalizeText(String text) {
		return Normalizer.normalize(
			text
				// replace ñ and ç to random chars to bypass them in the normalizer
				.replace('ñ', '\001')
				.replace('ç', '\002'),
			Normalizer.Form.NFD
		)
		.replaceAll("\\p{M}","")
		
		// replace ñ and ç back
		.replace('\001', 'ñ')
		.replace('\002', 'ç')
		// filter other marks like parenthesis, dots, exclamation, etc
		.replaceAll("[^\\p{IsLatin}\\d\\s]", "");
	}
	
	public static double getDefaultWeight() {
		String defaultWeightString = ConfigurationUtil.getInstance().getProperty("DEFAULT_WEIGHT");
		if (defaultWeightString != null) return Double.parseDouble(defaultWeightString);
		return 1.0;
	}
}
