package com.sokolov.gang.core.data;

import com.sokolov.gang.core.entity.IDevice;

import java.util.Collection;

public class NotifyDevicesRepository implements IDevicesRepository {
    private final IDevicesRepository origin;
    private final Listener listener;

    public NotifyDevicesRepository(IDevicesRepository origin, Listener listener) {
        this.origin = origin;
        this.listener = listener;
    }

    @Override
    public IDevice getByAddress(String address) {
        return origin.getByAddress(address);
    }

    @Override
    public IDevice save(IDevice device) {
        IDevice save = origin.save(device);
        listener.onSave(save);
        return save;
    }

    @Override
    public IDevice delete(String address) {
        IDevice delete = origin.delete(address);
        listener.onDelete(delete);
        return delete;
    }

    @Override
    public Collection<IDevice> getAll() {
        return origin.getAll();
    }

    @Override
    public boolean contains(String address) {
        return origin.contains(address);
    }

    public interface Listener {
        void onSave(IDevice device);
        void onDelete(IDevice device);
    }

}
