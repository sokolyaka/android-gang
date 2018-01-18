package com.sokolov.gang.core.domain;

import android.content.Context;

import com.sokolov.gang.core.entity.IGangClient;

public interface IGangClientBuilder {
    IGangClientBuilder setType(String type);

    IGangClientBuilder setContext(Context context);

    IGangClientBuilder setOnConnectionRequestListener(IGangClient.IConnectionListener onConnectionRequestListener);

    IGangClientBuilder setNetworkId(String networkId);

    IGangClient build();
}
