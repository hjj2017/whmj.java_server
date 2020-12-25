package org.mj.comm.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 自定义数据源工厂类
 */
public class CustomDataSourceFactory implements DataSourceFactory {
    /**
     * 属性字典
     */
    private final Map<Object, Object> _propMap = new HashMap<>();

    /**
     * 数据源
     */
    private DataSource _dataSource = null;

    @Override
    public void setProperties(Properties props) {
        if (null != props) {
            this._propMap.putAll(props);
        }
    }

    @Override
    public DataSource getDataSource() {
        if (null == this._dataSource) {
            synchronized (CustomDataSourceFactory.class) {
                if (null == this._dataSource) {
                    this._dataSource = this.createDataSource();
                }
            }
        }

        return this._dataSource;
    }

    /**
     * 创建数据源
     *
     * @return 数据源
     */
    private DataSource createDataSource() {
        try {
            return DruidDataSourceFactory.createDataSource(this._propMap);
        } catch (Exception ex) {
            throw new RuntimeException("初始化数据源失败", ex);
        }
    }
}
