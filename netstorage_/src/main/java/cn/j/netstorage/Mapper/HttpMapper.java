package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.plugin.Plugin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HttpMapper extends JpaRepository<Plugin,Long>{
    List<Plugin> findAllByPluginNameContaining(String PluginName);
}
