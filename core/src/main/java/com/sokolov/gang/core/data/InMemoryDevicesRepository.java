package com.sokolov.gang.core.data;

import com.sokolov.gang.core.entity.IDevice;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDevicesRepository implements IDevicesRepository {

    private final static Map<String, IDevice> MAP = new ConcurrentHashMap<>();

    @Override
    public IDevice getByAddress(String address) {
        return MAP.get(address);
    }

    @Override
    public IDevice save(IDevice device) {
        MAP.put(device.address(), device);
        return device;
    }

    @Override
    public IDevice delete(String address) {
        return MAP.remove(address);
    }

    @Override
    public Collection<IDevice> getAll() {
        return MAP.values();
    }

    @Override
    public boolean contains(String address) {
        return MAP.containsKey(address);
    }
}
