package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.xml.XmlContract;

public class TypeNotPresentException extends Exception {

    private final String message;

    public TypeNotPresentException(String message) {
        this.message = message;
    }

    public TypeNotPresentException(String typeName, XmlContract contract, String... values) {
        StringBuilder msg = new StringBuilder(typeName);
        msg.append(" ").append("[");

        for (int i = 0; i < values.length; i++) {
            msg.append(values[i]);
            if (i != values.length - 1) msg.append(", ");
        }

        msg.append("]").append(" is not registered in ");
        msg.append(contract.getFolderName()).append("/").append(contract.getFileName());
        message = msg.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
