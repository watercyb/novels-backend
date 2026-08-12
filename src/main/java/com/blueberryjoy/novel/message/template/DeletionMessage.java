package com.blueberryjoy.novel.message.template;

public record DeletionMessage(Type type, String name, String reason) {
    public enum Type {
        NOVEL, CHAPTER, COMMENT, REPLY;

        public static Type getType(String value) {
            if (value == null)
                return null;
            try {
                return Type.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
