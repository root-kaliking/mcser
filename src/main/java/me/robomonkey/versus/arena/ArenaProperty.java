package me.robomonkey.versus.arena;

public enum ArenaProperty {
    CENTER_LOCATION("竞技场中心点"),
    SPAWN_LOCATION_ONE("第一个出生点"),
    SPAWN_LOCATION_TWO("第二个出生点"),
    SPECTATE_LOCATION("观战点"),
    KIT("物品包"),
    LOBBY_LOCATION("大厅复活点");

    private String friendlyString;

    ArenaProperty(String friendlyVersion) {
        friendlyString = friendlyVersion;
    }

    public ArenaProperty getNextProperty() {
        switch (this) {
            case CENTER_LOCATION:
                return SPAWN_LOCATION_ONE;
            case SPAWN_LOCATION_ONE:
                return SPAWN_LOCATION_TWO;
            case SPAWN_LOCATION_TWO:
                return SPECTATE_LOCATION;
            case SPECTATE_LOCATION:
                return KIT;
            case KIT:
                return LOBBY_LOCATION;
            default:
                return null;
        }
    }

    public String getExplanation() {
        switch (this) {
            case CENTER_LOCATION:
                return "选择竞技场的中心点, 通常位于两个决斗者的中间。";
            case SPAWN_LOCATION_ONE:
                return "决定决斗中第一个玩家的出生位置。";
            case SPAWN_LOCATION_TWO:
                return "决定决斗中第二个玩家的出生位置。";
            case SPECTATE_LOCATION:
                return "决定观战者被传送到哪里观看决斗。";
            case KIT:
                return "决定玩家进入决斗时拥有的物品包, 将直接读取你当前身上的装备。";
            case LOBBY_LOCATION:
                return "决定决斗结束后玩家被传送回的大厅复活点。";
            default:
                return "";
        }
    }

    public static ArenaProperty fromString(String propertyName) {
        try {
            ArenaProperty fromString = ArenaProperty.valueOf(propertyName.toUpperCase());
            return fromString;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public String toFriendlyString() {
        return friendlyString;
    }
}
