package de.btu.monopoly.ui.chat;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class NetworkData {
    
    public enum Type {
        CONNECT, DISCONNECT;
    }
    
    private Type type;
    private Object obj;
    
    public NetworkData(Type type, Object obj) {
        this.type = type;
        this.obj = obj;
    }
    
    public Type getType() {
        return type;
    }
    
    public Object getObject() {
        return obj;
    }
}
