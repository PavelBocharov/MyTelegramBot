package marolok.tel;

public enum BotCommand {
    START("/start"),
    HELP("/help"),
    FOTO("/foto"),
    PHOTO("/photo"),
    GIF("/gif"),
    VIDEO("/video"),
    TEXT("/text")
    ;

    public String name;

    BotCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BotCommand getBotCommand(String name) {
        try {
            for (BotCommand botCommand : BotCommand.values()) {
                if (botCommand.getName().equalsIgnoreCase(name)) return botCommand;
            }
        } catch (Exception e) {
            return TEXT;
        }
        return TEXT;
    }
}
