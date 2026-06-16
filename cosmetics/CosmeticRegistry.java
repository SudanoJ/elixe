package elixe.cosmetics;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-UUID cosmetics registry. For now it holds a name prefix (a colored symbol)
 * shown before the player's name. Hardcoded today, but structured so a remote
 * source (e.g. a Vercel JSON fetched at startup) can later fill PREFIXES via
 * {@link #put}. Cosmetics are client-side: only players running this Elixe build
 * see them.
 */
public class CosmeticRegistry {

	private static final Map<UUID, String> PREFIXES = new HashMap<UUID, String>();

	static {
		put(UUID.fromString("a9f6d5f2-c4ee-4fc5-babd-f18308bf3d1c"), "§4❃§r ");
	}

	public static void put(UUID uuid, String prefix) {
		if (uuid != null && prefix != null) {
			PREFIXES.put(uuid, prefix);
		}
	}

	public static String getPrefix(UUID uuid) {
		return uuid == null ? null : PREFIXES.get(uuid);
	}
}
