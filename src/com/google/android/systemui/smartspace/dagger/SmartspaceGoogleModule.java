package com.google.android.systemui.smartspace.dagger;

import com.android.systemui.CoreStartable;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.BcSmartspaceDataProvider;
import com.google.android.systemui.smartspace.KeyguardSmartspaceStartable;
import com.google.android.systemui.smartspace.WeatherSmartspaceDataProvider;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

import javax.inject.Named;

@Module
public abstract class SmartspaceGoogleModule {
    @Provides
    @Named("dream_smartspace_data_plugin")
    static BcSmartspaceDataPlugin provideDreamBcSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    @Provides
    @Named("dream_weather_smartspace_data_plugin")
    static BcSmartspaceDataPlugin provideDreamWeatherSmartspaceDataPlugin() {
        return new WeatherSmartspaceDataProvider();
    }

    @Provides
    static BcSmartspaceDataPlugin provideBcSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    @Binds
    @IntoMap
    @ClassKey(KeyguardSmartspaceStartable.class)
    abstract CoreStartable bindKeyguardSmartspaceStartable(KeyguardSmartspaceStartable impl);
}
