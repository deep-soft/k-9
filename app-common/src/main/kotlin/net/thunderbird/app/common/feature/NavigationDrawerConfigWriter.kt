package net.thunderbird.app.common.feature

import com.fsck.k9.preferences.DrawerConfigManager
import net.thunderbird.feature.navigation.drawer.api.NavigationDrawerExternalContract
import net.thunderbird.feature.navigation.drawer.api.NavigationDrawerExternalContract.DrawerConfig

internal class NavigationDrawerConfigWriter(
    private val drawerConfigManager: DrawerConfigManager,
) : NavigationDrawerExternalContract.DrawerConfigWriter {
    override fun writeDrawerConfig(drawerConfig: DrawerConfig) {
        drawerConfigManager.save(drawerConfig)
    }
}
