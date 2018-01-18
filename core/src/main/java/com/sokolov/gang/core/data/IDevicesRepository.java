package com.sokolov.gang.core.data;

import com.sokolov.gang.core.entity.IDevice;

import java.util.Collection;

public interface IDevicesRepository {

    IDevice getByAddress(String address);

    IDevice save(IDevice device);

    IDevice delete(String address);

    Collection<IDevice> getAll();

    boolean contains(String address);

}
