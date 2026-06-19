<div align="center">

<img src="elixe_logo.png" width="420" alt="Elixe">

**Utility client gratuito e open-source para Minecraft 1.8.9.**

[elixe.lol](https://elixe.lol) · free · open-source

</div>

---

## O que é

**Elixe** é um utility client de Minecraft **1.8.9** focado em PvP e qualidade de vida, com um
ClickGUI moderno (tema dark + ciano) e um conjunto enxuto de módulos bem-feitos. Esta é a versão
**standalone** — você compila o código no seu próprio workspace decompilado do Minecraft.

> **Prefere instalar como mod?** O Elixe também existe como **mod de Forge + Mixin** (instala como
> qualquer outro mod, mais fácil) em **[SudanoJ/elixe-mod](https://github.com/SudanoJ/elixe-mod)**.
> Use a versão que preferir.

## Compilando

Não é permitido distribuir o código-fonte decompilado do Minecraft de forma alguma. Por isso, este
repositório contém **apenas os arquivos adicionados pós-decompilação**. Não há um manual formal,
mas, se quiser compilar o Elixe você mesmo, adicionamos comentários nos métodos (quando preciso)
indicando onde modificar as classes externas (`net.minecraft.*`).

## Conteúdo licenciado / Créditos

### LiquidBounce
[LiquidBounce](https://github.com/CCBlueX/LiquidBounce) é um hacked-client gratuito baseado em
mixin pra Minecraft com Forge. Usamos a [classe World To Screen](https://github.com/CCBlueX/LiquidBounce/blob/master/shared/main/java/net/ccbluex/liquidbounce/utils/render/WorldToScreen.java)
e [o método getAngleDifference() do RotationUtils](https://github.com/CCBlueX/LiquidBounce/blob/3f5cb5e3e65dcd3dcf12e3ae68dac33ff914f544/shared/main/java/net/ccbluex/liquidbounce/utils/RotationUtils.java#L261).

### Alpine
[Alpine](https://github.com/ZeroMemes/Alpine) é um framework de event system com interfaces
funcionais. Usamos ele no Elixe pra facilitar as chamadas de evento.

### TheAlteningAuth4j
[TheAlteningAuth4j](https://github.com/TheAltening/TheAlteningAuth4j) é uma API de auth do
TheAltening feita por [Vladymyr](https://github.com/Vladymyr). Usamos pra trocar de service type
rápido no AltManager.

## Licença

Sujeito à [GNU General Public License v3.0](LICENSE).
