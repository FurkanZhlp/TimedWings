package me.furkanzhlp.timedwings.adapter;

import me.furkanzhlp.timedwings.TimedWings;
import me.furkanzhlp.timedwings.adapter.adapters.fabledskyblock.FabledSkyblockAdapter;
import me.furkanzhlp.timedwings.manager.Manager;
import org.bukkit.event.HandlerList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterManager extends Manager {

    private final List<TimedWingsAdapter> adapterList;
    private final Map<String,TimedWingsAdapter> registeredAdapters = new HashMap<>();
    public AdapterManager(TimedWings plugin) {
        super(plugin);

        this.adapterList = Arrays.asList(
            new FabledSkyblockAdapter(plugin)
        );

        adapterList.stream().filter(adapter -> adapter.checkPlugin()).forEach(this::registerAdapter);
    }

    public void registerAdapter(TimedWingsAdapter adapter){
        if(!registeredAdapters.containsKey(adapter.getAdapterName())){
            if(!adapter.checkPlugin()) return;
            plugin.logger().info("[Adapter] Registering "+adapter.getPluginName()+" into TimedWings plugin.");
            registeredAdapters.put(adapter.getAdapterName(),adapter);
            adapter.registerAdapter();
        }
    }

    public void unregisterAdapter(TimedWingsAdapter adapter){
        if(registeredAdapters.containsKey(adapter.getAdapterName())){
            registeredAdapters.remove(adapter.getAdapterName());
        }
    }

    public Map<String, TimedWingsAdapter> getRegisteredAdapters() {
        return registeredAdapters;
    }

    public void onDisable(){
        registeredAdapters.values().forEach(this::unregisterAdapter);
    }
}
