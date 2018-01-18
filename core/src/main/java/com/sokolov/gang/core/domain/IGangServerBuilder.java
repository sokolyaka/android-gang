package com.sokolov.gang.core.domain;

import android.content.Context;

import com.sokolov.gang.core.data.IDevicesRepository;
import com.sokolov.gang.core.entity.IGangServer;

public interface IGangServerBuilder {

    IGangServerBuilder setContext(Context context);

    IGangServerBuilder setNetworkId(String networkId);

    IGangServerBuilder setType(String type);

    IGangServerBuilder setDevicesRepository(IDevicesRepository devicesRepository);

    IGangServer build();
}
