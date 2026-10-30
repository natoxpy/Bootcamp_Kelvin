package net.ecom.components;

public interface ComponentUpdate<T extends UiComponent<T>> {
    public void update(T component);
}
